<?php

use App\Http\Controllers\ActionController;
use Illuminate\Support\Facades\Route;

Route::apiResource('actions', ActionController::class)
    ->only('index', 'show', 'store');

Route::get('actions/check-availability', [ActionController::class, 'checkAvailability']);
