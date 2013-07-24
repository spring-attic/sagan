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


    //OPENS DOCUMENTATION DROPDOWN
    $(".js-item--open-dropdown").click(function() {
      $(this).toggleClass("js-open");
    });






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

    $.fn.showPreferredLink = function() {
        this.find("li").hide();
        this.find("li." + detectOs() + detectArch()).show();
        return this;
    };

    $('.download-links').showPreferredLink();
});

var detectOs = function() {
    if (navigator.appVersion.indexOf("Win")!=-1) return "Windows";
    if (navigator.appVersion.indexOf("Mac")!=-1) return "Mac";
    if (navigator.appVersion.indexOf("Linux")!=-1) return "Linux";
    return "Unknown";
}

var detectArch = function() {
    if (navigator.platform.indexOf("Win64") !== -1) {
        return "64"
    }

    if (navigator.platform.indexOf("Linux x86_64") !== -1) {
        return "64";
    }

    if (/Mac OS X 10.[0-5]/.test(navigator.userAgent)) {
        return "32"
    }

    if (navigator.userAgent.indexOf("Mac OS X") !== -1) {
        return "64"
    }

    return "32";
}

