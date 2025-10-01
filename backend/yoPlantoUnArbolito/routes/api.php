<?php

// use App\Http\Controllers\UserController;
use App\Http\Controllers\TreeController;
use App\Http\Controllers\TreeUserController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

/* Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
}); */

Route::prefix('auth')->group(__DIR__ . '/api/AuthRoutes.php');

Route::middleware('auth:sanctum')->group(function () {
    Route::group([], __DIR__ . '/api/UserRoutes.php');
    Route::group([], __DIR__ . '/api/ActionRoutes.php');
    Route::group([], __DIR__ . '/api/DeviceRoutes.php');
    Route::get('trees', [TreeController::class, 'index']);
    Route::get('trees/{id}', [TreeController::class, 'show']);
    Route::patch('trees/{id}', [TreeController::class, 'edit_tree']);

    Route::get('tree_users', [TreeUserController::class, 'index']);
    Route::get('tree_users/{id}', [TreeUserController::class, 'show']);

    Route::get('tree_users/{id}', [TreeUserController::class, 'trees']);
});

Route::post('trees', [TreeController::class, 'store']);
Route::put('savephoto/{id}', [TreeController::class, 'savePhotoTree']);
Route::post('tree_users', [TreeUserController::class, 'store']);
Route::get('get_photo/{id}', [TreeController::class, 'getPhotoTree']);

// Route::group( ['middleware' => ["auth:sanctum"]], function(){
//     // Route::get('islogin', [UserAuthController::class, 'isLogin']);
//     // Route::post('logout', [UserAuthController::class, 'logout']);
//     // Route::get('users', [UserController::class, 'index']);
//     // Route::get('users/{id}', [UserController::class, 'show']);
// });
