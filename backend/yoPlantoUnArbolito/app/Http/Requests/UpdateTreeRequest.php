<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class UpdateTreeRequest extends FormRequest
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
            'name' => ['string', 'min:5', 'max:50'],
            'lat' => ['string'],
            'lng' => ['string'],
            'avatar' => ['string'],
            'path_photo' =>['string'],
            'state' => ['string'],
        ];

        return [
            'name' => 'nombre',
            'lat' => 'latitud',
            'lat' => 'longitud',
            'avatar' => 'personaje',
            'path_photo' => 'fotografía',
            'state' => 'estado',
        ];
    }
}
