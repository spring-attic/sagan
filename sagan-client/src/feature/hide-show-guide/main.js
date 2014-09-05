var $ = require('jquery');
var storageProvider = require('./storage');
var storage = storageProvider();
var body = $('body');
var container = $('.content--container');

var guidesBuildPref = '/guides/gs/build';
var buildOpts = ['gradle', 'maven', 'sts'];


module.exports = initHideShowGuide;

function initHideShowGuide() {

    var plan = {
        ready: ready,
        destroy: function() {
            container.off('click', '.use-gradle, .use-maven, .use-sts', hideBuildSteps);
            container.off('click', '.reveal-gradle', revealGradle);
            container.off('click', '.reveal-maven', revealMaven);
            container.off('click', '.reveal-sts', revealSTS);
        }
    }

    function ready () {
        registerBuildSwitches();
    }

    $(ready);

    return plan;
}

function revealGradle(e) {
    reveal('gradle', e);
}

function revealMaven(e) {
    reveal('maven', e);
}

function revealSTS(e) {
    reveal('sts', e);
}

function reveal(cls, e) {
    hideBuildSteps();
    body.addClass('show-' + cls);
    storage.setItem(guidesBuildPref, cls);

    if (e !== undefined) {
        if (e.currentTarget.className.indexOf('reveal-gradle') !== -1) {
            document.querySelector('#scratch').scrollIntoView(true);
        } else if (e.currentTarget.className.indexOf('reveal-maven') !== -1) {
            document.querySelector('#use-maven').scrollIntoView(true);
        } else if (e.currentTarget.className.indexOf('reveal-sts') !== -1) {
            document.querySelector('#use-sts').scrollIntoView(true);
        } else if (e.currentTarget.className.indexOf('use-gradle') !== -1) {
            document.querySelector('#reveal-gradle').scrollIntoView(true);
        } else if (e.currentTarget.className.indexOf('use-maven') !== -1) {
            document.querySelector('#reveal-maven').scrollIntoView(true);
        } else if (e.currentTarget.className.indexOf('use-sts') !== -1) {
            document.querySelector('#reveal-sts').scrollIntoView(true);
        }
    }
}

function hideBuildSteps() {
    body.removeClass('show-gradle show-maven show-sts');
    storage.setItem(guidesBuildPref, 'none');
}

function registerBuildSwitches() {
    container.on('click', '.use-gradle, .use-maven, .use-sts', hideBuildSteps);

    container.on('click', '.reveal-gradle', function(e) {
        revealGradle(e);
    });
    container.on('click', '.reveal-maven', function(e) {
        revealMaven(e);
    });
    container.on('click', '.reveal-sts', function(e) {
        revealSTS(e);
    });

    if (storage.hasItem(guidesBuildPref)) {
        var preference = storage.getItem(guidesBuildPref);
        if (buildOpts.indexOf(preference) >= 0) {
            reveal(preference, undefined);
        } else {
            hideBuildSteps();
        }
    }
}