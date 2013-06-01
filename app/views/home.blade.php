@extends('layout')

@section('content')
	
	<div class="home-screen page">
		
		<div style="text-align:center;position:relative;margin-top:-100px;top:50%;">
			<h1 style="color:#ffffff;">Kinkie!</h1>
			<button class="btn" style="background:#c0392b;" onclick="window.location='/select'; return false;">Join the fun!</button>
		</div>
		
	</div>
	
	<script>
		
		var noSocketSupport = false;
		
		if(!("WebSocket" in window)) {
			$('button').text('Unfortunately, your device is not supported! :(');
			$('button').attr('disabled', 'disabled');
			noSocketSupport = true;
		}
		
		$('button').text('We need to access your current location to enable chatting!');
		$('button').attr('disabled', 'disabled');
		
		if(navigator.geolocation != null) {
			navigator.geolocation.getCurrentPosition(function(position) {
				$('button').text('Join the fun');
				$('button').attr('disabled', false);
			}, function() {
				// Unable to get current position
			});
		} else {
			// No support for geolocation
		}
		
	</script>
	
@stop


