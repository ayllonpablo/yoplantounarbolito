<?php

namespace App\Classes\Constants\Action;

class Params
{
    public const MIN_DISTANCE_TREE = 50;

    public const ACTION_FREQUENCY = [
        Name::PLANT => 6,
        Name::TO_WATER => 6,
        Name::CLEANING => 6,
        Name::FERTILIZER => 6,
        Name::GRIP => 6,
        Name::GAMES => 6
    ];
}
