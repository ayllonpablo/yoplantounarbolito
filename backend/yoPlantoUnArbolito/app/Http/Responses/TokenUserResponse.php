<?php

namespace App\Http\Responses;

use App\Models\User;
use Illuminate\Contracts\Support\Responsable;

class TokenUserResponse implements Responsable
{
    private $user;

    public function __construct(User $user)
    {
        $this->user = $user;
    }

    public function toResponse($request)
    {
        $email = $this->user->email;
        // $expiredAt = now()->addMinutes(60);
        $expiredAt = null;
        return response()->json([
            'accessToken' => $this->user->createToken($email, $expiredAt)->plainTextToken,
            'expiredAt' => $expiredAt,
            'tokenType' => 'Bearer',
            'user' =>  [
                'id' => $this->user->id,
                'firstname' => $this->user->firstname,
                'lastname' => $this->user->lastname,
                'email' => $this->user->email,
                'age' => $this->user->age,
                'organization' => $this->user->organization,
                'points' => $this->user->points,
                'phone' => $this->user->phone
            ]
        ]);
    }
}
