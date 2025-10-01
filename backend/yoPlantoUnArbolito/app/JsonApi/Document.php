<?php

namespace App\JsonApi;

use Illuminate\Support\Collection;

class Document extends Collection
{
    public static function type(string $type): Document
    {
        return new self([
            'data' => [
                'type' => $type 
            ]
        ]);
    }

    public function id($id): Document
    {
        if ($id) {
            $this->items['data']['id'] = (string) $id;
        }
        return $this;
    }

    public function attributes(array $attributes): Document
    {
        $this->items['data']['attributes'] = $attributes;
        return $this;
    }

    public function links(array $links): Document
    {
        $this->items['data']['links'] = $links;
        return $this;
    }

    public function relationshipsData(array $relationships): Document
    {
        foreach ($relationships as $key => $resource) {
            if ($resource instanceof Collection) {
                $this->items['data']['relationships'][$key]['data'] = [];
                $resource->map(function($item) use($key) {
                    $this->items['data']['relationships'][$key]['data'][] = [
                        'type' => $item->getResourceType(),
                        'id' => (string) $item->getRouteKey()
                    ];
                });
            } else {
                if (is_null($resource->resource)) {
                    $this->items['data']['relationships'][$key]['data'] = null;
                } else {
                    $this->items['data']['relationships'][$key]['data'] = [
                        'type' => $resource->getResourceType(),
                        'id' => (string) $resource->getRouteKey()
                    ];
                }
            }  
        }
        return $this;
    }

    public function relationshipsLinks(array $resourceKeys): Document
    {
        foreach ($resourceKeys as $key) {
            $this->items['data']['relationships'][$key]['links'] = [
                'self' => route("{$this->items['data']['type']}.relationships.{$key}", $this->items['data']['id']),
                'related' => route("{$this->items['data']['type']}.{$key}", $this->items['data']['id']),
            ];
        }
        return $this;
    }
    
    public function getRelationship(string $relationshipKey): Array
    {
        return $this->items['data']['relationships'][$relationshipKey];
    }
}
