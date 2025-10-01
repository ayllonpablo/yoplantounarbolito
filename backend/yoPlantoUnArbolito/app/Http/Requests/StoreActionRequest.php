<?php

namespace App\Http\Requests;

use App\Classes\Constants\Action\Name;
use App\Rules\ActionFrequency;
use App\Rules\MinDistanceFromTree;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class StoreActionRequest extends FormRequest
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
            // 'photo' => ['required', 'image', 'mimes:jpg,png', 'max:51200'],
            'name' => ['required', 'string', Rule::in(Name::AVAILABLE_NAMES), new ActionFrequency(request('userId'), request('treeId'))],
            'latitude' => ['required', 'numeric', 'between:-90,90'],
            'longitude' => ['required', 'numeric', 'between:-180,180'],
            'userId' => ['required', 'exists:users,id'],
            'treeId' => ['nullable', 'exists:trees,id', new MinDistanceFromTree(request('latitude'),request('longitude'))]
        ];
    }

    public function attributes()
    {
        return [
            'photo' => 'foto',
            'name' => 'nombre',
            'latitude' => 'latitud',
            'longitude' => 'logitud',
            'userId' => 'usuario',
            'treeId' => 'árbol'
        ];
    }
}
