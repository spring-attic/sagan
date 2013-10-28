require('./guide');
require('./searchResults/main');

// Implicit dependency on jQuery contentFilter plugin
require('./filter');

var $ = require('jquery');
var os = require('./os');
var initSearch = require('./search/main');

var searchOpenTrigger = '.js-search-input-open';
var searchCloseTrigger = '.body--container, .js-search-input-close, .homepage--body';
var searchDropdown = '.js-search-dropdown';

$(function () {

  // FIXME: This element does not exist on all pages. This code
  // just relies on jQuery silently doing nothing when it finds no
  // nodes.  Ideally, contentFilter should not be loaded/installed
  // on pages where it is never used.
  $('#doc_filter').contentFilter();

  initSearch(window.location.pathname === '/search', $(searchDropdown), $(searchOpenTrigger), $(searchCloseTrigger));

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

    $('#scrim').addClass('js-show').css('height', scrimHeight).css('top', headerHeight);
    $('#scrim').click(function () {
      $('.js-item-dropdown--wrapper').removeClass('js-open');
      $(this).removeClass('js-show');
    });
  });

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

  moveItemSlider();

  $('.js-item').click(function () {
    $(this).addClass('js-active');
    $(this).siblings().removeClass('js-active');
    moveItemSlider();
  });


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
    $('.viewport').height(deviceHeight).addClass('constrained');
    $('#scrim').addClass('js-show js-open-mobile-nav');
    $('#scrim').click(function() {
      $('.navigation-drawer--container').removeClass('js-open');
      $('.mobile-nav, .body--container, .homepage--body').removeClass('js-slide-right');
      $('.viewport').removeClass('constrained');
    });
  });

  initializeFacetSearchWidget();
});

// TODO: Turn into an item slider component?
function moveItemSlider() {
  var activeItem = $('.js-item-slider--wrapper .js-item.js-active');

  if (activeItem.length === 0 ) {
    return;
  }

  var activeItemPosition = activeItem.position();
  var activeItemOffset = activeItemPosition.left;
  var activeItemWidth = activeItem.outerWidth();

  var slider = $('.js-item--slider');
  var sliderPosition = slider.position();
  var sliderOffset = sliderPosition.left;
  var sliderTarget = activeItemOffset - sliderOffset;

  slider.width(activeItemWidth);
  slider.css('margin-left', sliderTarget);
}

function firstCheckbox(context) {
  context.siblings('.facet--wrapper').find('input[type="checkbox"]').first().prop('checked', false);
}

// TODO: Turn into a facet search component?
// AFAICT, "Search facets" are used to narrow search results, and thus
// are only used on the search results page.
function initializeFacetSearchWidget() {
  var searchFacet = $('.search-facets');

  if (!searchFacet.length) {
    return;
  }

  $('.sub-facet--list, .facet-section--header').addClass('js-close');

  $('.js-toggle-sub-facet-list').click(function() {
    $(this).closest('.facet').find('.sub-facet--list:first').toggleClass('js-close');
  });

  $('.projects-facet .js-toggle-sub-facet-list:first').click(function() {
    $('.facet-section--header').toggleClass('js-close');
  });


  $('.js-checkbox-pill').click(function() {
    var facet = $(this).closest('.facet');
    var group = facet.parents('.facet').first();
    var checkBoxes = facet.find('input[type="checkbox"]');
    var checkBox = checkBoxes.first();
    var uncheckedCheckBoxes = group.find('.sub-facet--list input[type="checkbox"]:not(:checked)');


    if (checkBox.prop('checked') === false){
      //IF IT IS CHECKED

      //UNCHECKES ITSELF
      $(this).prop('checked', false);

      // UNCHECKES HIGHEST PARENT
      firstCheckbox($(this).parents('.sub-facet--list'));

      // UNCHECKS CLOSEST PARENT
      firstCheckbox($(this).closest('.sub-facet--list'));

      // UNCHECKS ALL CHILDRED
      $(this).parents('.facet--wrapper').siblings('.sub-facet--list, .facet-section--header').find('input[type="checkbox"]').prop('checked', false);

    } else {
      //IF IT IS NOT CHECKED

      //CHECKS ALL CHILDRED
      checkBoxes.prop('checked',true);
    }

    if (uncheckedCheckBoxes.length === 0) {
      group.find('input[type="checkbox"]').first().prop('checked', true);
    }
  });

  $('.sub-facet--list').each(function () {
    var checkedCheckBoxes = $('input[type="checkbox"]:checked', this);
    var uncheckedBoxes = $('input[type="checkbox"]:not(:checked)', this);
    if (checkedCheckBoxes.length !== 0 && uncheckedBoxes.length !== 0) {
      $(this).removeClass('js-close');
    }
  });

  if ($('.projects-facet input[type="checkbox"]:checked').length) {
    $('.facet-section--header').removeClass('js-close');
    $('.projects-facet .sub-facet--list').first().removeClass('js-close');
  }

  $('.facets--clear-filters').click(function() {
    $('.facet--wrapper input[type="checkbox"]:checked').prop('checked', false);
    $('.sub-facet--list, .facet-section--header').addClass('js-close');
  });
}
