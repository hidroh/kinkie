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
            $table->string('gender');
            $table->decimal('latitude',8,5);
            $table->decimal('longitude',8,5);
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