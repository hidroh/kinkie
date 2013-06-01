<?php

use Illuminate\Database\Migrations\Migration;

class CreateMessagesTable extends Migration {

	/**
	 * Run the migrations.
	 *
	 * @return void
	 */
	public function up()
	{
		//
		Schema::create('messages', function($table)
        {
            $table->increments('id');
            $table->string('user_id');
            $table->string('username');
            $table->decimal('latitude',5,2);
            $table->decimal('longitude',5,2);
            $table->string('message');
            $table->timestamp('created');
        });
	}

	/**
	 * Reverse the migrations.
	 *
	 * @return void
	 */
	public function down()
	{
		Schema::drop('messages');
	}

}