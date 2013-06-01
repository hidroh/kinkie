@extends('layout')

@section('content')
	
<div class="join-screen page">

    <script type="text/javascript">
        $(function() {
            var user = JSON.parse($.cookie('user'));
            $('#image').css('backgroundImage', "url('" + user.image + "')").find('i').css('opacity', '0');
            $('#name').text(user.name);
        });
    </script>
	
    <div style="text-align:center;position:relative;margin-top:-200px;height:400px;top:50%;width:300px;left:50%;margin-left:-150px;">
        <h1 style="color:#ffffff;">Welcome</h1>
        <div style="margin-top:50px;">
            <div style="text-align:center;">
                <div style="background:#2c3e50;display:inline-block;padding:35px 30px;width:130px;border-radius:100px;background-size:cover;" id="image">
                    <i style="font-size:60px;color:#3498db;" class="icon-facebook"></i>
                </div>
            </div>
        </div>
        <h3 style="color:#ffffff;" id="name">Nico Hiort af Ornas</h3>
        <p style="color:#ffffff;">By clicking the button below, you agree to the non-existent privacy policy we have.</p>
        <p><button class="btn" style="background:#f1c40f;color:#2c3e50;">Jump in!</button></p>
    </div>    
	
</div>

@stop


