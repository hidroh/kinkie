var io = require('socket.io').listen(8080);

// io.configure(function() {
//     io.set("transports", ["jsonp-polling"]);
//     io.set("polling duration", 5);
// });

var connectedClients = [];
var chat = io.of('/chat').on('connection', function (socket) {
    console.log('Client connected');
    // socket.emit('a message', { that: 'only', '/chat': 'will get' });
    // chat.emit('a message', { everyone: 'in', '/chat': 'will get' });
    // socket.on('hi server', function() {
    //     console.log('yesss man');
    // });
    // socket.on('received', function() {
    //     console.log('he received it');
    // });

    socket.on('new user', function(data) {
        console.log('New user connected');
        connectedClients.push(socket);
    })

    socket.on('msg', function(data) {
        console.log('Received:');
        for (var index in connectedClients) {
            if (connectedClients != socket) {
                connectedClients[index].emit('msg', data);
            }
        }
    })
  });

// var news = io.of('/news').on('connection', function (socket) {
//     console.log('sending news');
//     socket.emit('item', { news: 'item' });
//   });