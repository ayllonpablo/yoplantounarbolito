<?php

namespace App\Http\Controllers;

use App\Http\Responses\TokenUserResponse;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;

class RegisterController extends Controller
{
    public function store(Request $request)
    {
        $validated = Validator::make($request->all(), [
            'firstname' => ['required', 'string', 'min:5', 'max:100'],
            'lastname' => ['required', 'string', 'min:5', 'max:100'],
            'email' => ['required', 'email:rfc,dns', 'unique:users,email'],
            'age' => ['required', 'integer', 'min:1'],
            'organization' => ['nullable', 'string', 'min:5', 'max:50'],
            'phone' => ['required', 'integer', 'digits_between:6,10'],
            'password' => ['required', 'confirmed', 'min:8', 'max:20']
        ], [], [
            'firstname' => 'nombres',
            'lastname' => 'apellidos',
            'email' => 'correo electrónico',
            'age' => 'edad',
            'organization' => 'institución',
            'phone' => 'teléfono',
            'password' => 'contraseña'
        ])->validate();

        $validated['password'] = Hash::make($validated['password']);
        $user = User::create($validated);

        return new TokenUserResponse($user);
    }

    public function update(Request $request)
    {
        $user = $request->user();

        $validated = Validator::make($request->all(), [
            'firstname' => ['nullable', 'string', 'min:5', 'max:100'],
            'lastname' => ['nullable', 'string', 'min:5', 'max:100'],
            'email' => ['nullable', 'email:rfc,dns', Rule::unique('users', 'email')->ignore($user->id)],
            'age' => ['integer', 'min:1'],
            'organization' => ['nullable', 'string', 'min:5', 'max:50'],
            'phone' => ['nullable', 'integer', 'digits_between:6,10'],
        ], [], [
            'firstname' => 'nombres',
            'lastname' => 'apellidos',
            'email' => 'correo electrónico',
            'age' => 'edad',
            'organization' => 'institución',
            'phone' => 'teléfono'
        ])->validate();

        $user->update($validated);

        return response()->json($user->only([
            'id',
            'firstname',
            'lastname',
            'email',
            'age',
            'organization',
            'phone'
        ]));
    }
}
