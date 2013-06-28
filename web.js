var connect = require('connect'), http = require('http'), https = require('https');
var app = connect()
    .use(connect.static('public/'))
    .use(connect.query())
    .use(function(req, res){
        console.log(req.query);
        if (req._parsedUrl.pathname == '/success') {
            var url = "https://graph.facebook.com/oauth/access_token?client_id=550033468382419&redirect_uri=http://kinkie.local:8081/success&client_secret=1a9edcc9fd1dd56894eab6c93eaff41f&code=" + req.query.code;
            https.get(url, function(fbRes) {
                fbRes.on('data', function (chunk) {
                    res.writeHead(302, {
                      'Location': req.headers.referer + '?' + chunk
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
