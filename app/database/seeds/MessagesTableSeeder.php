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
			'latitude' => '100',
			'longitude' => '100',
			'message' => 'hello world'
			));
	}
}
?>