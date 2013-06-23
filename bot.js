var io = require('socket.io-client');
var chat = io.connect('http://localhost:8080/chat');
var keywords = ['coffee', 'lunch'];
var business = (function() {
    var id, lat, lon, geo, logo, seen;

    var init = function() {
        this.id = Math.random();
        this.lat = 1.2931;
        this.lon = 103.8558;
        this.geo = 10;
        this.logo = "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/276812_22092443056_333388977_q.jpg";
        this.seen = {};
    };

    var getPromotion = function(data) {
        var userMessage = JSON.parse(data);
        var now = new Date().getTime() / 1000; // in seconds
        if (userMessage.user_id in this.seen) {
            var lastSeen = this.seen[userMessage.user_id];
            if (now - lastSeen <= 60) {
                return;
            }
        } else {
            this.seen[userMessage.user_id] = now;
        }

        var allowed = false;
        for (var i = 0; i < keywords.length; i++) {
            if (userMessage.message.indexOf(keywords[i]) !== -1) {
                allowed = true;
                break;
            }
        };

        if (!allowed) {
            return null;
        }

        // some data processing here
        console.log('Getting promotion...');
        var message = {
            "type": 'message',
            "user_id": this.id,
            "latitude": this.lat,
            "longitude": this.lon,
            "message": "Awesome promotion alabama",
            "image": this.logo,
            "gender": "business",
            "geo": this.geo,
            "promotion_id": userMessage.user_id
        };

        return message;
    };

    return {
        init: init,
        getPromotion: getPromotion,
        id: id,
        logo: logo,
        lat: lat,
        lon: lon,
        geo: geo,
        seen: seen
    }
})();

var initChat = function(b) {
    if (b.lat != null && b.lon != null) {

        chat.emit('new business', JSON.stringify({
            'type': 'new',
            'business_id': b.id,
            'latitude': b.lat,
            'longitude': b.lon
        }));
        connected = true;
        console.log('Chat initiated.');
    }
};

chat.on('connect', function () {
    console.log("Connection established!");
    business.init();
    initChat(business);
});

chat.on('disconnect', function() {
    console.log('Connection lost.');
    connected = false;
})

chat.on('reconnect', function() {
    console.log('Reconnected!');
});

chat.on('msg', function(data, callback) {
    console.log('Message received.');
    e = JSON.parse(data);
    var promotion = {};

    if(e.type == 'message') {
        promotion = business.getPromotion(data);
    } else if (e.type == 'new') {
        console.log(e);
        e.messages.forEach(function(f) {
            promotion = business.getPromotion(data);
        });
    }

    if (promotion) {
        chat.emit('promotion', JSON.stringify(promotion));
    }
});
