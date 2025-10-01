<?php

namespace App\Rules;

use App\Classes\Constants\Action\Params;
use App\Models\Tree;
use Illuminate\Contracts\Validation\Rule;
use Location\Coordinate;
use Location\Distance\Vincenty;

class MinDistanceFromTree implements Rule
{
    public $latitude;
    public $longitude;
    /**
     * Create a new rule instance.
     *
     * @return void
     */
    public function __construct($latitude, $longitude)
    {
        $this->latitude = $latitude;
        $this->longitude = $longitude;
    }

    /**
     * Determine if the validation rule passes.
     *
     * @param  string  $attribute
     * @param  mixed  $value
     * @return bool
     */
    public function passes($attribute, $value)
    {
        $tree = Tree::find($value);

        $coordinateTree = new Coordinate($tree->lat, $tree->lng);
        $coordinateAction = new Coordinate($this->latitude, $this->longitude);

        $calculator = new Vincenty();
        $distanceInMeters = $calculator->getDistance($coordinateTree, $coordinateAction);
        return $distanceInMeters <= Params::MIN_DISTANCE_TREE;
    }

    /**
     * Get the validation error message.
     *
     * @return string
     */
    public function message()
    {
        return 'Estas muy lejos de tu árbol.';
    }
}
