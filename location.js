module.exports = function() {
    var local, nearby, global;

    local = new scope(0.01);
    nearby = new scope(0.1);
    global = new scope(1);

    var update = function(socketId, lat, lon) {
        local.move(socketId, lat, lon);
        nearby.move(socketId, lat, lon);
        global.move(socketId, lat, lon);
    };

    var get = function(socketId) {
        var users = [];
        if (local.count(socketId) >= 10) {

        } else if (nearby.count(socketId) >= 10) {

        } else {

        }
    };

    return {
        update: update
    };
};

var scope = function(size) {
    var squares = {};
    var sockets = {};

    var move = function(socketId, lat, lon) {
        var squareLat = Math.round(lat / size) * size;
        var squareLon = Math.round(lon / size) * size;
        var lastLat, lastLon;
        if (sockets[socketId]) {
            lastLat = sockets[socketId].lat;
            lastLon = sockets[socketId].lon;
        }

        sockets[socketId] = {lat: squareLat, lon: squareLon};

        if (lastLat && lastLon) {
            delete squares[lastLat][lastLon][socketId];
        }

        if (!squares[squareLat]) {
            squares[squareLat] = {};
        }

        if (!squares[squareLat][squareLon]) {
            squares[squareLat][squareLon] = [];
        }
        squares[squareLat][squareLon][socketId] = 1;
    };

    var remove = function(socketId) {
        if (sockets[socketId]) {
            var lastLat = sockets[socketId].lat;
            var lastLon = sockets[socketId].lon;

            if (lastLat && lastLon) {
                delete squares[lastLat][lastLon][socketId];
            }

            delete sockets[socketId];
        }

    };

    var count = function(socketId) {
        if (sockets[socketId]) {
            var squareLat = Math.round(sockets[socketId].lat / size) * size;
            var squareLon = Math.round(sockets[socketId].lon / size) * size;
            if (squares[squareLat][squareLon]) {
                return Object.keys(squares[squareLat][squareLon]).length;
            }
        }

        return 0;
    };

    return {
        move: move,
        count: count
    };
};