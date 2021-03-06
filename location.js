var geolib = require('geolib');

module.exports = function() {
    var local, nearby, global;

    local = new grid(3, 0.5); // 0.005 lat = 0.5km
    nearby = new grid(2, 5);
    global = new grid(1);

    var move = function(socketId, lat, lon) {
        console.log('Updated new location ' + lat + ', ' + lon);
        local.move(socketId, lat, lon);
        nearby.move(socketId, lat, lon);
        global.move(socketId, lat, lon);
    };

    var remove = function(socketId) {
        local.remove(socketId);
        nearby.remove(socketId);
        global.remove(socketId);
    };

    var get = function(socketId, min) {
        var users = [];
        users = local.get(socketId);
        if (users.length < min) {
            users = nearby.get(socketId);
        }

        if (users.length < min) {
            users = global.get(socketId);
        }

        if (users.length < min) {
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
var grid = function(precision, distance) {
    var squares = {};
    var sockets = {};

    var move = function(socketId, lat, lon) {
        var squareLat = snapToGrid(lat);
        var squareLon = snapToGrid(lon);
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

    var get = function(socketId, min) {
        if (precision == 1) {
            return getAll();
        }

        var nearbySockets = [];
        if (!sockets[socketId]) {
            return [];
        }

        var gridSockets = getGridSockets(socketId, sockets[socketId].squareLat, sockets[socketId].squareLon);

        nearbySockets = filterByDistance(sockets[socketId], gridSockets);
        console.log(distance + 'km [grid: ' + gridSockets.length + ', radius: ' + nearbySockets.length + ']');
        if (nearbySockets.length < min) {
            nearbySockets = gridSockets;
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

    function snapToGrid(degree) {
        lastDigit = degree.toFixed(precision).toString().slice(-1);
        var snap = 0;
        if (lastDigit > 7.5) {
            snap = 10;
        } else if (lastDigit > 2.5) {
            snap = 5;
        } else {
            snap = 0;
        }

        return Number(degree.toFixed(precision).toString().slice(0, -1)) + (Math.pow(10, -(precision - 1)) * 0.1 * snap);
    }

    function getGridSockets(socketId, centerLat, centerLon) {
        var s = [];

        var degreeStep = Math.pow(10, -(precision - 1));
        var rect = [
            [centerLat + degreeStep / 2, centerLon + degreeStep / 2],
            [centerLat, centerLon + degreeStep / 2],
            [centerLat + degreeStep / 2, centerLon],
            [centerLat, centerLon]
        ];

        rect.forEach(function(coords) {
            if (squares[coords[0]] && squares[coords[0]][coords[1]]) {
                for (var id in squares[coords[0]][coords[1]]) {
                    s.push(id);
                }
            }
        });

        return s;
    }

    function filterByDistance(socket, gridSockets) {
        var s = [];
        var lat1 = socket.lat;
        var lon1 = socket.lon;
        var log = '';
        gridSockets.forEach(function(id) {
            var lat2 = sockets[id].lat;
            var lon2 = sockets[id].lon;
            var km = geolib.getDistance({latitude: lat1, longitude: lon1}, {latitude: lat2, longitude: lon2}) / 1000;
            log += ' ' + km;
            if (!distance || km <= distance) {
                s.push(id);
            }
        });

        if (log) console.log('{' + log + ' }');
        return s;
    }

    return {
        move: move,
        remove: remove,
        get: get,
        getAll: getAll
    };
};