@extends('layout')

@section('content')

<div class="join-screen page">



<div id="fb-root"></div>

<script>

    var wsuri = "ws://54.251.109.177:80";
    var lat, lon;
    var conn;

    window.onload = function() {
        navigator.geolocation.getCurrentPosition(function(position) {
            lat = position.coords.latitude;
            lon = position.coords.longitude;
        });
        
        conn = new WebSocket(wsuri);
        conn.onopen = function(e) {

        };

        conn.onmessage = function(e) {

        }

        conn.onerror = function(e) {
            alert('Oops! We made a doodoo..');
        }
    }

window.fbAsyncInit = function() {
  FB.init({ appId: '141581032697864', 
        status: true, 
        cookie: true,
        xfbml: true,
        oauth: true});

  function updateButton(response) {
        
    if (response.authResponse) {
      //user is already logged in and connected
      FB.api('/me', function(response) {
        var user = {
            "type": "user",
            "id": response.id,
            "username": response.username,
            "name": response.first_name + ' ' + response.last_name,
            "gender": response.gender,
            "image": "https://graph.facebook.com/" + response.id + "/picture",
            "lat": lat,
            "lon": lon
        }

        conn.send(JSON.stringify(user));
        $.cookie('user', JSON.stringify(user));
        window.location.href = '/join';
      });
    } else {
        //user is not connected to your app or logged out
      
        $('#fb').click(function() {
            FB.login(function(response) {
                if (response.authResponse) {
                    FB.api('/me', function(response) {
                        var user = {
                            "type": "user",
                            "id": response.id,
                            "username": response.username,
                            "name": response.first_name + ' ' + response.last_name,
                            "gender": response.gender,
                            "image": "https://graph.facebook.com/" + response.id + "/picture",
                            "latitude": lat,
                            "longitude": lon
                        }
                        conn.send(JSON.stringify(user));
                        $.cookie('user', JSON.stringify(user));
                        window.location.href = '/join';
                    });    
                } else {
                    //user cancelled login or did not grant authorization
                }
            }, {scope:'email'});   
            return false; 
        });
      
    }
  }

  // run once with current status and whenever the status changes
  FB.getLoginStatus(updateButton); 
};
    
(function() {
  var e = document.createElement('script'); e.async = true;
  e.src = document.location.protocol 
    + '//connect.facebook.net/en_US/all.js';
  document.getElementById('fb-root').appendChild(e);
}());

</script>

    <div style="text-align:center;position:relative;margin-top:-200px;height:400px;top:50%;width:300px;left:50%;margin-left:-150px;">
        <h1 style="color:#ffffff;">Kinkie!</h1>
        <p style="color:#ffffff;">Go on.. choose one</p>
        <div style="margin-top:30px;">
            <div style="width:100%;float:left;text-align:center;">
                <div id="fb" style="background:#2c3e50;display:inline-block;padding:35px 30px;width:130px;border-radius:100px;">
                    <a href="javascript:;"><i style="font-size:60px;color:#3498db;" class="icon-facebook"></i></a>
                </div>
            </div>
        </div>
        <p style="color:#ffffff;padding-top:30px;clear:both;">yes. just facebook.</p>
    </div>    

</div>
	
@stop


