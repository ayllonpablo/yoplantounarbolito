<?php

namespace App\Models;

use App\JsonApi\Traits\CamelCasing;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Action extends Model
{
    use HasFactory, CamelCasing;

    protected $guarded = [];

    public $resourceType = 'actions';

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function tree()
    {
        return $this->belongsTo(Tree::class);
    }
}
