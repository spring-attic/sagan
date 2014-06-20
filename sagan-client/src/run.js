/*exported sts_import*/
/*global curl*/
var Munchkin, baseUrl, sts_import; // pre-existing globals
(function () {
    var cjsConfig = {
        loader: 'curl/loader/cjsm11'
    };

    curl.config({
        baseUrl: baseUrl || '',
        packages: {
            app: { location: 'app', config: cjsConfig, main: 'main' },
            feature: { location: 'feature', config: cjsConfig },
            component: { location: 'component', config: cjsConfig },
            platform: { location: 'platform', config: cjsConfig },
            curl: { location: 'lib/curl/src/curl/' },
            when: { location: 'lib/when', main: 'when' },
            most: { location: 'lib/most', main: 'most', config: cjsConfig },
            poly: { location: 'lib/poly' }
        },
        paths: {
            leaflet: { location: 'lib/leaflet/dist/leaflet', config: cjsConfig },
            ZeroClipboard: 'lib/zeroclipboard/ZeroClipboard',
            jquery: 'lib/jquery/jquery.min',
            timeago: {
                location: 'lib/jquery-timeago/jquery.timeago',
                config: {
                    requires: [ 'jquery' ],
                    exports: '$.fn.timeago'
                }
            },
            'prettify': 'lib/google-code-prettify/src/prettify',
            bootstrap: {
                location: 'lib/bootstrap/docs/assets/js/bootstrap',
                config: {
                    loader: 'curl/loader/legacy',
                    requires: [ 'jquery' ],
                    // there is no global `bootstrap` var anywhere?
                    exports: '$.fn.tooltip'
                }
            },
            'bootstrap-datetimepicker': {
                location: 'lib/bootstrap-datetimepicker/src/js/bootstrap-datetimepicker',
                config: {
                    loader: 'curl/loader/legacy',
                    requires: [ 'bootstrap' ],
                    exports: '$.fn.datetimepicker'
                }
            },
            'jquery-migrate': {
                location: '//code.jquery.com/jquery-migrate-1.2.1',
                config: {
                    loader: 'curl/loader/legacy',
                    requires: [ 'jquery' ],
                    exports: '$.migrateWarnings'
                }
            },
            munchkin: {
                location: '//munchkin.marketo.net/munchkin',
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

    curl(['app', 'jquery']).then(start, fail);

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
