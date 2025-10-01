<?php

namespace App\Http\Responses;

use Illuminate\Http\JsonResponse;
use Illuminate\Validation\ValidationException;

class ValidationErrorResponse extends JsonResponse
{
    public function __construct(ValidationException $exception)
    {
        $data = $this->formatJsonApiErrors($exception);
        $headers = [
            'content-type' => 'application/json'
        ];

        parent::__construct($data, 422, $headers);
    }

    public function formatJsonApiErrors(ValidationException $exception) {
        return [
            'errors' => collect($exception->errors())
                ->map(fn ($message) => $message[0])->values()
        ];
    }
}
