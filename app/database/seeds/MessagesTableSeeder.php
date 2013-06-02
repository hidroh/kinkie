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
			'gender' => 'Male',
			'message' => 'hello world',
			'created' => '2013-05-06 00:00:12'
			));
		Messages::create(array(
			'user_id' => '2',
			'username' => 'nico',
			'latitude' => 101.40,
			'longitude' => 100.24,
			'gender' => 'Female',
			'message' => 'finlander',
			'created' => '2013-04-06 00:00:12'
			));
		Messages::create(array(
			'user_id' => '3',
			'username' => 'jamoy',
			'latitude' => 102.10,
			'longitude' => 101.24,
			'gender' => 'Male',
			'message' => 'korean guy',
			'created' => '2013-05-03 01:00:12'
			));
		Messages::create(array(
			'user_id' => '1',
			'username' => 'bala',
			'latitude' => 103.10,
			'longitude' => 104.24,
			'gender' => 'Male',
			'message' => 'Indian guy',
			'created' => '2013-05-16 20:00:12'
			));
		Messages::create(array(
			'user_id' => '4',
			'username' => 'trung',
			'latitude' => 100.10,
			'longitude' => 106.24,
			'gender' => 'Male',
			'message' => 'vietnamese guy',
			'created' => '2013-05-06 00:02:12'
			));

		Messages::create(array(
			'user_id' => '5',
			'username' => 'chien',
			'latitude' => 101.50,
			'longitude' => 102.24,
			'gender' => 'Male',
			'message' => 'vietnamese guy',
			'created' => '2013-05-07 20:10:12'
			));
	}
}
?>