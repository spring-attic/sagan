var createGuide = require('page/guide/main');
var createGuidesList = require('page/guidesList/main');
var createSearchResults = require('page/searchResults/main');

var os = require('platform/os');

var most = require('most');

var $ = require('jquery');

var SearchController = require('component/search/SearchController');

var navItem = '.nav-search';
var searchDropdown = '.js-search-dropdown';
var searchOpenTrigger = '.js-search-input-open';
var searchCloseTrigger = '.body--container, .js-search-input-close, .homepage--body';
var noAnimationClass = 'no-animation';
var enabledClass = 'js-highlight';
var showHideClass = 'js-show';

var dataAttrRx = /^data-/i;

var capabilities = {
    search: function() {
        return initSearch(window.location.pathname === '/search', $(searchDropdown)[0]);
    },
    'search-facets': function() {
        return createSearchResults($('.search-facets'));
    },
    'filterable-list': function() {
        return createGuidesList(document.getElementById('doc_filter'));
    },
    guide: createGuide,
    'mobile-support': initMobileSupport,
    'info-popups': initInfoPopups,
    'platform-downloads': initPlatformDownloads
};

function initCapabilities() {
    scanCapabilities().map(function(key) {
        return components[key]();
    })
        .reduce(function(initialized, capability) {
            initialized.push(capability);
            return initialized;
        }, [])
        .each(function(capabilities) {
            $(window).unload(function() {
                capabilities.forEach(function(capability) {
                    capability.destroy();
                });
            });
        });
}

function scanCapabilities() {
    return most.fromArray(Array.prototype.slice.call(document.documentElement.attributes))
        .map(function(attr) {
            return attr.name;
        })
        .map(function(name) {
            return dataAttrRx.test(name) && name.slice(5);
        })
        .filter(function(name) {
            return name && name in capabilities;
        });
}

initCapabilities();

function initMobileSupport() {

    window.addEventListener('orientationchange', function() {
        var deviceHeight = $(window).height();
        var openDrawer = $('.js-open-nav-drawer.js-slide-right');
        if (!openDrawer.length) {
            return;
        }

        $('.viewport').height(deviceHeight).addClass('constrained');

    }, false);

    $('.js-open-nav-drawer').click(function() {
        $('.navigation-drawer--container').addClass('js-open');
        $('.mobile-nav, .body--container, .homepage--body').addClass('js-slide-right');

        var deviceHeight = $(window).height();

        $('.viewport')
            .height(deviceHeight)
            .addClass('constrained');

        $('#scrim')
            .addClass('js-show js-open-mobile-nav')
            .click(function() {
                $('.navigation-drawer--container').removeClass('js-open');
                $('.mobile-nav, .body--container, .homepage--body').removeClass('js-slide-right');
                $('.viewport').removeClass('constrained');
            });
    });
}

function initInfoPopups() {
    $(function() {
        // TODO: Turn this into a dropdown component?
        //OPENS ITEM DROPDOWN WIDGET
        $('.js-item--open-dropdown').click(function () {
            var dropdownItem = $(this).parents('.js-item-dropdown--wrapper');
            var documentHeight = $(document).height();
            var headerHeight = $('header').outerHeight();
            var footerHeight = $('footer').outerHeight();
            var scrimHeight = documentHeight - headerHeight - footerHeight;

            dropdownItem.toggleClass('js-open');
            dropdownItem.siblings().removeClass('js-open');
            $(this).parents('.js-item-dropdown-widget--wrapper').siblings().find('.js-item-dropdown--wrapper').removeClass('js-open');

            $('#scrim')
                .addClass('js-show')
                .css({ height: scrimHeight, top: headerHeight})
                .click(function () {
                    $('.js-item-dropdown--wrapper').removeClass('js-open');
                    $(this).removeClass('js-show');
                });
        });
    });
}

function initPlatformDownloads() {
    $(function() {
        // FIXME: Download links exist only on some pages, not all.  This
        // should be moved to a composition plan for those specific pages.
        $('.download-links li.' + os.type() + os.arch()).show();

        // FIXME: AFAICT, these links are only on the tools/eclipse page, so this should
        // be moved to a composition plan for that page.  Note that I extracted this
        // from the sagan-site:resources/templates/tools/eclipse/index.html layout
        // where it was inlined into the page, and depended on a global function that
        // was defined in application.js.  There was no indication of this dependency
        // and I just got lucky in finding it.
        $('#platform--' + os.type())
            .addClass('in')
            .css('overflow', 'visible')
            .css('height', 'auto')
            .parent()
            .find('.platform-dropdown--icon')
            .removeClass('icon-chevron-down')
            .addClass('icon-chevron-up');
    });
}

/**
 * Create and initialize a SearchController for the page-level search
 * component.  Since the component is used on every page, is owned by
 * the page, and interacts with other page-level components (like
 * navigation items) it makes sense to do this initialization at this level.
 * @param {boolean} initiallyVisible should the search component be visible initially
 * @param {Node} searchContainer container node of the search component
 * @returns {SearchController}
 */
function initSearch(initiallyVisible, searchContainer) {
    $(function() {
        var searchController = new SearchController(searchContainer, showHideClass);

        $(searchOpenTrigger).on('click', function() {
            showSearchDropdown();
        });

        $(searchCloseTrigger).on('click', hideSearchDropdown);

        if (initiallyVisible) {
            showSearchDropdown(noAnimationClass);
        }

        return searchController;

        function showSearchDropdown(additionalClass) {
            if(searchController.isShown()) {
                return;
            }

            searchController.show(additionalClass);
            $(navItem).addClass(enabledClass);
        }

        function hideSearchDropdown() {
            if(!searchController.isShown()) {
                return;
            }

            searchController.hide();
            $(navItem).removeClass(enabledClass);
        }
    });
}
