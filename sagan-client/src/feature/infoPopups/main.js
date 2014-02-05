var $ = require('jquery');
var openItemPopupsTrigger = '.js-item--open-dropdown';
var itemPopupWrapper = '.js-item-dropdown--wrapper';
var itemPopupOpen = 'js-open';
var itemPopupWrapperOpen = itemPopupWrapper + '.' + itemPopupOpen;

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

/**
 * Show/hide the item popup whose toggle was clicked
 * @param {Event} e
 */
function handleItemPopup(e) {
    // Enabling the document-level click trap within this event handler
    // causes disableClickTrap to be executed as part of the same event turn,
    // thus *hiding* (via hideAll) the popup that was just shown.
    // Stopping propagation prevents the document-level click handler
    // from executing immediately after this handler.
    e.stopImmediatePropagation();

    var target = $(this).parents(itemPopupWrapper);

    if(target.hasClass(itemPopupOpen)) {
        target.removeClass(itemPopupOpen);
    } else {
        hideAll();
        enableClickTrap();
        target.addClass(itemPopupOpen);
    }
}

function enableClickTrap() {
    $(document).on('click', disableClickTrap);
}

function disableClickTrap() {
    $(document).off('click', disableClickTrap);
    hideAll();
}

function hideAll() {
    $(itemPopupWrapperOpen).removeClass(itemPopupOpen);
}
