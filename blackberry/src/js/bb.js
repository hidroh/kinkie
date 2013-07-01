var updateButton;

var lat, lon, connected;
var lastMessage;
var watchId;
var chat = io.connect('http://kinkie.im:8080/chat');

$(document).ready(function() {
   
   // Copypasta from http://stackoverflow.com/questions/1403888/get-url-parameter-with-jquery
   function getURLParameter(name) {
      return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
   }
   
   function displayPage(page) {
      
      $('div.page').hide();
      $('div.page.' + page + '-screen').show();
      
   }
   
   // ---------------------- Home page ----------------------
   
   function handleHomePage() {
      
      $('div.page.home-screen button#join').click(function() {
         displayPage('join');
         handleJoinPage();
      });
      
      $('div.page.home-screen button#join').text('Could not detect your location! :(');
      $('div.page.home-screen button#join').attr('disabled', 'disabled');
      
      if(navigator.geolocation != null) {
         navigator.geolocation.getCurrentPosition(function(position) {
            $('div.page.home-screen button#join').text('Join the fun');
            $('div.page.home-screen button#join').attr('disabled', false);
            
            if(getURLParameter('chat') == 'now') {
               $('div.page.home-screen button#join').trigger('click');
            }
         }, function() {
            // Unable to get current position
         });
      } else {
         // No support for geolocation
      }
      
   }

   // ---------------------- Home page ----------------------
   function handleJoinPage() {
      if (localStorage.getItem('user')) {
         $.cookie('user', localStorage.getItem('user'));
         displayPage('profile');
         handleProfilePage();
      } else {
         if(!getURLParameter('access_token')) {
            var url = 'https://www.facebook.com/dialog/oauth?client_id=461663813917591&redirect_uri=http://kinkie.im/success';
            childWindow = window.open(url, '_self');
         } else {
               //user is already logged in and connected
               localStorage.setItem('access_token', getURLParameter('access_token'));
               var fbMeUrl = 'https://graph.facebook.com/me?access_token=' + localStorage.getItem('access_token');
               $.get(fbMeUrl, function(response) {
               var user = {
                  "type": "user",
                  "id": response.id,
                  "username": response.username,
                  "name": response.first_name + ' ' + response.last_name,
                  "gender": response.gender,
                  "image": "https://graph.facebook.com/" + response.id + "/picture",
               }

               // conn.send(JSON.stringify(user));
               localStorage.setItem('user', JSON.stringify(user));
               $.cookie('user', JSON.stringify(user));
               displayPage('profile');
               handleProfilePage();
            });
         }
      }
   }

   // ---------------------- Profile page ----------------------
   
   function handleProfilePage() {
   
      var user = JSON.parse($.cookie('user'));
      $('div.page.profile-screen #image').css('backgroundImage', "url('" + user.image + "')").find('i').css('opacity', '0');
      $('div.page.profile-screen #name').text(user.name);

      $('div.page.profile-screen .goto').click(function() {
         handleChatPage();
         displayPage('chat');
      });
      
      if(getURLParameter('chat') == 'now') {
         $('div.page.profile-screen .goto').trigger('click');
      }
      
      $('div.page.profile-screen .logout').click(function() {
            $.get('https://www.facebook.com/logout.php?next=http://kinkie.im&access_token=' + localStorage.getItem('access_token'), function(response) {
               localStorage.removeItem('user');
               $.cookie('user', null);
               localStorage.removeItem('access_token');
               displayPage('home');
            });
      });
   }

   // ---------------------- Chat page ----------------------
   
   function handleChatPage() {
      
      if (!window.console) console = {log: function() {}};
      var disableChat = function() {
         $('.textbox > *').attr('disabled');
         $('#noone').html('We couldn\'t reach anyone <i class="icon-frown"></i>');
      };
      
      var enableChat = function() {
         $('.textbox > *').removeAttr('disabled');
         $('#noone').html('It\'s lonely in here <i class="icon-meh"></i><p style="font-size:15px;">Why not say something to start a conversation</p>');
      }
      
      try {

         var initChat = function(lat, lon) {
            if (lat != null && lon != null && !connected) {
               var user = JSON.parse($.cookie('user'));

               chat.emit('new user', JSON.stringify({
                  'type': 'new',
                  'user_id': user.id,
                  'latitude': lat,
                  'longitude': lon
               }));
               connected = true;
               console.log('Chat initiated.');
            }
         };

         chat.on('connect', function () {
            console.log("Connection established!");
            initChat(lat, lon);
         });

         chat.on('disconnect', function() {
            console.log('Connection lost.');
            connected = false;
            disableChat();
         })

         chat.on('reconnect', function() {
            console.log('Reconnected!');
            enableChat();
         });

         chat.on('error', function(data) {
            console.log('Error message from server: ' + data.message);
         })

         chat.on('msg', function(data, callback) {
            e = JSON.parse(data);
            
            if(e.type == 'message') {
               addMessage(true, e.message, null, e.image, e.latitude, e.longitude, e.user_id, e.gender);
            } else if(e.type == 'info') {
               $('#topinfo').show();
               $('#topinfo span').text(e.message);
               setTimeout("$('#topinfo').fadeOut(500)", 5000);
            } else if (e.type == 'new') {
               console.log(e);
               e.messages.forEach(function(f) {
                  addMessage(true, f.message, null, f.image, f.latitude, f.longitude, f.user_id, f.gender);
               });
            }
         });
         
         watchId = navigator.geolocation.watchPosition(function(position) {
            console.log("Location updated.");
            lat = position.coords.latitude;
            lon = position.coords.longitude;
            // initChat(lat, lon);
         }, function() {}, {timeout: 60000});
         
         // send a message
         $('.textbox textarea').keypress(function() {
            if ($(this).val() != '') {
               $('.send').attr('disabled', false);
            } else {
               $('.send').attr('disabled', true);
            }
         }).keypress(function(e) {
            var text = $(this).val();
            if (e.which == 13 && text) {
               addMessage(false, text, chat);
               $('.textbox #text').val('');
               e.preventDefault(true);
               return; 
            } else if(e.which == 13) {
               e.preventDefault(true);
               return;
            }
         });
         
         $('.btn.send').click(function() {
            var text = $('.textbox #text').val();
            addMessage(false, text, chat);
            $('.textbox #text').val('');
         });
         
         navigator.geolocation.getCurrentPosition(function(position) {
            console.log("Location determined");
            lat = position.coords.latitude;
            lon = position.coords.longitude;
            initChat(lat, lon);
         });
         
      } catch(e) {
         disableChat();
      }
      
      if ($('.message').length > 0) {
         $('#noone').hide();
         $('.message').addClass('show');
      } else {
         $('#noone').addClass('go');
      }

      $('.textbox #text').TextAreaExpander(0, 60);
      
   }
   
   function addMessage(displayed, e, conn, image, lat2, lon2, userid, gender) {
      var user = JSON.parse($.cookie('user'));
      var message = {
         "type": 'message',
         "user_id": user.id,
         "latitude": lat,
         "longitude": lon,
         "message": e,
         "image": image || user.image,
         "gender": gender || user.gender,
         "geo": -1
      }

      if (displayed) {
         gender = gender || user.gender;

         userid = userid || user.id;

         var image = image || user.image;

         e = e.replace(/^\s+|\s+$/g, '');
         if (e == '') {
            $('.textbox #text').val('');
            return;
         }

         $('#noone').fadeOut(function() {
            $(this).remove();
         });

         var p = $('<p/>').text(e);
         var text = $('<div/>').addClass('text');
         if (lastMessage == userid) {
            var div = $('#messagebox .message:last-child');
            div.find('.text').append(p);
         } else {
            text.append(p);
            var avatar = $('<div/>').addClass('avatar').css('backgroundImage', "url('" + image + "')");
            var away = $('<div/>').addClass('away');
            var dist = 0;
            if (conn == null && lat2 && lon2) {
               var from = new google.maps.LatLng(lat, lon);
               var to   = new google.maps.LatLng(lat2, lon2);
               dist = google.maps.geometry.spherical.computeDistanceBetween(from, to);
               dist = Math.ceil(dist);
               
               if(dist > 10000) {
                  $(away).html('<strong>' + Math.ceil(dist/1000) + 'km</strong> away');
               } else if(dist > 1000) {
                  $(away).html('<strong>' + (Math.round(dist / 100) / 10) + 'km</strong> away');
               } else {
                  $(away).html('<strong>' + Math.ceil(dist) + 'm</strong> away');
               }
            }
            var div = $('<div />').addClass('message').append(text).append(avatar).append(away);
         }

         div.addClass(gender);
         if (userid == user.id) {
            div.addClass('mine');
         }

         $('#messagebox').append(div);

         $('#textbox').css('bottom', 0);

         text.after('<div class="clearfix"></div>');

         $('.message').addClass('show');

         // smilies
         e = p.text().replace(/(<([^>]+)>)/ig,"");
         e = e.replace(/\:\)/g, '<i class="icon icon-smile"></i>');
         e = e.replace(/\:\|/g, '<i class="icon icon-meh"></i>');
         e = e.replace(/\:\(/g, '<i class="icon icon-frown"></i>');
         e = e.replace(/\<3/g, '<i class="icon icon-heart"></i>');
         e = e.replace(/\(y\)/g, '<i class="icon icon-hand-right"></i>');
         
         p.html(e);
         
         $('#textbox').css('bottom', $(document).height());
         $("html, body").animate({ scrollTop: $(document).height() }, 200);

         div.find('.text').find('.dateago').remove();
         $('<div/>').addClass('dateago').text($.timeago(new Date())).attr('title', new Date().toISOString()).appendTo(div.find('.text'));

         lastMessage = userid;
      }

      
      if (conn) {
         conn.emit('msg', JSON.stringify(message));
      }
   }
   
   if (getURLParameter('access_token')) {
      localStorage.setItem('access_token', getURLParameter('access_token'));
      displayPage('join');
      handleJoinPage();
   } else {
      displayPage('home');
      handleHomePage();
   }

   setInterval(function() {
      $('.dateago').each(function(i, e) {
         var date = $.timeago($(this).attr('title'));
         $(this).text(date);
      });
   }, 10000);
   
});