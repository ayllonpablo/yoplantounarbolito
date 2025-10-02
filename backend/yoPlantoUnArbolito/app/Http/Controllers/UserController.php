<?php

namespace App\Http\Controllers;

use App\Http\Requests\StoreUserRequest;
use App\Http\Resources\UserResource;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class UserController extends Controller
{
    public function index(Request $request)
    {
        $query = User::query();

        // Handle includes
        if ($request->has('include') && $request->input('include') === 'trees') {
            $query->with('trees');
        }

        // Handle sorting
        if ($request->has('sort')) {
            $sortField = ltrim($request->input('sort'), '-');
            $sortDirection = str_starts_with($request->input('sort'), '-') ? 'desc' : 'asc';
            $query->orderBy($sortField, $sortDirection);
        }

        $users = $query->get();

        return UserResource::collection($users);
    }

    public function show(Request $request, $id)
    {
        $query = User::where('id', $id);

        // Handle includes
        if ($request->has('include') && $request->input('include') === 'trees') {
            $query->with('trees');
        }

        $user = $query->firstOrFail();

        return UserResource::make($user);
    }

}
