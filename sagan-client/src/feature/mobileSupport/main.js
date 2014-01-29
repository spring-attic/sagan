var $ = require('jquery');
var openNavDrawerTrigger = '.js-open-nav-drawer';

module.exports = function initMobileSupport() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        window.addEventListener('orientationchange', handleOrientationChange, false);
        $(openNavDrawerTrigger).on('click', handleOpenNavDrawer);
    }

    function destroy() {
        $(openNavDrawerTrigger).off('click', handleOpenNavDrawer);
        window.removeEventListener('orientationchange', handleOrientationChange, false);
    }
};

function handleOrientationChange() {
    var openDrawer = $('.js-open-nav-drawer.js-slide-right');
    if (!openDrawer.length) {
        return;
    }

    $('.viewport').height($(window).height()).addClass('constrained');
}

function handleOpenNavDrawer() {
    $('.navigation-drawer--container').addClass('js-open');
    $('.mobile-nav, .body--container, .homepage--body').addClass('js-slide-right');

    var deviceHeight = $(window).height();

    $('.viewport')
        .height(deviceHeight)
        .addClass('constrained');

    $('#scrim')
        .addClass('js-show js-open-mobile-nav')
        .on('click', hideMobileNavDrawer);
}

function hideMobileNavDrawer() {
    $('#scrim')
        .removeClass('js-show js-open-mobile-nav')
        .off('click', hideMobileNavDrawer);

    $('.navigation-drawer--container').removeClass('js-open');
    $('.mobile-nav, .body--container, .homepage--body').removeClass('js-slide-right');
    $('.viewport').removeClass('constrained');
}