var $ = require('jquery');
var docCookies = require('./docCookie');

var body = $('body');
var container = $('.content--container');

var guidePreferences = '_guide_preferences';
var preferencePath = '/guides/gs';
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
    hideBuildSteps();
    body.addClass('show-' + cls);
    docCookies.setItem(guidePreferences, cls, Infinity, preferencePath);
}

function hideBuildSteps() {
    body.removeClass('show-gradle show-maven show-sts');
    docCookies.setItem(guidePreferences, 'none', Infinity, preferencePath);
}

function registerBuildSwitches() {
    container.on('click', '.use-gradle, .use-maven, .use-sts', hideBuildSteps);

    container.on('click', '.reveal-gradle', revealGradle);
    container.on('click', '.reveal-maven', revealMaven);
    container.on('click', '.reveal-sts', revealSTS);

    if (docCookies.hasItem(guidePreferences)) {
        var preference = docCookies.getItem(guidePreferences);
        if (preference in buildOpts) {
            reveal(preference);
        } else {
            hideBuildSteps();
        }
    }
}