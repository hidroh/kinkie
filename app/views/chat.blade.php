@extends('layout')

@section('content')
<script src="/js/vendor/jquery.textarea-expander.js"></script>

<div class="chat-screen page">
    <script>
        var wsuri = "ws://54.251.109.177:80";
        var lat, lon;

        window.onload = function() {
            navigator.geolocation.getCurrentPosition(function(position) {
                lat = position.coords.latitude;
                lon = position.coords.longitude;
            });
            
            var conn = new WebSocket(wsuri);
            conn.onopen = function(e) {
                console.log("Connection established!");

                // send a message
                $('.textbox textarea').keypress(function() {
                    if ($(this).val() != '') {
                        $('.send').attr('disabled', false);
                    } else {
                        $('.send').attr('disabled', true);
                    }
                }).keypress(function(e) {
                    var text = $(this).val();
                    if ((e.which == 13 && !e.shiftKey) && text) {
                        addMessage(text, conn);
                        $('.textbox #text').val('');
                    }
                });

                $('.btn.send').click(function() {
                    var text = $('.textbox #text').val();
                    addMessage(text, conn);
                    $('.textbox #text').val('');
                });
            };

            conn.onmessage = function(e) {
                e = JSON.parse(e);
                addMessage(e.message);
            }

            conn.onerror = function(e) {
                $('.textbox > *').attr('disabled');
                $('#noone').html('We couldn\'t reach anyone <i class="icon-frown"></i>');
            }
        }

        function addMessage(e, conn) {
            var message = {
                "type": 'message',
                "lat": lat,
                "lon": lon,
                "message": e
            }

            $('#noone').fadeOut(function() {
                $(this).remove();
            });

            var p = $('<p/>').text(e);
            var text = $('<div/>').addClass('text').append(p);
            var avatar = $('<div/>').addClass('avatar');
            var away = $('<div/>').addClass('away').text('32m away');
            var div = $('<div />').addClass('message').append(text).append(avatar).append(away);
            $('#messagebox').append(div);
            $('.message').addClass('show');
            
            if (conn) conn.send(JSON.stringify(message));
        }
    </script>

    <a id="lockmein" href="javascript:;" class="btn" style="position:fixed;top:10px;right:10px;z-index:100;"><span>I wanna stay here</span> <i class="icon-unlock-alt"></i></a>

    <div id="messagebox">

        <div id="noone" style="margin-top:-12px;position:absolute;width:100%;color:#c0c0c0;text-align:center;font-size:28px;">Yey! You're alone <i class="icon-meh"></i></div>

        <!--div class="message">
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
        </div-->

    </div>


    <div class="textbox" >
        <div class="smilies">
            <button class="btn" data-smily=":)"><i class="icon-smile"></i></button>
            <button class="btn" data-smily=":|"><i class="icon-meh"></i></button>
            <button class="btn" data-smily=":("><i class="icon-frown"></i></button>
            <button class="btn" data-smily="<3"><i class="icon-heart"></i></button>
            <button class="btn" data-smily="(y)"><i class="icon-hand-right"></i></button>
        </div>
        <div class="textbar">
            <button class="btn smily" style="position:absolute;top:0;left:0;width:50px;margin:10px;font-size:20px;"><i class="icon-smile"></i></button>
            <textarea id="text" placeholder="Your brainwords go here..." style="margin:10px 20px;min-height:32px;height:32px;"></textarea>
            <button class="btn send" disabled style="position:absolute;top:0;right:0;width:90px;margin:10px;">Send <i class="icon-rocket"></i></button>
        </div>
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

        $('.textbox #text').TextAreaExpander(0, 60);

        $('.btn.smily').click(function() {
            if (!$('.smilies').hasClass('go')) {
                $('.smilies').addClass('go');
            } else {
                $('.smilies').removeClass('go');
            }
        });

        $('.smilies .btn').click(function() {
            $('.btn.smily').click();
            var text = $('.textbox #text').val();
            var data = $(this).data('smily');
            $('.textbox #text').val(text + ' ' + data);
        });

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
    });
</script>

@stop


