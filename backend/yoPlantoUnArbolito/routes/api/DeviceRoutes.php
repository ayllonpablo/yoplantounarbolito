<?php

use App\Http\Controllers\DeviceController;
use Illuminate\Support\Facades\Route;

Route::apiResource('devices', DeviceController::class)
    ->only('index', 'show', 'store', 'update');
