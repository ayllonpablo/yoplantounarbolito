<?php

namespace Database\Seeders;

use App\Models\Tree;
use App\Models\User;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     *
     * @return void
     */
    public function run()
    {
        // // \App\Models\User::factory(10)->create();
        $user = User::create([
            'firstname' => 'Daniela',
            'lastname' => 'Apellido',
            'email' => 'daniela@gmail.com',
            'age' => '25',
            'phone' => '67428511',
            'password' => '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
        ]);

        $tree = Tree::create([
            'name' => 'Arbolito de Daniela',
            'lat' => '0.0',
            'lng' => '0.0',
            'avatar' => 'www/dfafa/dfadfa',
            'path_photo' => 'null',
            'state' => 'regado'
        ]);

        $user->trees()->attach($tree->id);

        $this->call([
            UserSeeder::class,
            ActionSeeder::class
        ]);
    }
}
