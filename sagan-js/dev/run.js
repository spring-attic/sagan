var Munchkin, curl, baseUrl; // pre-existing globals
(function () {

    var cjsConfig = {
        loader: 'curl/loader/cjsm11'
    };

    curl.config({
        baseUrl: baseUrl,
        packages: {
          curl: { location: 'lib/curl/src/curl/' },
          app: { location: 'app', config: cjsConfig, main: 'main' },
          poly: { location: 'lib/poly' }
        },
        paths: {
            ZeroClipboard: '../lib/zeroclipboard/ZeroClipboard.min',
            jquery: '../lib/jquery/jquery.min',
            bootstrap: {
                location: '../bootstrap/js/bootstrap.min.js',
                config: {
                    loader: 'curl/loader/legacy',
                    requires: [ 'jquery' ],
                    // there is no global bootstrap var anywhere?
                    exports: '$.fn.tooltip'
                }
            },
            'jquery-migrate': {
                location: '//code.jquery.com/jquery-migrate-1.2.1.js',
                config: {
                    loader: 'curl/loader/legacy',
                    requires: [ 'jquery' ],
                    exports: '$.migrateWarnings'
                }
            },
            munchkin: {
                location: '//munchkin.marketo.net/munchkin.js',
                config: {
                    loader: 'curl/loader/legacy',
                    requires: [ 'jquery', 'jquery-migrate' ],
                    factory: function() {
                        Munchkin.init('625-IUJ-009');
                        return Munchkin;
                    }
                }
            }
        },
        preloads: ['poly/es5']
    });

    // Can't document this in bower.json, so I'm documenting it here:
    // munchkin.js has a bug that incorrectly detects jquery 1.10.x as
    // being 1.1.x, below its required 1.3.x.  It proceeds to download jquery
    // 1.7.1 ("jquery-latest") which overwrites any existing jquery in
    // curl.js's cache. The newer jquery doesn't have any plugins
    // registered, so calls to $().tooltip, etc. fail.
    // To fix this, the bower.json cannot specify anything over ~1.9.  However,
    // munchkin has another bug: it uses an obsolete feature: $.browser.
    // This was deprecated as of 1.3.0 and removed in 1.9.0, but is still
    // used in munchkin.js.  Therefore, we must use the jquery-migrate plugin.
    // Also: I'm ensuring munchkin.js gets loaded last!

    curl(['app', 'jquery', 'app/filter']).then(start, fail);

    function start(main, $) {
        // tell the jquery migrate plugin to be quiet
        $.migrateMute = true;
        // load munchkin once our page is loaded
        curl(['munchkin']);
    }

    function fail(ex) {
        // TODO: show a meaningful error to the user.
        //document.location.href = '/500';
        throw ex;
    }

}());
