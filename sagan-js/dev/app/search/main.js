var $ = require('jquery');
var SearchController = require('./SearchController');

var enabledClass = 'js-highlight';
var shownClass = 'js-show';
var noAnimationClass = 'no-animation';
var navItem = '.nav-search';

module.exports = initSearch;

function initSearch(initiallyVisible, dropdown, openTrigger, closeTrigger) {

  var searchController = new SearchController(dropdown, shownClass);

  searchController.setValue($(dropdown).attr('value'));

  $(openTrigger).on('click', function() {
    showSearchDropdown();
  });

  $(closeTrigger).on('click', function() {
    hideSearchDropdown();
  });

  if (initiallyVisible) {
    showSearchDropdown(noAnimationClass);
  }

  return {
    show: showSearchDropdown,
    hide: hideSearchDropdown
  };

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
}

