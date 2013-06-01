@extends('layout')

@section('content')

<div class="join-screen page">

    <div style="text-align:center;position:relative;margin-top:-200px;height:400px;top:50%;width:300px;left:50%;margin-left:-150px;">
        <h1 style="color:#ffffff;">WhereChat!</h1>
        <p style="color:#ffffff;">Go on.. choose one</p>
        <div style="margin-top:100px;">
            <div style="width:50%;float:left;text-align:center;">
                <div style="background:#2c3e50;display:inline-block;padding:35px 30px;width:130px;border-radius:100px;">
                    <a href="/join"><i style="font-size:60px;color:#3498db;" class="icon-facebook"></i></a>
                </div>
            </div>
            <div style="width:50%;float:left;">
                <div style="background:#2c3e50;display:inline-block;padding:35px 30px;width:130px;border-radius:100px;">
                    <a href="/join"><i style="font-size:60px;color:#3498db;" class="icon-twitter"></i></a>
                </div>
            </div>
        </div>
    </div>    

</div>
	
@stop


