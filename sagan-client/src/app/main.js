var initSearch = require('src/feature/search/main');
var initSearchFacets = require('src/feature/searchFacets/main');
var initFilterableList = require('src/feature/filterableList/main');
var initClipboardButtons = require('src/feature/clipboardButtons/main');
var initCodeSidebar = require('src/feature/codeSidebar/main');
var initStsImport = require('src/feature/stsImport/main');
var initMobileSupport = require('src/feature/mobileSupport/main');
var initInfoPopups = require('src/feature/infoPopups/main');
var initPlatformDownloads = require('src/feature/platformDownloads/main');
var initFormWidgets = require('src/feature/formWidgets/main');
var initPrettify = require('src/feature/prettify/main');
var initMap = require('src/feature/map/main');
var initTimeAgo = require('src/feature/timeAgo/main');
var initHideShowGuide = require('src/feature/hide-show-guide/main');
var initHeroBanner = require('src/feature/heroBanner/main');

var most = require('most');
var $ = require('jquery');

var slice = Array.prototype.slice;
var dataAttrRx = /^data-/i;

var features = {
    search: initSearch,
    'search-facets': initSearchFacets,
    'filterable-list': initFilterableList,
    'clipboard-buttons': initClipboardButtons,
    'code-sidebar': initCodeSidebar,
    'sts-import': initStsImport,
    'mobile-support': initMobileSupport,
    'info-popups': initInfoPopups,
    'platform-downloads': initPlatformDownloads,
    'form-widgets': initFormWidgets,
    'code-prettify': initPrettify,
    'map': initMap,
    'timeago': initTimeAgo,
    'hide-show-guide': initHideShowGuide,
    'hero-banner': initHeroBanner
};

initFeatures(features, document).each(function(features) {
    $(window).unload(function() {
        destroyFeatures(features);
    });
});

/**
 * Scans the document for the set of desired features, initializes
 * each, and returns a Stream containing a single array, whose contents
 * are the initialized features.
 * @param features
 * @param document
 * @returns {Object|*}
 */
function initFeatures(features, document) {
    return scanFeatures(features, document)
        .map(function(key) {
            return features[key]();
        })
        .reduce(function(initialized, feature) {
            initialized.push(feature);
            return initialized;
        }, []);
}

/**
 *
 * @param {object} features hash of feature initializers by name
 * @param {Document} document Document whose <html> is annotated with the
 *  set of desired features
 * @returns {object} most.js Stream containing the string names of
 *  the desired features
 */
function scanFeatures(features, document) {
    return most.fromArray(slice.call(document.documentElement.attributes))
        .map(function(attr) {
            var name = attr.name;
            return dataAttrRx.test(name) && name.slice(5);
        })
        .filter(function(name) {
            return name && name in features;
        });
}

function destroyFeatures(features) {
    features.forEach(function(feature) {
        feature.destroy();
    });
}
