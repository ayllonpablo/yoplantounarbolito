<?php

namespace App\JsonApi\Traits;

use Illuminate\Support\Str;

trait HasRelationships
{
    public function validated()
    {
        $data = parent::validated()['data'];
        $attributes = $data['attributes'] ?? [];
        if (isset($data['relationships'])) {
            $relationships = $data['relationships'];
            foreach ($relationships as $key => $relationship) {
                if ($this->isSingularRelationshipKey($key)) {
                    $attributes = array_merge($attributes, $this->{$key}($relationship));
                } else {
                    $attributes['_relationships'][$key] = $relationship['data'];
                }
            }
        }
        return $attributes;
    }

    private function isSingularRelationshipKey($relationshipKey)
    {
        $singularRelationshipKey = Str::singular($relationshipKey);
        return $singularRelationshipKey === $relationshipKey;
    }
}
