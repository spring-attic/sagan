import $ from 'jquery';
import * as JsSearch from 'js-search';
import '../css/guide.css'

$(document).ready(function () {
    var guides = [];
    var _guides = $('div.guide-search');
    var noResult = $('#guide-search-no-result');
    if (_guides.length > 0) {
        _guides.each(function (index) {
            guides.push({
                index: index,
                obj: $(this),
                str: $(this).attr('data-filterable')
            })
        });
        var search = new JsSearch.Search('index');
        search.addIndex('str');
        search.addDocuments(guides);
        $('#guide-search').keyup(function () {
            noResult.removeClass('show');
            if ($(this).val().trim() === '') {
                _guides.removeClass('hide');
            } else {
                var result = search.search($(this).val());
                _guides.addClass('hide');
                $.each(result, function (index, value) {
                    value.obj.removeClass('hide');

                });
                if (result.length === 0) {
                    noResult.addClass('show');
                }
            }
        })
    }
});