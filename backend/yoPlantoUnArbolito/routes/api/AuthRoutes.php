<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\RegisterController;
use Illuminate\Support\Facades\Route;

Route::post('login', [AuthController::class, 'login'])->middleware(['guest:sanctum', 'throttle:login']);
Route::post('logout', [AuthController::class, 'logout'])->middleware('auth:sanctum');
Route::post('refresh', [AuthController::class, 'refresh'])->middleware('auth:sanctum');
Route::get('me', [AuthController::class, 'me'])->middleware('auth:sanctum');
Route::post('register', [RegisterController::class, 'store'])->middleware('guest:sanctum');
Route::patch('register', [RegisterController::class, 'update'])->middleware('auth:sanctum');
