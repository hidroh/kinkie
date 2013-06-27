var connect = require('connect');
connect.createServer(connect.static('/var/www/kinkie.im/public/')).listen(80);