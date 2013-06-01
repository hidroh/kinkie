<?php

class MessagesTableSeeder extends Seeder {

	/**
	 * Run the database seeds.
	 *
	 * @return void
	 */
	public function run()
	{
		DB::table('messages')->delete();
		Messages::create(array(
			'user_id' => '1',
			'username' => 'bala',
			'latitude' => 100.10,
			'longitude' => 100.24,
			'message' => 'hello world'
			));
		Messages::create(array(
			'user_id' => '2',
			'username' => 'nico',
			'latitude' => 101.40,
			'longitude' => 100.24,
			'message' => 'finlander'
			));
		Messages::create(array(
			'user_id' => '3',
			'username' => 'nico',
			'latitude' => 102.10,
			'longitude' => 101.24,
			'message' => 'korean guy'
			));
		Messages::create(array(
			'user_id' => '1',
			'username' => 'bala',
			'latitude' => 103.10,
			'longitude' => 104.24,
			'message' => 'Indian guy'
			));
		Messages::create(array(
			'user_id' => '4',
			'username' => 'trung',
			'latitude' => 100.10,
			'longitude' => 106.24,
			'message' => 'vietnamese guy'
			));

		Messages::create(array(
			'user_id' => '5',
			'username' => 'chien',
			'latitude' => 101.50,
			'longitude' => 102.24,
			'message' => 'vietnamese guy'
			));
	}
}
?>