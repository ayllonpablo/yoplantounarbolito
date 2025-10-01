<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class StoreTreeRequest extends FormRequest
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
            'name' => ['required', 'string', 'min:5', 'max:50', 'unique:trees,name'],
            'lat' => ['required'],
            'lng' => ['required'],
            'avatar' => ['required'],
            'path_photo' =>['required'],
            'state' => ['required', 'string'],
        ];
    }

    public function attributes()
    {
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
