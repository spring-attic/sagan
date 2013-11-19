var filterableList = require('component/filterableList/filterableList');
var $ = require('jquery');

module.exports = function(filterInput) {
    var filterList;

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        var containers = $('[data-filterable-container]').get();
        filterList = filterableList(onFilterMatch, 'data-filterable', containers);

        $(filterInput).on('keyup', filterList);

        function onFilterMatch(matched, node) {
            $(node).toggleClass('filterable-non-matching', !matched);
        }
    }

    function destroy() {
        if(filterList) {
            $(filterInput).off('keyup', filterList);
        }
    }
};