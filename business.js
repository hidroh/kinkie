module.exports = function() {
    var id, lat, lon, logo, seen, keywords;

    var init = function(lat, lon, logo, keywords) {
        this.id = Math.random();
        this.lat = lat;
        this.lon = lon;
        this.logo = logo;
        this.seen = {};
        this.keywords = keywords;
    };

    var getPromotion = function(data) {
        var userMessage = JSON.parse(data);
        var now = new Date().getTime() / 1000; // in seconds
        if (userMessage.socket_id in this.seen) {
            var lastSeen = this.seen[userMessage.socket_id];
            if (now - lastSeen <= 60) {
                return;
            }
        } else {
            this.seen[userMessage.socket_id] = now;
        }

        var text = '';
        for (var keyword in this.keywords) {
            if (userMessage.message.indexOf(keyword) !== -1) {
                text += this.keywords[keyword] + " ";
            }
        }

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
            "promotion_id": userMessage.socket_id
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
        seen: seen
    };
};