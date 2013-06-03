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
			'latitude' => 1.3567,
			'longitude' => 103.8272,
			'gender' => 'Male',
			'message' => '250m',
			'created' => '2013-05-06 00:00:12'
			));
		Messages::create(array(
			'user_id' => '2',
			'username' => 'nico',
			'latitude' => 1.3552,
			'longitude' => 103.8260,
			'gender' => 'Female',
			'message' => '300m',
			'created' => '2013-04-06 00:00:12'
			));
		Messages::create(array(
			'user_id' => '3',
			'username' => 'jamoy',
			'latitude' => 1.3545,
			'longitude' => 103.8260,
			'gender' => 'Male',
			'message' => '400m',
			'created' => '2013-05-03 01:00:12'
			));
		Messages::create(array(
			'user_id' => '1',
			'username' => 'bala',
			'latitude' => 1.3517,
			'longitude' => 103.8271,
			'gender' => 'Male',
			'message' => '770m',
			'created' => '2013-05-16 20:00:12'
			));
		Messages::create(array(
			'user_id' => '4',
			'username' => 'trung',
			'latitude' => 1.3484,
			'longitude' => 103.8274,
			'gender' => 'Male',
			'message' => 'outside',
			'created' => '2013-05-06 00:02:12'
			));

		Messages::create(array(
			'user_id' => '5',
			'username' => 'chien',
			'latitude' => 1.3585,
			'longitude' => 103.8255,
			'gender' => 'Male',
			'message' => 'test point guy',
			'created' => '2013-05-07 20:10:12'
			));
	}
}
?>