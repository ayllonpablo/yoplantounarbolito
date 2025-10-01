<?php

namespace App\JsonApi;

use Illuminate\Support\Str;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Support\Arr;

class JsonApiQueryBuilder
{
    public function allowedIncludes()
    {
        return function ($allowedIncludes = []) {

            /** @var Builder $this */
            if (request()->isNotFilled('include')) {
                return $this;
            }
            $includes = explode(',', request('include'));
            foreach ($includes as $include) {
                if (request()->isMethod('POST') || request()->isMethod('PATCH')) {
                    if (in_array($include, $allowedIncludes)) {
                        $this->getModel()->load($include);
                    }
                } else {
                    abort_unless(in_array($include, $allowedIncludes), 400, "Unsupported inclusion {$include} resource");
                    $this->with($include);
                }
            }
            if (request()->isMethod('POST') || request()->isMethod('PATCH')) {
                return $this->getModel();
            } else {
                return $this;
            }
        };
    }

    public function allowedSorts()
    {
        return  function ($allowedSorts) {
            /** @var Builder $this */
            if (request()->filled('sort')) {
                $sortFields = explode(',', request('sort'));
                foreach ($sortFields as $sortField) {
                    if (Str::contains($sortField, '.')) {
                        $sortDirection = Str::of($sortField)->startsWith('-') ? 'desc' : 'asc';
                        $sortField = ltrim($sortField, '-');

                        abort_unless(in_array($sortField, $allowedSorts), 400, "Unsupported sort by {$sortField} field");

                        $nestedSort = explode('.', $sortField);
                        $relatedResourceKey = $nestedSort[0];
                        $relatedSortField = $nestedSort[1];

                        abort_unless(
                            isset($this->getModel()->relationshipTypes) && array_key_exists($relatedResourceKey, $this->getModel()->relationshipTypes)
                            , 400, "Unsupported sort by {$sortField} field, not found relationshipType"
                        );

                        $relationshipType = $this->getModel()->relationshipTypes[$relatedResourceKey];
                        $relatedModelName = Str::ucfirst(Str::singular($relatedResourceKey));
                        $relatedModel = "App\\Models\\{$relatedModelName}";
                        $relatedModelInstance = new $relatedModel();
                        $relatedTableName = $relatedModelInstance->getTable();

                        abort_unless(
                            $relationshipType === 'belongsTo' || $relationshipType === 'hasOne',
                            400,
                            "Unsupported sort {$relationshipType} relationship type"
                        );

                        if ($relationshipType === 'belongsTo') {
                            $relatedRouteKeyName = $relatedModelInstance->getRouteKeyName();
                            if (isset($this->getModel()->foreingKeyNames) && array_key_exists($relatedResourceKey, $this->getModel()->foreingKeyNames)) {
                                $foreingKeyName = $this->getModel()->foreingKeyNames[$relatedResourceKey];
                            } else {
                                $foreingKeyName = $relatedModelInstance->getForeignKey();
                            }
                            $params = [
                                "{$relatedTableName}.{$relatedRouteKeyName}",
                                "{$this->getModel()->getTable()}.{$foreingKeyName}"
                            ];
                        } else {
                            $routekeyName = $this->getModel()->getRouteKeyName();
                            $resourcerKey = Str::singular($this->getResourceType());

                            if (isset($relatedModelInstance->foreingKeyNames) && array_key_exists($resourcerKey, $relatedModelInstance->foreingKeyNames)) {
                                $relatedForingKeyName = $relatedModelInstance->foreingKeyNames[$resourcerKey];
                            } else {
                                $relatedForingKeyName = $this->getModel()->getForeignKey();
                            }
                            $params = [
                                "{$relatedTableName}.{$relatedForingKeyName}",
                                "{$this->getModel()->getTable()}.{$routekeyName}"
                            ];
                        }

                        $this->orderBy(
                            $relatedModel::withoutGlobalScopes()->select(Str::snake($relatedSortField))
                                ->whereColumn($params[0], $params[1]),
                            $sortDirection
                        );
                    } else {
                        $sortDirection = Str::of($sortField)->startsWith('-') ? 'desc' : 'asc';
                        $sortField = ltrim($sortField, '-');
                        abort_unless(in_array($sortField, $allowedSorts), 400, "Unsupported sort by {$sortField} field");

                        $this->orderBy(Str::snake($sortField), $sortDirection);
                    }

                }
            }
            return $this;
        };
    }

    public function jsonPaginate()
    {
        return function () {
            /** @var Builder $this */
            // if (request()->filled('page')) {
            return $this->paginate(
                $perPage = request('page.size', null),
                $columns = ['*'],
                $pageName = 'page[number]',
                $page = request('page.number', null)
            )->appends(request()->only('sort', 'filter', 'page.size'));
            // }
            // return $this;
        };
    }

    public function allowedFilters()
    {
        return function ($allowedFilters) {
            /** @var Builder $this */
            //no se verifica que exista el queryParam filter, si no existe se devolvera un array vacio
            $filters = [];
            // como llegan los filters
            // dump(request('filter', []));
            $allowedOperatorsByOperatorType = [
                'logical' => ['and', 'or'],
                'comparison' => [
                    'all' => ['eq', 'ne', 'gt', 'ge', 'lt', 'le', 'contains', 'containsora'],
                    'routeKey' => ['eq', 'ne']
                ],
                'relation' => [
                    'withValue' => ['has', 'dnh'],
                    'withoutValue' => ['has', 'doesnthave']
                ]
            ];

            $allowedOperatorsByFilterType = [
                'field' => [
                    // explode -
                    1 => [
                        'allowed' => fn ($op) => [
                            'keys' => ['comparison.all'], // eq..
                            'operators' => ['comparison' => ['logical' => 'and', 'operator' => $op[0]]]
                        ]
                    ],
                    2 => [
                        'allowed' => fn ($op) => [
                            'keys' => ['logical', 'comparison.all'], // and-eq, or-eq
                            'operators' => ['comparison' => ['logical' => $op[0], 'operator' => $op[1]]]
                        ]
                    ]
                ],
                'routeKey' => [
                    // explode .
                    1 => [
                        [   // explode -
                            1 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['comparison.routeKey,relation.withoutValue'], // eq, has, doesnthave
                                    'operators' => [
                                        'relation' => ['logical' => 'and', 'operator' => 'has'],
                                        'comparison' => ['operator' => $op[0]]
                                    ]
                                ]
                            ],
                            2 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['logical', 'relation.withoutValue'], // and-has, and-doesnthave, or-has, or-doesnthave
                                    'operators' => [
                                        'relation' => ['logical' => $op[0], 'operator' => $op[1]],
                                    ]
                                ]
                            ]
                        ]
                    ],
                    2 => [ //explode .
                        [   // explode -
                            1 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['relation.withValue'], // has, dnh
                                    'operators' => [
                                        'relation' => ['logical' => 'and', 'operator' => ($op[0] === 'dnh' ? 'doesnthave' : $op[0])],
                                    ]
                                ]
                            ],
                            2 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['logical', 'relation.withValue'], // and-has, and-dnh, or-has, or-dnh
                                    'operators' => [
                                        'relation' => ['logical' => $op[0], 'operator' => ($op[1] === 'dnh' ? 'doesnthave' : $op[1])],
                                    ]
                                ]
                            ]
                        ],
                        [
                            1 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['comparison.routeKey'], // eq, ne
                                    'operators' => [
                                        'comparison' => ['operator' => $op[0]]
                                    ]
                                ]
                            ]
                        ]
                    ]
                ],
                'nestedField' => [
                    // explode .
                    1 => [
                        [   // explode -
                            1 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['comparison.all'], // eq, ne, ...
                                    'operators' => [
                                        'relation' => ['logical' => 'and', 'operator' => 'has'],
                                        'comparison' => ['logical' => 'and', 'operator' => $op[0]],
                                    ]
                                ]
                            ],
                            2 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['logical', 'comparison.all'], // and-eq, or-eq, and-ne, or-ne, ...
                                    'operators' => [
                                        'relation' => ['logical' => 'and', 'operator' => 'has'],
                                        'comparison' => ['logical' => $op[0], 'operator' => $op[1]],
                                    ]
                                ]
                            ]
                        ]
                    ],
                    2 => [ //explode .
                        [   // explode -
                            1 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['relation.withValue'], // has, dnh
                                    'operators' => [
                                        'relation' => ['logical' => 'and', 'operator' => ($op[0] === 'dnh' ? 'doesnthave' : $op[0])],
                                    ]
                                ]
                            ],
                            2 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['logical', 'relation.withValue'], // and-has, and-dnh, or-has, or-dnh
                                    'operators' => [
                                        'relation' => ['logical' => $op[0], 'operator' => ($op[1] === 'dnh' ? 'doesnthave' : $op[1])],
                                    ]
                                ]
                            ]
                        ],
                        [
                            1 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['comparison.all'], // eq, ne, ...
                                    'operators' => [
                                        'comparison' => ['logical' => 'and', 'operator' => $op[0]],
                                    ]
                                ]
                            ],
                            2 => [
                                'allowed' => fn ($op) => [
                                    'keys' => ['logical', 'comparison.all'], // and-eq, and-ne, or-eq, or-ne, ...
                                    'operators' => [
                                        'comparison' => ['logical' => $op[0], 'operator' => $op[1]],
                                    ]
                                ]
                            ]
                        ]
                    ],
                ]
            ];

            $validateFilter = function ($filter) use ($allowedFilters) {
                abort_unless(in_array($filter, $allowedFilters), 400, "Unsupported filter by {$filter}");
            };

            $validateOperator = function ($filterType, $operatorsString) use ($allowedOperatorsByOperatorType, $allowedOperatorsByFilterType) {
                $response = [];
                if ($filterType === 'field') {
                    $operators = explode('-', $operatorsString);
                    $operatorsCount = count($operators);;
                    $allowed = $allowedOperatorsByFilterType['field'][$operatorsCount]['allowed']($operators);
                    $response = $allowed['operators'];
                    foreach ($allowed['keys'] as $index => $allowedKey) {
                        $allowedOperators = Arr::get($allowedOperatorsByOperatorType, $allowedKey);
                        $validated = in_array($operators[$index], $allowedOperators);
                        abort_unless($validated, 400, "Unsupported filter with {$operatorsString} operator");
                    }
                } elseif ($filterType === 'routeKey' || $filterType === 'nestedField') {
                    $operators = explode('.', $operatorsString);
                    $operatorsCount = count($operators);
                    foreach ($operators as $index1 => $operator) {
                        $subOperators = explode('-', $operator);
                        $subOperatorsCount = count($subOperators);
                        $allowed = $allowedOperatorsByFilterType[$filterType][$operatorsCount][$index1][$subOperatorsCount]['allowed']($subOperators);
                        $response = array_merge($response, $allowed['operators']);
                        foreach ($allowed['keys'] as $index2 => $allowedKey) {
                            if (Str::contains($allowedKey, ',')) {
                                foreach (explode(',', $allowedKey) as $subAllowedKey) {
                                    $subAllowedOperators = Arr::get($allowedOperatorsByOperatorType, $subAllowedKey);
                                    $validated = in_array($subOperators[$index2], $subAllowedOperators);
                                    if ($validated) {
                                        if (in_array($response['comparison']['operator'], $allowedOperatorsByOperatorType['relation']['withoutValue'])) {
                                            // dump('dddddddd');
                                            $response['relation']['operator'] = $response['comparison']['operator'];
                                            unset($response['comparison']);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                $allowedOperators = Arr::get($allowedOperatorsByOperatorType, $allowedKey);
                                $validated = in_array($subOperators[$index2], $allowedOperators);
                            }
                            // dump($validated);
                            abort_unless($validated, 400, "Unsupported filter with {$operatorsString} operator");
                        }
                    }
                }
                return $response;
            };

            $formatOperatorValue = function ($filterType, $value) use ($validateOperator) {
                $response = [];
                if (is_array($value)) { // contiene operators
                    $operatorsWithValues = $value;
                    $operators = '';
                    foreach ($operatorsWithValues as $op => $val) { // solo se itera una vez, deberia
                        $operators = $op;
                        $value = $val;
                    }
                } else {
                    // defaul operators
                    if ($filterType === 'nestedField' || $filterType === 'field') {
                        $operators = 'contains';
                    } else {
                        $operators = 'eq';
                    }
                }
                if ($filterType === 'routeKey') {
                    $value = explode(',', $value);
                }
                if ($filterType === 'field') {
                    $value = Str::contains($value, ',') ? explode(',', $value) : $value;
                }
                $response = [
                    'value' => $value,
                    'operators' => $validateOperator($filterType, $operators)
                ];
                return $response;
            };

            $formatFilterResponse = function ($type, $filter, $value, $field = '') use ($formatOperatorValue) {
                $response = [
                    'type' => $type,
                    'filter' => $filter,
                    // 'value' => $value
                ];
                // dump($value);
                $operatorValue = $formatOperatorValue($type, $value);
                $response['value'] = $operatorValue['value'];
                isset($operatorValue['operators']) ? $response['operators'] = $operatorValue['operators'] : '';
                empty($field) ? '' : $response['field'] = $field;
                return $response;
            };


            $groupFilters = function (Builder $queryBuilder, &$filters, $filter, $value) use ($validateFilter, $formatFilterResponse) {
                ($value['type'] ?? null) === 'groupAndOr' ? null : $validateFilter($filter); // validas todos excepto las agrupaciones and, or
                if (Str::contains($filter, '.')) {
                    $nestedFilter = explode('.', $filter);
                    $relationshipKey = $nestedFilter[0];
                    $relationshipField = Str::snake($nestedFilter[1]);

                    $filters[$relationshipKey]['type'] = 'relationships';
                    $filters[$relationshipKey]['filters'][] = $formatFilterResponse('nestedField', $filter, $value, $relationshipField);
                } else {
                    if (array_key_exists($filter, $filters)) {
                        $relationshipKey = $filter;
                        $filters[$relationshipKey]['filters'][] = $formatFilterResponse('routeKey', $filter, $value);
                    } else {
                        if (method_exists($queryBuilder->getModel(), $filter)) {
                            $relationshipKey = $filter;
                            $filters[$relationshipKey]['type'] = 'relationships';
                            if (is_array($value)) {// separate filter routeKey
                                foreach ($value as $key => $val) {
                                    $filters[$relationshipKey]['filters'][] = $formatFilterResponse('routeKey', $filter, [$key => $val]);
                                }
                            } else {
                                $filters[$relationshipKey]['filters'][] = $formatFilterResponse('routeKey', $filter, $value);
                            }
                        } else {
                            $valueType = $value['type'] ?? null;
                            if ($valueType === 'groupAndOr') {
                                $filters[$filter] = $value;
                            } else {
                                $filters[$filter]['type'] = 'field';
                                if (is_array($value)) { // separate filter field
                                    foreach ($value as $key => $val) {
                                        $filters[$filter]['filters'][] = $formatFilterResponse('field', $filter, [$key => $val]);
                                    }
                                } else {
                                    $filters[$filter]['filters'][] = $formatFilterResponse('field', $filter, $value);
                                }
                            }
                        }
                    }
                }
            };

            foreach (request('filter', []) as $filter => $value) {
                $queryBuilder = $this;
                if (in_array(Str::before($filter, '-'), $allowedOperatorsByOperatorType['logical'])) {
                    $subFilters = [];
                    foreach ($value as $subFilter => $subValue) {
                        $groupFilters($queryBuilder, $subFilters, $subFilter, $subValue);
                    }
                    $value = [
                        'type' => 'groupAndOr',
                        'whereClause' => Str::before($filter, '-') === 'or' ? 'orWhere' : 'where',
                        'filters' => $subFilters
                    ];
                }
                $groupFilters($queryBuilder, $filters, $filter, $value);
            }

            // filter formateados
            // dump($filters);

            foreach ($filters as $filter => $value) {
                $type = $value['type'] ?? '';

                if ($type === 'groupAndOr') {
                    $groupFilters = $value['filters'];
                    $queryBuilder = $this;
                    $this->{$value['whereClause']}(function ($query) use ($groupFilters) {
                        foreach ($groupFilters as $filter => $value) {
                            $type = $value['type'] ?? '';
                            if ($type === 'field') {
                                $this->filterByField($query, $filter, $value['filters']);
                            } elseif ($type === 'relationships') {
                                $this->filterByRelationship($query, $filter, $value['filters']);
                            }
                        }
                    });
                } elseif($type === 'relationships') {
                    $this->filterByRelationship($this, $filter, $value['filters']);
                } elseif($type === 'field') {
                    $this->filterByField($this, $filter, $value['filters']);
                }
            }
            return $this;
        };
    }

    public function sparseFieldset()
    {
        return function () {
            /** @var Builder $this */
            if (request()->isNotFilled('fields')) {
                return $this;
            }

            $fields = array_filter(explode(',', request('fields.' . $this->getResourceType())));
            $fields = array_map(fn ($field) => Str::snake($field), $fields);

            $routeKeyName = $this->model->getRouteKeyName();
            if (!in_array($routeKeyName, $fields)) {
                $fields[] = $routeKeyName;
            }

            return $this->addSelect($fields);
        };
    }

    public function allowLimit()
    {
        return function () {
            /** @var Builder $this */
            if (request()->isNotFilled('limit')) {
                return $this;
            }

            $limit = request('limit');
            if ($limit > 0) {
                $this->limit($limit);
            }

            return $this;
        };
    }

    public function getResourceType()
    {
        return function () {
            /** @var Builder $this */
            return property_exists($this->model, 'resourceType') ? $this->model->resourceType : $this->model->getTable();
        };
    }

    public function filterByField()
    {
        return function (Builder &$query, $filterKey, $filters) {
            if ($query->hasNamedScope($filterKey)) {
                $query->{$filterKey}($filterKey, $filters);
            } else {
                $comparisonOperators = [
                    'containsora' => 'LIKE',
                    'contains' => 'ILIKE',
                    'eq' => '=',
                    'ne' => '<>',
                    'gt' => '>',
                    'ge' => '>=',
                    'lt' => '<',
                    'le' => '<='
                ];
                $logicalOperators = [
                    'and' => 'where',
                    'or' => 'orWhere'
                ];
                foreach ($filters as $filter) {
                    $whereClause = $logicalOperators[$filter['operators']['comparison']['logical']];
                    $field = Str::snake($filter['filter']);
                    $comparisonOp = $comparisonOperators[$filter['operators']['comparison']['operator']];
                    $value = $filter['value'];
                    if (is_array($value)) {
                        $values = $value;
                        $query->{$whereClause}(function ($q) use ($values, $field, $comparisonOp){
                            foreach ($values as $key => $value) {
                                $subWhereClause = $key === 0 ? 'where' : 'orWhere';
                                $q->{$subWhereClause}($field, $comparisonOp, $value);
                            }
                        });
                        // $query->{$whereClause}($field, $comparisonOp, $value);
                    } else {
                        // $value = $comparisonOp === 'ILIKE' ? "%{$value}%" : $value;
                        $value = ($comparisonOp === 'ILIKE') ? "%{$value}%" : $value;

                        $value = $comparisonOp === 'LIKE' ? '%'.Str::upper($value).'%': $value;
                        $query->{$whereClause}($field, $comparisonOp, $value);
                    }
                }
            }
        };
    }

    public function filterByRelationship()
    {
        return function(Builder &$query, $relationshipKey, $filters) {
            if ($query->hasNamedScope($relationshipKey)) {
                $query->{$relationshipKey}($relationshipKey, $filters);
            } else {
                $groupFilters = [];
                $comparisonOperatorsByType = [
                    'nestedField' => [
                        'contains' => 'ILIKE',
                        'eq' => '=',
                        'ne' => '<>',
                        'gt' => '>',
                        'ge' => '>=',
                        'lt' => '<',
                        'le' => '<='
                    ],
                    'routeKey' => [
                        'eq' => 'whereIn',
                        'ne' => 'whereNotIn'
                    ]
                ];
                $relationLogicalOperators = [
                    'has' => [
                        'and' => 'whereHas',
                        'or' => 'orWhereHas'
                    ],
                    'doesnthave' => [
                        'and' => 'whereDoesntHave',
                        'or' => 'orWhereDoesntHave'
                    ],
                ];
                $logicalOperators = [
                    'and' => 'where',
                    'or' => 'orWhere'
                ];
                foreach ($filters as $filter) {
                    if (isset($filter['operators']['comparison'])) { // excluir has, doesnthave (withoutValue)
                        $groupFilterKey = $filter['operators']['relation']['logical'] . $filter['operators']['relation']['operator'];
                        $groupFilters[$groupFilterKey]['operators']['relation'] = $filter['operators']['relation'];
                        unset($filter['operators']['relation']);
                        $groupFilters[$groupFilterKey]['subfilters'][] = $filter;
                    } else {
                        $groupFilters[] = $filter;
                    }
                }

                foreach ($groupFilters as $filter) {
                    $relationOperator = $filter['operators']['relation']['operator'];
                    $relationLogicalOperator = $filter['operators']['relation']['logical'];
                    $relationLogicalWhereClause = $relationLogicalOperators[$relationOperator][$relationLogicalOperator];
                    $query->{$relationLogicalWhereClause}($relationshipKey, function (Builder $q) use ($query, $filter, $logicalOperators, $comparisonOperatorsByType, $relationshipKey) {
                        if (!empty($filter['subfilters'])) {
                            foreach ($filter['subfilters'] as $subFilter) {
                                if ($subFilter['type'] === 'routeKey') {
                                    $relatedModelName = Str::ucfirst(Str::singular($relationshipKey));
                                    $relatedModel = "App\\Models\\{$relatedModelName}";
                                    $relatedRouteKeyName = (new $relatedModel())->getRouteKeyName();
                                    $manyToManyRelationshipRouteKeyNames = $query->getModel()->manyToManyRelationshipRouteKeyNames;
                                    if (is_array($manyToManyRelationshipRouteKeyNames) && array_key_exists($relationshipKey, $manyToManyRelationshipRouteKeyNames)) {
                                        $relatedRouteKeyName = $manyToManyRelationshipRouteKeyNames[$relationshipKey];
                                    }
                                    $ids = $subFilter['value'];
                                    $comparisonWhereClause = $comparisonOperatorsByType['routeKey'][$subFilter['operators']['comparison']['operator']];
                                    $q->{$comparisonWhereClause}($relatedRouteKeyName, $ids);
                                } elseif ($subFilter['type'] === 'nestedField') {
                                    $whereClause = $logicalOperators[$subFilter['operators']['comparison']['logical']];
                                    $field = $subFilter['field'];
                                    $comparisonOp = $comparisonOperatorsByType['nestedField'][$subFilter['operators']['comparison']['operator']];
                                    $value = $comparisonOp === 'ILIKE' ? "%{$subFilter['value']}%" : $subFilter['value'];
                                    $q->{$whereClause}($field, $comparisonOp, $value);
                                }
                            }
                        }
                    });
                }
            }
        };
    }

}
