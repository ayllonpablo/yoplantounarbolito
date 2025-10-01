<?php

namespace App\Http\Controllers;

use App\Http\Responses\TokenUserResponse;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    public function login(Request $request) {

        $request->validate([
            "email" => ['required', 'email:rfc,dns'],
            "password" => ['required', 'max:20']
        ]);

        $user = User::where("email", $request->email)->first();

        if (!Hash::check($request->password, optional($user)->password)) {
            throw ValidationException::withMessages([
                '' => [__('auth.failed')]
            ]);
        }
        return new TokenUserResponse($user);
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->noContent();
    }

    public function refresh(Request $request)
    {
        $user = $request->user();
        $user->currentAccessToken()->delete();

        return new TokenUserResponse($user);
    }

    public function me(Request $request)
    {
        $user = $request->user();

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
