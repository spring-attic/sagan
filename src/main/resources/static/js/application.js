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
      var documentationItem = $(this).parents(".js-documentation--item");
      var documentHeight = $(document).height();
      var headerHeight = $("header").outerHeight()
      var footerHeight = $("footer").outerHeight();
      var scrimHeight = documentHeight - headerHeight - footerHeight;

      documentationItem.toggleClass("js-open");
      documentationItem.siblings().removeClass("js-open");
      $(this).parents(".documentation--body").siblings().find(".js-documentation--item").removeClass("js-open");
      
      $("#scrim").addClass("js-show").css("height", scrimHeight).css("top", headerHeight);
      $("#scrim").click(function() {
        $(".js-documentation--item").removeClass("js-open");
        $(this).removeClass("js-show");
      });
    });

    //OPENS SEARCH DROPDOWN
    $(".js-search-input-open").click(function () {
      $(".nav-search").addClass("js-highlight");
      var inputContainer = $(".js-search-dropdown");
      var input = $(".js-search-input");
      inputContainer.addClass("js-show");
      setTimeout(function() {
        input.focus();
      }, 100);
      $(".body--container, .js-search-input-close").click(function() {
        inputContainer.removeClass("js-show");
        $(".nav-search").removeClass("js-highlight");
      });
    })

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

