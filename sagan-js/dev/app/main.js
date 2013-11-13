var createGuide = require('page/guide/main');
var createSearchResults = require('page/searchResults/main');

var sts = require('platform/sts');
var os = require('platform/os');

var meld = require('meld');
var after = meld.after;

var $ = require('jquery');

var SearchController = require('component/search/SearchController');
var filter = require('component/filterable/filter');

var navItem = '.nav-search';
var searchDropdown = '.js-search-dropdown';
var searchOpenTrigger = '.js-search-input-open';
var searchCloseTrigger = '.body--container, .js-search-input-close, .homepage--body';
var noAnimationClass = 'no-animation';
var enabledClass = 'js-highlight';
var showHideClass = 'js-show';

var guidePage = createGuide();

// ensure that we connect to STS when the guide page is ready
after(guidePage, 'ready', sts.bind(null, '.gs-guide-import'));

$(function () {

    var searchFacets = $('.search-facets');

    var searchResultsPage;
    if(searchFacets.length) {
        searchResultsPage = createSearchResults(searchFacets);
        searchResultsPage.ready();
    }

    // FIXME: This element does not exist on all pages. This code
    // just relies on jQuery silently doing nothing when it finds no
    // nodes.  Ideally, contentFilter should not be loaded/installed
    // on pages where it is never used.
    var filterDocs = filter.create(
        function (matched, node) {
            $(node).toggleClass('filterable-non-matching', !matched);
        },
        $('[data-filterable-container]').get().map(function (node) {
            return {
                node: node,
                children: $('[data-filterable]', node).get()
            };
        })
    );

    $('#doc_filter').on('keyup', function (e) {
        var rx = new RegExp(e.target.value, 'i');
        filterDocs(function (node) {
            return rx.test(node.getAttribute('data-filterable'));
        });
    });

  initSearch(window.location.pathname === '/search', $(searchDropdown).get(0));

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
}
