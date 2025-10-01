<?php

namespace App\Models;

use App\JsonApi\Traits\CamelCasing;
use App\Models\Traits\HasApiTokensWithExpiration;
use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokensWithExpiration, HasFactory, CamelCasing;

    protected $guarded = [];

    public $resourceType = 'users';

    public function trees()
    {
        return $this->belongsToMany(Tree::class)->withPivot('points');
    }

    public function actions()
    {
        return $this->hasMany(Action::class);
    }

    public function devices()
    {
        return $this->hasMany(Device::class);
    }
}
