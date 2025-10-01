<?php

namespace App\Http\Controllers;

use App\Http\Requests\StoreDeviceRequest;
use App\Http\Requests\UpdateDeviceRequest;
use App\Http\Resources\DeviceResource;
use App\Models\Device;
use Illuminate\Http\Request;

class DeviceController extends Controller
{

    public function index()
    {
        $devices = Device::query()
            ->allowedIncludes([])
            ->allowedFilters([])
            ->allowedSorts([])
            ->sparseFieldset();

        return DeviceResource::collection($devices->get());
    }

    public function store(StoreDeviceRequest $request)
    {
        $validated = $request->validated();

        $device = Device::create($validated);

        return DeviceResource::make($device);
    }

    public function show($id)
    {
        $device = Device::findOrFail($id);

        return DeviceResource::make($device);
    }

    public function update(UpdateDeviceRequest $request, $id)
    {
        $device = Device::findOrFail($id);
        $device->update($request->validated());

        return DeviceResource::make($device);
    }

}
