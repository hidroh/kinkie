module.exports = function() {
    var local, nearby, global;

    local = new scope(0.01, 0.5);
    nearby = new scope(0.1, 5);
    global = new scope(1);

    var move = function(socketId, lat, lon) {
        local.move(socketId, lat, lon);
        nearby.move(socketId, lat, lon);
        global.move(socketId, lat, lon);
    };

    var remove = function(socketId) {
        local.remove(socketId);
        nearby.remove(socketId);
        global.remove(socketId);
    };

    var get = function(socketId) {
        var users = [];
        users = local.get(socketId);
        if (users.length < MIN) {
            users = nearby.get(socketId);
        }

        if (users.length < MIN) {
            users = global.get(socketId);
        }

        if (users.length < MIN) {
            users = global.getAll();
        }

        return users;
    };

    return {
        move: move,
        remove: remove,
        get: get
    };
};

var MIN = 10;

var scope = function(size, distance) {
    var squares = {};
    var sockets = {};

    var move = function(socketId, lat, lon) {
        var squareLat = Math.round(lat / size) * size;
        var squareLon = Math.round(lon / size) * size;
        var lastLat, lastLon;
        if (sockets[socketId]) {
            lastLat = sockets[socketId].squareLat;
            lastLon = sockets[socketId].squareLon;
        }

        sockets[socketId] = {
            lat: lat,
            lon: lon,
            squareLat: squareLat,
            squareLon: squareLon
        };

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
            var lastLat = sockets[socketId].squareLat;
            var lastLon = sockets[socketId].squareLon;

            if (lastLat && lastLon) {
                delete squares[lastLat][lastLon][socketId];
            }

            delete sockets[socketId];
        }

    };

    var get = function(socketId) {
        var nearbySockets = [];
        if (sockets[socketId]) {
            var socket = sockets[socketId];
            var squareLat = Math.round(socket.squareLat / size) * size;
            var squareLon = Math.round(socket.squareLon / size) * size;
            var squareSockets = getSquareSockets(squareLat, squareLon);
            nearbySockets = getNearbySockets(socket, squareSockets);
            if (nearbySockets.length < MIN) {
                nearbySockets = squareSockets;
            }

            // try doubled size square
            if (nearbySockets.length < MIN) {
                nearbySockets.concat(getSquareSockets(squareLat + size, squareLon));
                nearbySockets.concat(getSquareSockets(squareLat, squareLon + size));
                nearbySockets.concat(getSquareSockets(squareLat + size, squareLon + size));
            }
        }

        return nearbySockets;
    };

    var getAll = function() {
        var s = [];
        for (var id in sockets) {
            s.push(id);
        }

        return s;
    };

    function getSquareSockets(squareLat, squareLon) {
        var s = [];
        if (squares[squareLat] && squares[squareLat][squareLon]) {
            for (var id in squares[squareLat][squareLon]) {
                s.push(id);
            }
        }

        return s;
    }

    function getNearbySockets(socket, square) {
        var s = [];
        square.forEach(function(id) {
            var lat1 = socket.lat;
            var lon1 = socket.lon;
            var lat2 = sockets[id].lat;
            var lon2 = sockets[id].lon;
            if (!distance || getDistance(lat1, lon1, lat2, lon2) <= distance) {
                s.push(id);
            }
        });

        return s;
    }

    function getDistance(lat1, lon1, lat2, lon2) {
        var R = 6371; // km
        return Math.acos(Math.sin(lat1)*Math.sin(lat2) + 
                          Math.cos(lat1)*Math.cos(lat2) *
                          Math.cos(lon2-lon1)) * R;
    }

    return {
        move: move,
        remove: remove,
        get: get,
        getAll: getAll
    };
};