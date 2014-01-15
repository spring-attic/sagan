var SearchController = require('./SearchController');
var $ = require('jquery');

var searchDropdown = '.js-search-dropdown';
var navItem = '.nav-search';
var searchOpenTrigger = '.js-search-input-open';
var searchCloseTrigger = '.body--container, .js-search-input-close, .homepage--body';
var noAnimationClass = 'no-animation';
var enabledClass = 'js-highlight';
var showHideClass = 'js-show';

/**
 * Initialize the search feature
 * @returns {{destroy: function()}}
 */
module.exports = function initSearch() {
    return initSearchController(window.location.pathname === '/search', $(searchDropdown)[0]);
};

/**
 * Create and initialize a SearchController for the page-level search
 * feature.
 * @param {boolean} initiallyVisible should the search component be visible initially
 * @param {Node} searchContainer container node of the search component
 * @returns {{destroy: function()}}
 */
function initSearchController(initiallyVisible, searchContainer) {
    var searchController;

    $(function() {
        searchController = new SearchController(searchContainer, showHideClass);

        $(searchOpenTrigger).on('click', function() {
            showSearchDropdown();
        });

        $(searchCloseTrigger).on('click', hideSearchDropdown);

        if (initiallyVisible) {
            showSearchDropdown(noAnimationClass);
        }

        return searchController;

        function showSearchDropdown(additionalClass) {
            searchController.show(additionalClass);
            $(navItem).addClass(enabledClass);
        }

        function hideSearchDropdown() {
            searchController.hide();
            $(navItem).removeClass(enabledClass);
        }
    });

    return {
        destroy: function() {}
    };
}
