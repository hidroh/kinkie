var location = require('./location');
var io = require('socket.io').listen(8080);
io.set('log level', 1); // reduce logging

var location = new location();

var activeUserSockets = {};
// var activeBotSockets = {};
var chat = io.of('/chat').on('connection', function (socket) {

    var parseJSON = function(jsonString, required) {
        try {
            data = JSON.parse(jsonString);
            for (var i = 0; i < required.length; i++) {
                if (!(required[i] in data)) {
                    sendError('Missing ' + required[i]);
                    return null;
                }
            }
            console.log('Message validated');
            return data;
        } catch (e) {
            sendError('Invalid JSON string');
            return null;
        }
    };

    var sendError = function(message) {
        console.log('Error message sent to client');
        socket.emit('error', {'message': message});
    };

    console.log('Socket client connected');
    socket.on('new user', function(data) {
        console.log('New user joined');
        data = parseJSON(data, ['latitude', 'longitude', 'user_id']);
        if (!data) {
            return;
        }
        activeUserSockets[socket.id] = 
        {
            'socket': socket,
            'user_id': data.user_id
        };
        location.move(socket.id, data.latitude, data.longitude);
    });

    var forward = function(client, jsonData, data) {
        client.socket.emit('msg', data);
        return client.user_id != jsonData.user_id ? 1 : 0;
    };

    socket.on('msg', function(data) {
        console.log('Received message from user');
        jsonData = parseJSON(data, ['latitude', 'longitude', 'user_id', 'geo']);
        if (!jsonData) {
            return;
        }

        var count = 0;
        console.log('Forward message to users within ' + jsonData.geo + 'km');
        var nearby = location.get(socket.id, 10);
        nearby.forEach(function(socketId) {
            if (!activeUserSockets[socketId]) {
                return;
            }

            client = activeUserSockets[socketId];
            if (socketId == socket.id) {
                location.move(socket.id, jsonData.latitude, jsonData.longitude);
            }

            count += forward(client, jsonData, data);
        });

        socket.emit('msg', JSON.stringify({'type': 'info', 'message': count}));
    });

    socket.on('disconnect', function() {
        delete activeUserSockets[socket.id];
        location.remove(socket.id);
        // delete activeBotSockets[socket.id];
        console.log('Socket client disconnected');
    });
});
