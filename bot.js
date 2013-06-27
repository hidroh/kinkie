var io = require('socket.io-client');
var chat = io.connect('http://localhost:8080/chat');
var vendors = null;
var business = require('./business');

var initChat = function() {
    for (var i = 0; i < vendors.length; i++) {
        b = vendors[i];
        if (b.lat !== null && b.lon !== null) {
            chat.emit('new business', JSON.stringify({
                'type': 'new',
                'business_id': b.id,
                'latitude': b.lat,
                'longitude': b.lon
            }));
            connected = true;
            console.log('Chat initiated.');
        }
    }
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
    var hnm = new business();
    hnm.init(
        1.2931,
        103.8558,
        "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/1063679_21415640912_1996587499_q.jpg",
        {"panty": "Awesome H&M's promotion alabama"}
    );
    vendors = [starbucks, mcdonalds, hnm];
};

var combinePromotions = function(data) {
    var promotions = [];
    for (var i = 0; i < vendors.length; i++) {
        b = vendors[i];
        var p = b.getPromotion(data);
        if (p) {
            promotions.push(p);
        }
    }

    return promotions;
};

chat.on('connect', function () {
    console.log("Connection established!");
    initVendors();
    initChat();
});

chat.on('disconnect', function() {
    console.log('Connection lost.');
    connected = false;
});

chat.on('reconnect', function() {
    console.log('Reconnected!');
});

chat.on('msg', function(data, callback) {
    console.log('Message received.');
    e = JSON.parse(data);
    var promotions = {};

    promotions = combinePromotions(data);
    for (var i = 0; i < promotions.length; i++) {
        p = promotions[i];
        chat.emit('promotion', JSON.stringify(p));
    }
});
