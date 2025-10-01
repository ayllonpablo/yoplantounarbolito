<?php

namespace App\JsonApi\Traits;

use App\JsonApi\Document;
use Illuminate\Http\Resources\Json\JsonResource;
use Illuminate\Support\Collection;
use Illuminate\Support\Str;

trait JsonApiResource
{
    abstract function toJsonApiAttributes(): array;
    public function toArray($request): array
    {
        $relations = [];
        if ($request->filled('include')) {
            $included = [];
            $relations = array_filter($this->getRelationsWithApiResourceInstances());
            foreach ($relations as $include) {
                if ($include instanceof Collection) {
                    foreach ($include as $resource) {
                        $included[] = $resource;
                    }
                } elseif (isset($include->resource)) {
                    // include as resource
                    $included[] = $include;
                }
            }
            $this->with['included'] = $included;
        }

        return Document::type($this->resource->getResourceType())
        ->id($this->resource->getRouteKey())
        ->attributes($this->filterAttributes($this->toJsonApiAttributes()))
        ->relationshipsData(array_intersect_key($relations, array_flip($this->getRelationshipsLinks())))
        ->relationshipsLinks($this->getRelationshipsLinks())
        ->links([
            'self' => route("{$this->resource->getResourceType()}.show", $this->resource)
        ])
        ->get('data');
    }

    public static function related($resource, string $related)
    {
        $realtedResourceName = Str::ucfirst(Str::singular($related));
        $apiResource = "App\\Http\\Resources\\{$realtedResourceName}Resource";

        if (($resource->{$related} instanceof Collection)) {
            return $apiResource::collection($resource->{$related});
        } else {
            $data = null;
            if (isset($resource->{$related})) {
                $relatedResource = $apiResource::make($resource->{$related});
                $data =  Document::type($relatedResource->getResourceType())
                    ->id($relatedResource->getRouteKey())
                    ->attributes($relatedResource->filterAttributes($relatedResource->toJsonApiAttributes()))
                    ->links([
                        'self' => route("{$relatedResource->getResourceType()}.show", $relatedResource->resource)
                    ])->get('data');
            }

            return [
                'data' => $data,
                'links' => [
                    'self' => route("{$resource->getResourceType()}.{$related}", $resource)
                ]
            ];
        }
    }

    public function getIncludes(): array
    {
        return [];
    }

    public function getRelationshipsLinks(): array
    {
        return [];
    }

    public function withResponse($request, $response)
    {
        $response->header(
            'Location',
            route("{$this->getResourceType()}.show", $this->resource)
        );
    }

    public function filterAttributes(array $attributes): array
    {
        return array_filter($attributes, function ($value) {
            if (request()->isNotFilled('fields')) {
                return true;
            }
            // solo para verificar si id ya se encuentra en los fields solicitados
            $fields = explode(',', request('fields.' . $this->getResourceType()));
            if ($value === $this->getRouteKey()) {
                return in_array($this->getRouteKeyName(), $fields);
            }

            return $value;
        });
    }

    public static function collection($resources)
    {
        $collention = parent::collection($resources);

        if (request()->filled('include')) {
            $included = [];
            foreach ($collention as $resource) {
                // dump($resource->getResourceType());
                foreach (array_filter($resource->getRelationsWithApiResourceInstances()) as $include) {
                    if ($include instanceof Collection) {
                        foreach ($include as $resource) {
                            $included["{$resource->getResourceType()}-{$resource->getRouteKey()}"] = $resource;
                        }
                    } elseif (isset($include->resource))  {
                        // include as resource
                        $included["{$include->getResourceType()}-{$include->getRouteKey()}"] = $include;
                    }
                }
            }
            $collention->with['included'] = array_values($included);
        }

        $collention->with['links'] = ['self' => request()->url()];
        return $collention;
    }

    public function getRelationship(string $relationshipKey)
    {
        return Document::type($this->resource->getResourceType())
            ->id($this->resource->getRouteKey())
            ->attributes($this->filterAttributes($this->toJsonApiAttributes()))
            ->relationshipsData(array_filter($this->getRelationsWithApiResourceInstances()))
            ->relationshipsLinks($this->getRelationshipsLinks())
            ->links([
                'self' => route("{$this->resource->getResourceType()}.show", $this->resource)
            ])
            ->getRelationship($relationshipKey);
    }

    private function getRelationsWithApiResourceInstances(): array
    {
        // dump($this->getIncludes());
        return collect($this->getIncludes())->mapWithKeys(function ($relation) {
            $response = [];
            $isNestedRelationship = count(explode('.', $relation)) === 2;
            if ($isNestedRelationship) {
                $relationName = explode('.', $relation)[0];
                if ($this->relationLoaded($relationName) && isset($this->{$relationName})) {
                    $relatedResource = $this->{$relationName};
                    $nestedRelationName = explode('.', $relation)[1];
                    if ($relatedResource instanceof Collection) {
                        // nested, many to one / many to many
                        $response = $relatedResource->mapWithKeys(
                            fn ($resource) => $this->generateApiResourceRelation($resource, $nestedRelationName, true)
                        );
                    } else {
                        // nested, one to one / one to many
                        $response = $this->generateApiResourceRelation($relatedResource, $nestedRelationName, true);
                    }
                }
            } else {
                // dump($relation);
                $response = $this->generateApiResourceRelation($this, $relation);
            }
            // dump('==========');
            return $response;
        })->all();
    }

    private function generateApiResourceRelation($resource, $relationName, $nested = false)
    {
        $response = [];
        if ($resource->relationLoaded($relationName)) {
            $relatedResourceName = Str::ucfirst(Str::singular($relationName));
            $relatedApiResource = "App\\Http\\Resources\\{$relatedResourceName}Resource";
            if ($resource->{$relationName} instanceof Collection) {
                if ($nested) {
                    // nested, one to many or many to many
                    $response = $resource->{$relationName}->mapWithKeys(
                        fn ($resource) => [
                            Str::singular($relationName). $resource->getRouteKey() => $relatedApiResource::make($resource)
                        ]
                    );
                } else {
                    $response = [
                        $relationName => $resource->{$relationName}->map(fn ($resource) => $relatedApiResource::make($resource))
                    ];
                }
            } else {
                if (isset($resource->{$relationName})) {
                    if ($nested) {
                        // nested, one to one
                        $response = [
                            $relationName. $resource->{$relationName}->getRouteKey() =>  $relatedApiResource::make($resource->{$relationName})
                        ];
                    } else {
                        $response = [
                            $relationName =>  $relatedApiResource::make($resource->{$relationName})
                        ];
                    }
                } else {
                    $response = [ $relationName =>  new JsonResource(null) ];
                }
            }
        }
        return $response;
    }
}
