<?php

namespace App\Models;

use App\JsonApi\Traits\CamelCasing;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Device extends Model
{
    use HasFactory, CamelCasing;

    protected $guarded = [];

    public $resourceType = 'devices';

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
