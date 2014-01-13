// TODO:
// 1. Split out facet filtering component
// 2. Jsdoc
// 3. Unit and functional tests

module.exports = function() {
    var searchResults = new SearchResults($('.search-facets'));
    $(searchResults.ready.bind(searchResults));

    return searchResults;
};

var $ = require('jquery');
var filterForm = require('./filterForm');

function SearchResults(searchFacets) {
    this.searchFacets = searchFacets;
}

SearchResults.prototype = {
    ready: function() {
        this.searchFacets.on('submit', filterForm);
        this.facetSectionToggle = $('.projects-facet .js-toggle-sub-facet-list:first');
        this.facetCheckboxes = $('.js-checkbox-pill');
        this.subFacetToggle = $('.js-toggle-sub-facet-list');

        $('.sub-facet--list, .facet-section--header').addClass('js-close');

        this.subFacetToggle.on('click', toggleSubFacets);
        this.facetSectionToggle.on('click', toggleFacetSectionHeaders);
        this.facetCheckboxes.on('click', syncFacetStatus);

        this._destroy = function() {
            this.searchFacets.off('submit', filterForm);
            this.facetCheckboxes.off('click', syncFacetStatus);
            this.facetSectionToggle.off('click', toggleFacetSectionHeaders);
            this.subFacetToggle.off('click', toggleSubFacets);
        };

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

    },

    destroy: function() {
        this._destroy();
    },

    _destroy: function() {}
};

function toggleFacetSectionHeaders() {
    $('.facet-section--header').toggleClass('js-close');
}

function toggleSubFacets() {
    $(this).closest('.facet').find('.sub-facet--list:first').toggleClass('js-close');
}

function syncFacetStatus() {
    var $this = $(this);
    var facet = $this.closest('.facet');
    var group = facet.parents('.facet').first();
    var checkBoxes = facet.find('input[type="checkbox"]');
    var checkBox = checkBoxes.first();
    var uncheckedCheckBoxes = group.find('.sub-facet--list input[type="checkbox"]:not(:checked)');


    if (checkBox.prop('checked') === false){
        //IF IT IS CHECKED

        //UNCHECKES ITSELF
        $this.prop('checked', false);

        // UNCHECKES HIGHEST PARENT
        uncheckFirstCheckbox($this.parents('.sub-facet--list'));

        // UNCHECKS CLOSEST PARENT
        uncheckFirstCheckbox($this.closest('.sub-facet--list'));

        // UNCHECKS ALL CHILDRED
        $this.parents('.facet--wrapper')
            .siblings('.sub-facet--list, .facet-section--header')
            .find('input[type="checkbox"]')
            .prop('checked', false);

    } else {
        //IF IT IS NOT CHECKED

        //CHECKS ALL CHILDRED
        checkBoxes.prop('checked',true);
    }

    if (uncheckedCheckBoxes.length === 0) {
        group.find('input[type="checkbox"]').first().prop('checked', true);
    }
}

function uncheckFirstCheckbox(context) {
    context.siblings('.facet--wrapper').find('input[type="checkbox"]').first().prop('checked', false);
}
