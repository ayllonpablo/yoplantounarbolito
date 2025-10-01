<?php

namespace App\Models;

use App\JsonApi\Traits\CamelCasing;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Tree extends Model
{
    use HasFactory, CamelCasing;

    protected $guarded = [];

    public $resourceType = 'trees';

    public function users()
    {
        return $this->belongsToMany(User::class);
    }

    public function actions()
    {
        return $this->hasMany(Action::class);
    }
}
