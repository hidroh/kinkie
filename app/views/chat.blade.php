@extends('layout')

@section('content')

<div class="chat-screen page">
    <script src="http://autobahn.s3.amazonaws.com/js/autobahn.min.js"></script>
    <script>
        var conn = new ab.Session(
            'ws://54.251.109.177/:8080',
            function() {            // Once the connection has been established
                conn.subscribe('kittensCategory', function(topic, data) {
                    // This is where you would add the new article to the DOM (beyond the scope of this tutorial)
                    console.log('New article published to category "' + topic + '" : ' + data.title);
                });
            }, function() {            // When the connection is closed
                console.warn('WebSocket connection closed');
            }, {                       // Additional parameters, we're ignoring the WAMP sub-protocol for older browsers
                'skipSubprotocolCheck': true
            }
        );
    </script>

    <a id="lockmein" href="javascript:;" class="btn" style="position:fixed;top:10px;right:10px;z-index:100;"><span>I wanna stay here</span> <i class="icon-unlock-alt"></i></a>

    <div id="messagebox">

        <div id="noone" style="margin-top:-12px;position:absolute;width:100%;color:#c0c0c0;text-align:center;font-size:28px;">Yey! You're alone <i class="icon-meh"></i></div>

        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem.</p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message mine">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id</p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar.</p>
                <p>accumsan porta arcu et pulvinar.</p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem. Vestibulum ante ipsum primis</p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; </p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem.</p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message mine">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id</p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar.</p>
                <p>accumsan porta arcu et pulvinar.</p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem. Vestibulum ante ipsum primis</p>
            </div>
            <div class="avatar"></div>
            <div class="away">32m away</div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; </p>
            </div>
            <div class="avatar"></div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem.</p>
            </div>
            <div class="avatar"></div>
        </div>
        <div class="message mine">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id</p>
            </div>
            <div class="avatar"></div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar.</p>
                <p>accumsan porta arcu et pulvinar.</p>
            </div>
            <div class="avatar"></div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem. Vestibulum ante ipsum primis</p>
            </div>
            <div class="avatar"></div>
        </div>
        <div class="message">
            <div class="text" style="">
                <p>accumsan porta arcu et pulvinar. Maecenas scelerisque eros in velit feugiat blandit. Donec id condimentum sem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; </p>
            </div>
            <div class="avatar"></div>
        </div>

    </div>

    <div class="textbox" style="bottom:0;width:100%;left:0;height:50px;">
        <button class="btn" style="position:absolute;top:0;left:0;width:50px;margin:10px;font-size:20px;"><i class="icon-smile"></i></button>
        <textarea placeholder="Your brainwords go here..." style="margin:10px 20px;min-height:32px;height:32px;"></textarea>
        <button class="btn" disabled style="position:absolute;top:0;right:0;width:90px;margin:10px;">Send <i class="icon-rocket"></i></button>
    </div>

</div>

<script>
    $(function($) {
        if ($('.message').length > 0) {
            $('#noone').hide();
            $('.message').addClass('show');
        } else {
            $('#noone').addClass('go');
        }

        // lock
        $('#lockmein').click(function() {
            if (!$(this).hasClass('locked')) {
                $(this).addClass('locked').find('i').removeClass('icon-unlock-alt').addClass('icon-lock');
                $(this).find('span').text('I\'m staying here');
            } else {
                $(this).removeClass('locked').find('i').removeClass('icon-lock').addClass('icon-unlock-alt');
                $(this).find('span').text('I wanna stay here');
            }
        });

        // send a message
        $('.textbox textarea').keypress(function() {
            
        });
    });
</script>

@stop


