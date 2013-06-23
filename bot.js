var io = require('socket.io-client');
var chat = io.connect('http://localhost:8080/chat');
// var chat = io.connect('http://ec2-122-248-221-140.ap-southeast-1.compute.amazonaws.com:8080/chat');
var vendors = [];
var business = function() {
    var id, lat, lon, geo, logo, seen, keywords;

    var init = function(logo, keywords) {
        this.id = Math.random();
        this.lat = 1.2931;
        this.lon = 103.8558;
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

        var allowed = false;
        for (var i = 0; i < this.keywords.length; i++) {
            if (userMessage.message.indexOf(this.keywords[i]) !== -1) {
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
};

var initChat = function(vendors) {
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

var getVendorPromotions = function(data) {
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
    var starbucks = new business();
    starbucks.init("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/276812_22092443056_333388977_q.jpg", ["coffee"]);
    var mcdonalds = new business();
    mcdonalds.init("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/373478_50245567013_320421372_q.jpg", ["lunch"]);
    vendors = [starbucks, mcdonalds];
    initChat(vendors);
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
        promotions = getVendorPromotions(data);
    } else if (e.type == 'new') {
        console.log(e);
        e.messages.forEach(function(f) {
            promotions = getVendorPromotions(data);
        });
    }

    for (var i = 0; i < promotions.length; i++) {
        p = promotions[i];
        chat.emit('promotion', JSON.stringify(p));
    }
});
