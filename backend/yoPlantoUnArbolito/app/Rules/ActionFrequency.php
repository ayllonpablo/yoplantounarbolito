<?php

namespace App\Rules;

use App\Classes\Constants\Action\Name;
use App\Classes\Constants\Action\Params;
use App\Models\Action;
use Illuminate\Contracts\Validation\Rule;

class ActionFrequency implements Rule
{
    public $userId;
    public $treeId;
    public $message;
    /**
     * Create a new rule instance.
     *
     * @return void
     */
    public function __construct($userId, $treeId)
    {
        $this->userId = $userId;
        $this->treeId = $treeId;
    }

    /**
     * Determine if the validation rule passes.
     *
     * @param  string  $attribute
     * @param  mixed  $value
     * @return bool
     */
    public function passes($attribute, $value)
    {
        if (is_null($this->treeId) && $value !== Name::GAMES) {
            $this->message = 'El campo árbol es obligatorio.';
            return false;
        }

        $prevAction = Action::where('user_id', $this->userId)
            ->where('tree_id', $this->treeId)
            ->where('name', $value)
            ->orderBy('created_at', 'desc')
            ->first();

        if (isset($prevAction)) {
            $daysPastCurrently = $prevAction->createdAt->diffInDays(now());
            $actionIsPossible = $daysPastCurrently >= Params::ACTION_FREQUENCY[$value];

            if (!$actionIsPossible) {
                $daysRequired = Params::ACTION_FREQUENCY[$value];
                $availableDate = $prevAction->createdAt->addDays($daysRequired)->format('d/m/Y');
                $this->message = "La acción '{$value}' estará disponible el {$availableDate}";
            }

            return $actionIsPossible;
        }

        return true;
    }

    /**
     * Get the validation error message.
     *
     * @return string
     */
    public function message()
    {
        return $this->message;
    }

}
