var location = require('./location');
var io = require('socket.io').listen(8080);
io.set('log level', 1); // reduce logging

var location = new location();

var users = {};
var businesses = {};
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
        users[socket.id] = 
        {
            'socket': socket,
            'user_id': data.user_id
        };
        location.move(socket.id, data.latitude, data.longitude);
    });

    socket.on('new business', function(data) {
        console.log('New business joined');
        data = parseJSON(data, ['latitude', 'longitude']);
        if (!data) {
            return;
        }

        businesses[socket.id] = 
        {
            'latitude': data.latitude,
            'longitude': data.longitude,
            'socket': socket,
        };
    });

    socket.on('msg', function(data) {
        console.log('Received message from user');
        jsonData = parseJSON(data, ['latitude', 'longitude', 'user_id']);
        if (!jsonData) {
            return;
        }

        var count = 0;
        var nearby = location.get(socket.id, 10);
        nearby.forEach(function(socketId) {
            if (!users[socketId]) {
                return;
            }

            client = users[socketId];
            if (socketId == socket.id) {
                location.move(socket.id, jsonData.latitude, jsonData.longitude);
            } else {
                count++;
            }

            client.socket.emit('msg', data);
        });

        console.log('Forwarded message to ' + count + ' users');
        socket.emit('msg', JSON.stringify({'type': 'info', 'message': count}));

        for (var socketId in businesses) {
            businesses[socketId].socket.emit('msg', JSON.stringify({'message': jsonData.message, 'socket_id': socket.id }));
        }
    });

    socket.on('promotion', function(data) {
        console.log('Received promotion from business');
        jsonData = parseJSON(data, ['latitude', 'longitude', 'promotion_id']);
        if (!jsonData) {
            return;
        }

        console.log(users);
        console.log(jsonData.promotion_id);
        users[jsonData.promotion_id].socket.emit('msg', data);
        console.log('Forwarded promotion to user');
    });

    socket.on('disconnect', function() {
        delete users[socket.id];
        delete businesses[socket.id];
        location.remove(socket.id);
        console.log('Socket client disconnected');
    });
});
