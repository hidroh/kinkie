var connect = require('connect'), http = require('http'), https = require('https');
var app = connect()
    .use(connect.static('public/'))
    .use(connect.query())
    .use(function(req, res){
        console.log(req.query);
        if (req._parsedUrl.pathname == '/success') {
            var url = "https://graph.facebook.com/oauth/access_token?client_id=461663813917591&redirect_uri=http://kinkie.im/success&client_secret=97f7a1fc314700b1ca7a905385e7d06f&code=" + req.query.code;
            var options = require('url').parse(url);
            options.rejectUnauthorized = false;
            options.agent = new https.Agent(options);
            https.get(options, function(fbRes) {
                fbRes.on('data', function (chunk) {
                    res.writeHead(302, {
                      'Location': 'local:///select.html?' + chunk
                    });
                    res.end();
                    // res.end(chunk);
                });
            }).on('error', function(e) {
                console.log("Got error: " + e.message);
            });
        }
    });
http.createServer(app).listen(80);
