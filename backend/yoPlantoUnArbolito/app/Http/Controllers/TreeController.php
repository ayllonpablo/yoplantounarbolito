<?php

namespace App\Http\Controllers;

use App\Http\Requests\StoreTreeRequest;
use App\Http\Requests\UpdateTreeRequest;
use App\Http\Resources\TreeResource;
use App\Models\Tree;
use App\Models\TreeUser;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Ramsey\Uuid\Type\Integer;

use function PHPUnit\Framework\isEmpty;

class TreeController extends Controller
{
    public function index()
    {
        $trees = Tree::query()
            ->allowedIncludes([])
            ->allowedFilters([])
            ->allowedSorts([])
            ->sparseFieldset();

        return TreeResource::collection($trees->get());
    }

    public function show($id)
    {
        $tree = Tree::findOrFail($id);

        return TreeResource::make($tree);
    }

    public function store(StoreTreeRequest $request)//
    {
        $tree = $request->validated();
        $user = User::where('id',$request->user_id)->firstOrFail();

        if(!empty($user->trees->all())){
            return response()->json([
                "message" => "Ya tiene un árbol registrado",
            ], 422);
        }
        else{
            $tree = Tree::create($tree);
            $user->trees()->attach($tree->id);
            $user->points = $user->points + 15;
            $user->save();
            return response()->json($tree);
        }
    }

    public function edit_tree($id, UpdateTreeRequest $request){
        $tree = Tree::findOrFail($id);
        $tree->update($request->validated());

        return TreeResource::make($tree);
    }

    public function savePhotoTree($id, Request $request){
        \Log::info('savePhotoTree called with ID: ' . $id);
        \Log::info('Request data: ' . json_encode($request->all()));

        $tree = Tree::where('id',$id)->first();
        if(isset($tree)){
            \Log::info('Tree found: ' . $tree->id);

            if($request->photo){
                \Log::info('Photo data received, length: ' . strlen($request->photo));

                $image = $request->photo;
                $image = str_replace('data:image/png;base64,', '', $image);
                $image = str_replace(' ', '+', $image);
                $photo = base64_decode($image);

                \Log::info('Decoded photo size: ' . strlen($photo) . ' bytes');

                $photoName = Str::slug($tree->updated_at).$tree->name."."."jpg";
                $ruta = public_path("img/photosTree/");

                \Log::info('Saving to: ' . $ruta . $photoName);

                // Verificar si el directorio existe
                if (!file_exists($ruta)) {
                    \Log::warning('Directory does not exist, creating: ' . $ruta);
                    mkdir($ruta, 0777, true);
                }

                file_put_contents($ruta.$photoName, $photo);
                $tree->path_photo = $photoName;
                $tree->save();

                \Log::info('Photo saved successfully');

                return response()->json([
                    "status" => 1,
                    "message" => "Registro exitoso",
                ]);
            }
            else{
                \Log::error('No photo field in request');
                return response()->json([
                    "status" => 0,
                    "message" => "No se pudo registrar la fotografia - campo photo vacío",
                ], 400);
            }
        }
        else{
            \Log::error('Tree not found with ID: ' . $id);
            return response()->json([
                "status" => 0,
                "message" => "No existe el árbol seleccionado",
            ], 404);
        }
    }

    public function getPhotoTree($id){

        $tree = Tree::where('id',$id)->first();
        if(isset($tree)){
                $ruta = public_path("img/photosTree/");
                $image = file_get_contents($ruta.$tree->path_photo);
                $photo = base64_encode($image);
                return response()->json([
                    "photo" => $photo,
                ]);
        }
    }
}
