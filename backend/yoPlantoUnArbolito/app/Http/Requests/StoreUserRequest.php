<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class StoreUserRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array
     */
    public function rules()
    {
        return [
            'name' => ['required', 'string', 'max:50', 'unique:users,name'],
            'email' => ['required', 'email:rfc,dns', 'unique:users,email'],
            'phone' => ['required', 'max:10'],
            // 'points' => [],
            'password' => ['required', 'confirmed', 'max:20'],
        ];
    }

    public function attributes()
    {
        return [
            'name' => 'nombre',
            'email' => 'correo electrónico',
            'phone' => 'teléfono',
            // 'points' => 'puntos',
            'password' => 'contraseña',
        ];
    }

}
