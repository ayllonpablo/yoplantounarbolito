<?php

namespace App\Http\Controllers;

use App\Http\Requests\StoreUserRequest;
use App\Http\Resources\UserResource;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class UserController extends Controller
{
    public function index()
    {
        $users = User::query()
            ->allowedIncludes(['trees'])
            ->allowedFilters([])
            ->allowedSorts(['points'])
            ->sparseFieldset()
            ->allowLimit();

        return UserResource::collection($users->get());
    }

    public function show($id)
    {
        $user = User::where('id', $id)
            ->allowedIncludes(['trees'])
            ->firstOrFail();

        return UserResource::make($user);
    }

}
