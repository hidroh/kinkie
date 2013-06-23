var io = require('socket.io-client');
// var chat = io.connect('http://localhost:8080/chat');
var chat = io.connect('http://ec2-122-248-221-140.ap-southeast-1.compute.amazonaws.com:8080/chat');
var vendors = null;
var business = function() {
    var id, lat, lon, geo, logo, seen, keywords;

    var init = function(lat, lon, logo, keywords) {
        this.id = Math.random();
        this.lat = lat;
        this.lon = lon;
        this.geo = 10;
        this.logo = logo;
        this.seen = {};
        this.keywords = keywords;
    };

    var getPromotion = function(data) {
        var userMessage = JSON.parse(data);
        var now = new Date().getTime() / 1000; // in seconds
        if (userMessage.user_id in this.seen) {
            var lastSeen = this.seen[userMessage.user_id];
            // if (now - lastSeen <= 60) {
                // return;
            // }
        } else {
            this.seen[userMessage.user_id] = now;
        }

        var text = '';
        for (var keyword in this.keywords) {
            if (userMessage.message.indexOf(keyword) !== -1) {
                text += this.keywords[keyword] + " ";
            }
        };

        if (!text) {
            return null;
        }

        // some data processing here
        console.log('Getting promotion...');
        var message = {
            "type": 'message',
            "user_id": this.id,
            "latitude": this.lat,
            "longitude": this.lon,
            "message": text,
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
};

var initChat = function() {
    for (var i = 0; i < vendors.length; i++) {
        b = vendors[i];
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
};

var initVendors = function() {
    var starbucks = new business();
    starbucks.init(
        1.2931,
        103.8558,
        "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/276812_22092443056_333388977_q.jpg",
        {"coffee": "Awesome starbucks promotion alabama"}
    );
    var mcdonalds = new business();
    mcdonalds.init(
        1.2931,
        103.8558,
        "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/373478_50245567013_320421372_q.jpg",
        {"lunch": "Awesome mcdonald's promotion alabama"}
    );
    vendors = [starbucks, mcdonalds];
}

var getPromotions = function(data) {
    var promotions = new Array();
    for (var i = 0; i < vendors.length; i++) {
        b = vendors[i];
        var p = b.getPromotion(data);
        if (p) {
            promotions.push(p);
        }
    };

    return promotions;
}

chat.on('connect', function () {
    console.log("Connection established!");
    initVendors();
    initChat();
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
    var promotions = {};

    if(e.type == 'message') {
        promotions = getPromotions(data);
    } else if (e.type == 'new') {
        console.log(e);
        e.messages.forEach(function(f) {
            promotions = getPromotions(data);
        });
    }

    for (var i = 0; i < promotions.length; i++) {
        p = promotions[i];
        chat.emit('promotion', JSON.stringify(p));
    }
});
