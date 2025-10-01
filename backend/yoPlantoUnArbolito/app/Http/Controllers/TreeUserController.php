<?php

namespace App\Http\Controllers;

use App\Http\Requests\StoreTreeUserRequest;
use App\Models\TreeUser;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

use function PHPUnit\Framework\isEmpty;

class TreeUserController extends Controller
{
    public function index()
    {
        $tree_users = TreeUser::get();

        return response()->json($tree_users);

    }

    public function show($id)
    {
        $tree_user = TreeUser::where('id', $id)
            ->firstOrFail();
        return response()->json($tree_user);
    }

    public function store(StoreTreeUserRequest $request)
    {
        $response = response()->json([
            "message" => "Error de Registro.",
        ], 404);
        $tree_user = TreeUser::where('user_id', $request->user_id)->where('tree_id', $request->tree_id)->first();

        if(isset($tree_user)){
            $response = response()->json([
            "message" => "Usted ya esta cuidando un arbolito.",
        ], 404);
        }
        else{
            $tree_user = $request->validated();
            $tree_user = TreeUser::create($tree_user);
            if($tree_user){
                $response = response()->json($tree_user);
            }
        }
        return $response;
    }

    public function trees($id){
        $user = User::find($id);
        return $user->trees;
        if(isEmpty($user->trees)){
            foreach ($user->trees as $tree) {
                return response()->json([
                    "id" => $tree->id,
                    "name" => $tree->name,
                    "lat" => $tree->lat,
                    "lng" => $tree->lng,
                    "avatar" => $tree->avatar,
                    "state"=> $tree->state,
                ]);
            }
        }
        else{
            return response()->json([
                "message" => "No tiene árbol registrado",
            ], 404);
        }



         /* foreach ($user->trees as $tree) {

            $ruta = public_path("img/photosTree/");
            $image = file_get_contents($ruta.$tree->path_photo);
            $photo = base64_encode($image);

            return response()->json([
                "id" => $tree->id,
                "name" => $tree->name,
                "lat" => $tree->lat,
                "lng" => $tree->lng,
                "avatar" => $tree->avatar,
                "state"=> $tree->state,
            ]);
        }  */
    }
}
