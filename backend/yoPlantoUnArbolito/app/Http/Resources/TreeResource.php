<?php

namespace App\Http\Resources;

use Illuminate\Http\Resources\Json\JsonResource;

class TreeResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return array|\Illuminate\Contracts\Support\Arrayable|\JsonSerializable
     */
    public function toArray($request)
    {
        return [
            'id' => $this->id,
            'name' => $this->name,
            'lat' => $this->lat,
            'lng' => $this->lng,
            'avatar' => $this->avatar,
            'pathPhoto' => $this->pathPhoto,
            'state' => $this->state,
            $this->mergeWhen(isset($this->pivot), [
                'pivot' => [
                    'points' => optional($this->pivot)->points,
                    'userId' => optional($this->pivot)->user_id,
                    'treeId' => optional($this->pivot)->tree_id
                ]
            ]),
        ];
    }
}
