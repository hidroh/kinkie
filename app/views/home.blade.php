@extends('layout')

@section('content')
	
	<div class="home-screen page">
		
		<div style="text-align:center;position:relative;margin-top:-100px;top:50%;">
			<h1 style="color:#ffffff;">WhereChat!</h1>
			<button class="btn" style="background:#c0392b;" onclick="window.location='/select'; return false;">Join the fun!</button>
		</div>
		
	</div>
	
	<script>
		if(!("WebSocket" in window)) {
			$('button').text('Unfortunately, your device is not supported! :(');
			$('button').attr('disabled', 'disabled');
		}
	</script>
	
@stop


