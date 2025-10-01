<?php

namespace Database\Seeders;

use App\Classes\Constants\Action\Name;
use App\Classes\Constants\Action\Points;
use App\Models\Action;
use App\Models\Tree;
use App\Models\User;
use Illuminate\Database\Seeder;

class ActionSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        $user = User::find(1);
        $tree = Tree::find(1);

        Action::create([
            'photo_path' => '/test/test.png',
            'name' => Name::PLANT,
            'points' => Points::VALUES[Name::PLANT],
            'latitude' => '-17.418978',
            'longitude' => '-66.144611',
            'userId' => $user->id,
            'treeId' => $tree->id
        ]);
    }
}
