/*global curl,baseUrl*/
(function (curl, baseUrl) {

    var cjsConfig = {
        loader: 'curl/loader/cjsm11'
    };

    curl.config({
        baseUrl: baseUrl,
        packages: {
            curl: { location: 'lib/curl/src/curl/' },
            app: { location: 'app', config: cjsConfig }
        }
    });

    curl(['app/main']).then(start, fail);

    function start (/*main*/) {
        // TODO: are there any startup tasks at this level?
        console.log('it runs!');
    }

    function fail (ex) {
        // TODO: show a meaningful error to the user.
        console.log(ex);
    }

}(curl, baseUrl));
