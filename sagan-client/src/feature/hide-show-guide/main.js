var $ = require('jquery');

module.exports = initHideShowGuide;

function initHideShowGuide() {

    var plan = {
        ready: ready,
        destroy: function() {}
    }

    function ready () {
        registerBuildSwitches();
    }

    $(function () { plan.ready(); }.bind(plan));

    return plan;
}

// The callback is function(index, value)
function forEach(array, callback, scope) {
    for (var i = 0; i < array.length; i++) {
        callback.call(scope, i, array[i]); // passes back stuff we need
    }
}


var body = $('body');
var guidePreferences = '_guide_preferences';

var docCookies = {
    getItem: function (sKey) {
        return decodeURIComponent(document.cookie.replace(new RegExp("(?:(?:^|.*;)\\s*" + encodeURIComponent(sKey).replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=\\s*([^;]*).*$)|^.*$"), "$1")) || null;
    },
    setItem: function (sKey, sValue, vEnd, sPath, sDomain, bSecure) {
        if (!sKey || /^(?:expires|max\-age|path|domain|secure)$/i.test(sKey)) { return false; }
        var sExpires = "";
        if (vEnd) {
            switch (vEnd.constructor) {
                case Number:
                    sExpires = vEnd === Infinity ? "; expires=Fri, 31 Dec 9999 23:59:59 GMT" : "; max-age=" + vEnd;
                    break;
                case String:
                    sExpires = "; expires=" + vEnd;
                    break;
                case Date:
                    sExpires = "; expires=" + vEnd.toUTCString();
                    break;
            }
        }
        document.cookie = encodeURIComponent(sKey) + "=" + encodeURIComponent(sValue) + sExpires + (sDomain ? "; domain=" + sDomain : "") + (sPath ? "; path=" + sPath : "") + (bSecure ? "; secure" : "");
        return true;
    },
    removeItem: function (sKey, sPath, sDomain) {
        if (!sKey || !this.hasItem(sKey)) { return false; }
        document.cookie = encodeURIComponent(sKey) + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT" + ( sDomain ? "; domain=" + sDomain : "") + ( sPath ? "; path=" + sPath : "");
        return true;
    },
    hasItem: function (sKey) {
        return (new RegExp("(?:^|;\\s*)" + encodeURIComponent(sKey).replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=")).test(document.cookie);
    },
    keys: /* optional method: you can safely remove it! */ function () {
        var aKeys = document.cookie.replace(/((?:^|\s*;)[^\=]+)(?=;|$)|^\s*|\s*(?:\=[^;]*)?(?:\1|$)/g, "").split(/\s*(?:\=[^;]*)?;\s*/);
        for (var nIdx = 0; nIdx < aKeys.length; nIdx++) { aKeys[nIdx] = decodeURIComponent(aKeys[nIdx]); }
        return aKeys;
    }
};

function revealGradle() {
    reveal('gradle');
}

function revealMaven() {
    reveal('maven');
}

function revealSTS() {
    reveal('sts');
}

function reveal(cls) {
    console.log('Revealing ' + cls + ' stuff...');
    body.removeClass('show-gradle show-maven show-sts');
    body.addClass('show-' + cls);
    docCookies.setItem(guidePreferences, cls, Infinity, '/guides/gs');
    console.log(docCookies.getItem(guidePreferences));
}

function hideBuildSteps() {
    body.removeClass('show-gradle show-maven show-sts');
    docCookies.setItem(guidePreferences, 'none', Infinity, '/guides/gs');
    console.log(docCookies.getItem(guidePreferences));
}

function registerBuildSwitches() {
    console.log('Registering event listeners...');
    forEach(document.querySelectorAll('.reveal-gradle'), function(index, node) {
        console.log(node);
        node.addEventListener('click', revealGradle)
    });
    forEach(document.querySelectorAll('.reveal-maven'), function(index, node) {
        node.addEventListener('click', revealMaven)
    });
    forEach(document.querySelectorAll('.reveal-sts'), function(index, node) {
        node.addEventListener('click', revealSTS)
    });

    forEach(document.querySelectorAll('.use-gradle'), function(index, node) {
        node.addEventListener('click', hideBuildSteps)
    });
    forEach(document.querySelectorAll('.use-maven'), function(index, node) {
        node.addEventListener('click', hideBuildSteps)
    });
    forEach(document.querySelectorAll('.use-sts'), function(index, node) {
        node.addEventListener('click', hideBuildSteps)
    });

    console.log(docCookies.getItem(guidePreferences));

    if (docCookies.hasItem(guidePreferences)) {
        var preference = docCookies.getItem(guidePreferences);
        console.log("Found " + preference + " already set!");
        if (preference === 'gradle') {
            revealGradle();
        } else if (preference === 'maven') {
            revealMaven();
        } else if (preference === 'sts') {
            revealSTS();
        } else {
            hideBuildSteps();
        }
    } else {
        console.log("Don't have " + guidePreferences + " inside docCookies!");
    }
}