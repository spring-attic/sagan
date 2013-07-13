$(function(){

    $.fn.springPopover = function(){
      this.each(function(i,e){
          var $e = $(e);
          var contents = $e.html();

          $e.html("<span class='btn'>"+$e.data('title')+"</span>").
             popover({content: contents, trigger: 'click', html: true});
      });

      return this;
    };

    $('.js-spring-popover').springPopover();

    //OPENS SEARCH INPUT
    $(".js-search-input-open").click(function () {
      var input = $(".js-search-input");
      var container = $(".js-search-input--container");
      container.addClass("js-open");
      setTimeout(function() {
        input.focus();
      }, 100);
      input.blur(function () {
        container.removeClass('js-open');
      });
    });
});

var detectOs = function() {
    if (navigator.appVersion.indexOf("Win")!=-1) return "Windows";
    if (navigator.appVersion.indexOf("Mac")!=-1) return "Mac";
    if (navigator.appVersion.indexOf("Linux")!=-1) return "Linux";
    return "Unknown";
}
