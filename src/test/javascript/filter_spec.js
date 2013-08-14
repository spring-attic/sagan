describe('Filter', function () {

  beforeEach(function () {
    $('body').append('<div id="jasmine_content"></div>');

    $("#jasmine_content").append("<input type=text id='filter_input'/>");

    $("#jasmine_content").append("<div id='visible_container' data-filterable-container style='display:none;'>" +
      "<div style='display:none;' id='exact_match' data-filterable='string to filter'></div>" +
      "<div style='display:none;' id='partial_match' data-filterable='padding string to filter padding'></div>" +
      "<div id='another_filterable' data-filterable='another thing to filter'></div>" +
      "</div>");

    $("#jasmine_content").append("<div id='container_to_hide' data-filterable-container>" +
      "<div id='last_in_container_filterable' data-filterable='another thing to filter'></div>" +
      "</div>");

    $("#jasmine_content").append("<div id='not_filterable'>string to filter</div>");

    $('#filter_input').filter();

    $("#filter_input").val('string to filter').keyup();
  });

  afterEach(function () {
    $('#jasmine_content').remove();
  });

  it("shows exactly matched filterables", function () {
    expect($('#exact_match')).toBeVisible();
  });

  it("shows non-exact matches", function () {
    expect($('#partial_match')).toBeVisible();
  });

  it("hides non-matching filterables", function () {
    expect($('#another_filterable')).toBeHidden();
    expect($('#last_in_container_filterable')).toBeHidden();
  });

  it("doesn't hide divs that are not filterable", function () {
    expect($('#not_filterable')).toBeVisible();
  });

  it("shows everything if you clear the filter", function () {
    $("#filter_input").val('').keyup();
    expect($('#exact_match')).toBeVisible();
    expect($('#partial_match')).toBeVisible();
    expect($('#another_filterable')).toBeVisible();
  });

  it("doesnt hide a container if there are visible elements", function () {
    expect($('#visible_container')).toBeVisible();
  });

  it("hides a container if there are no filterable elements left", function () {
    expect($('#container_to_hide')).toBeHidden();
  });

});
