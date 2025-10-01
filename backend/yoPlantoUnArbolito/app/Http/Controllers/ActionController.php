<?php

namespace App\Http\Controllers;

use App\Classes\Constants\Action\Points;
use App\Http\Requests\StoreActionRequest;
use App\Http\Resources\ActionResource;
use App\Models\Action;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class ActionController extends Controller
{
    public function index()
    {
        $actions = Action::query()
            ->allowedIncludes([])
            ->allowedFilters([])
            ->allowedSorts([])
            ->sparseFieldset();

        return ActionResource::collection($actions->get());
    }

    public function store(StoreActionRequest $request)
    {
        $validated = $request->validated();

        $validated['points'] = Points::VALUES[$validated['name']];
        // $validated['photoPath'] = Storage::putFile('actions', $validated['photo']);
        // unset($validated['photo']);

        $action = Action::create($validated);

        // Update user points
        $user = \App\Models\User::find($validated['userId']);
        if ($user) {
            $user->increment('points', $validated['points']);
        }

        return ActionResource::make($action);
    }

    public function show($id)
    {
        $action = Action::findOrFail($id);

        return ActionResource::make($action);
    }

    public function update(Request $request, $id)
    {
        //
    }

    public function destroy($id)
    {
        //
    }
}
