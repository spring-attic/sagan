// TODO: This file is temporarily acting as the composition plan
// for the search results filter form.  It should be ported to a
// proper wire spec.

var $ = require('jquery');
var filterForm = require('./filterForm');

$(function() {
  $('.search-facets').on('submit', filterForm);
});
