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
    storage.setItem(guidesBuildPref, cls);
}

function hideBuildSteps() {
    body.removeClass('show-gradle show-maven show-sts');
    storage.setItem(guidesBuildPref, 'none');
}

function registerBuildSwitches() {
    container.on('click', '.use-gradle, .use-maven, .use-sts', hideBuildSteps);

    container.on('click', '.reveal-gradle', revealGradle);
    container.on('click', '.reveal-maven', revealMaven);
    container.on('click', '.reveal-sts', revealSTS);

    if (storage.hasItem(guidesBuildPref)) {
        var preference = storage.getItem(guidesBuildPref);
        if (buildOpts.indexOf(preference) >= 0) {
            reveal(preference);
        } else {
            hideBuildSteps();
        }
    }
}