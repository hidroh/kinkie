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
        console.log('Getting local sockets');
        users = local.get(socketId);
        if (users.length < min) {
            console.log('Trying to get nearby sockets');
            users = nearby.get(socketId);
        }

        if (users.length < min) {
            console.log('Trying to get global sockets');
            users = global.get(socketId);
        }

        if (users.length < min) {
            console.log('Getting all sockets');
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

        var gridSockets = getGridSockets(sockets[socketId].squareLat, sockets[socketId].squareLon);

        nearbySockets = filterByDistance(sockets[socketId], gridSockets);
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

    function getGridSockets(centerLat, centerLon) {
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

        console.log('Found ' + s.length + ' in grid');
        return s;
    }

    function filterByDistance(socket, gridSockets) {
        var s = [];
        var lat1 = socket.lat;
        var lon1 = socket.lon;
        gridSockets.forEach(function(id) {
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
        var distance = Math.acos(Math.sin(lat1)*Math.sin(lat2) + 
                          Math.cos(lat1)*Math.cos(lat2) *
                          Math.cos(lon2-lon1)) * R;
        console.log('Distance = ' + distance + ' km');
        return distance;
    }

    return {
        move: move,
        remove: remove,
        get: get,
        getAll: getAll
    };
};