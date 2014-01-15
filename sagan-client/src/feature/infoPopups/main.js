var $ = require('jquery');
var openItemPopupsTrigger = '.js-item--open-dropdown';

module.exports = function initInfoPopups() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        $(openItemPopupsTrigger).on('click', handleItemPopup);
    }

    function destroy() {
        $(openItemPopupsTrigger).off('click', handleItemPopup);
    }
};

function handleItemPopup() {
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
}
