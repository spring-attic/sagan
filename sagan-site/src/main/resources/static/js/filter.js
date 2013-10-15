$.fn.contentFilter = function () {
  this.keyup(function () {
    var rex = new RegExp($(this).val(), 'i');
    $('[data-filterable]').each(function () {
      var filterable = $(this);
      if (rex.test(filterable.data('filterable'))) {
        filterable.show();
      } else {
        filterable.hide();
      }
    });

    $('[data-filterable-container]').each(function () {
      var container = $(this);
      container.show();
      if (container.find('[data-filterable]:visible').length == 0) {
        container.hide();
      }
    });
  });
};
