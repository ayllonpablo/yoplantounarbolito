<?php

namespace App\Classes\Constants\Action;

class Params
{
    public const MIN_DISTANCE_TREE = 50;

    public const ACTION_FREQUENCY = [
        Name::PLANT => 28,
        Name::TO_WATER => 36,
        Name::CLEANING => 182,
        Name::FERTILIZER => 182,
        Name::GRIP => 28,
        Name::GAMES => 73
    ];
}
