var io = require('socket.io').listen(8080);
io.set('log level', 1); // reduce logging

// // Change transports method
// io.configure(function() {
//     io.set("transports", ["jsonp-polling"]);
//     io.set("polling duration", 5);
// });

var getDistance = function(lat1, lon1, lat2, lon2) {
    var R = 6371; // km
    return Math.acos(Math.sin(lat1)*Math.sin(lat2) + 
                      Math.cos(lat1)*Math.cos(lat2) *
                      Math.cos(lon2-lon1)) * R;
};

var activeUserSockets = {};
var activeBotSockets = {};
var chat = io.of('/chat').on('connection', function (socket) {
    console.log('Socket client connected');
    socket.on('new user', function(data) {
        console.log('New user joined');
        data = JSON.parse(data);
        activeUserSockets[socket.id] = 
        {
            'latitude': data.latitude,
            'longitude': data.longitude,
            'socket': socket,
            'user_id': data.user_id
        };
    });

    socket.on('new business', function(data) {
        console.log('New business joined');
        data = JSON.parse(data);
        activeBotSockets[socket.id] = 
        {
            'latitude': data.latitude,
            'longitude': data.longitude,
            'socket': socket,
        };
    });

    var forward = function(client, jsonData, data) {
        var km = getDistance(client.latitude, client.longitude, jsonData.latitude, jsonData.longitude);
        if (jsonData.geo < 0 || km < jsonData.geo) {
            client.socket.emit('msg', data);
            return 1;
        }

        return 0;
    }

    socket.on('msg', function(data) {
        console.log('Received message from user');
        jsonData = JSON.parse(data);
        var count = 0;
        console.log('Forward message to users within ' + jsonData.geo + 'km');
        for (var socketId in activeUserSockets) {
            client = activeUserSockets[socketId];
            if (socketId == socket.id) {
                client.latitude = jsonData.latitude;
                client.longitude = jsonData.longitude;
                console.log('Updated user location');
            }

            count += forward(client, jsonData, data);
        }

        console.log('Forward message to businesses within ' + jsonData.geo + 'km');
        for (var socketId in activeBotSockets) {
            client = activeBotSockets[socketId];
            forward(client, jsonData, data);
        }

        socket.emit('msg', JSON.stringify({'type': 'info', 'message': count - 1})); // exclude self
    });

    socket.on('promotion', function(data) {
        console.log('Received promotion from business');
        jsonData = JSON.parse(data);
        console.log('Forward promotion to users within ' + jsonData.geo + 'km');
        for (var socketId in activeUserSockets) {
            client = activeUserSockets[socketId];
            if (client.user_id == jsonData.promotion_id) {
                client.socket.emit('msg', data);
            }
        }
    });

    socket.on('disconnect', function() {
        delete activeUserSockets[socket.id];
        delete activeBotSockets[socket.id];
        console.log('Socket client disconnected');
    });
});
