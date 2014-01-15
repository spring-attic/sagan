var filterableList = require('./filterableList');
var getUrlFilter = require('./getUrlFilter');
var $ = require('jquery');

module.exports = createFilterableList;

function createFilterableList() {
    var filterList, filterInput;

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        var containers = $('[data-filterable-container]').get();
        var initialFilter = getUrlFilter();

        filterInput = document.getElementById('doc_filter');
        filterList = filterableList(onFilterMatch, 'data-filterable', containers);

        if (initialFilter) {
            $(filterInput).val(initialFilter);
            filterList(initialFilter);
        }
        // jquery has a "paste" event, but it fires too late.
        // "keyup" and "input" should catch mostly everything.
        $(filterInput).on('keyup input', onInputChange);

        function onFilterMatch(matched, node) {
            $(node).toggleClass('filterable-non-matching', !matched);
        }
    }

    function onInputChange (e) {
        filterList(e.target.value);
    }

    function destroy() {
        if(filterList) {
            $(filterInput).off('keyup input', filterList);
        }
    }
}
