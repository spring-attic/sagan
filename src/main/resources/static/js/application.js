$(function () {

  $("#doc_filter").contentFilter();

  $.fn.springPopover = function () {
    this.each(function (i, e) {
      var $e = $(e);
      var contents = $e.html();

      $e.html("<span class='btn'>" + $e.data('title') + "</span>").
        popover({content: contents, trigger: 'click', html: true});
    });

    return this;
  };


  //OPENS ITEM DROPDOWN WIDGET
  $(".js-item--open-dropdown").click(function () {
    var dropdownItem = $(this).parents(".js-item-dropdown--wrapper");
    var documentHeight = $(document).height();
    var headerHeight = $("header").outerHeight();
    var footerHeight = $("footer").outerHeight();
    var scrimHeight = documentHeight - headerHeight - footerHeight;

    dropdownItem.toggleClass("js-open");
    dropdownItem.siblings().removeClass("js-open");
    $(this).parents(".js-item-dropdown-widget--wrapper").siblings().find(".js-item-dropdown--wrapper").removeClass("js-open");

    $("#scrim").addClass("js-show").css("height", scrimHeight).css("top", headerHeight);
    $("#scrim").click(function () {
      $(".js-item-dropdown--wrapper").removeClass("js-open");
      $(this).removeClass("js-show");
    });
  });

  //OPENS SEARCH DROPDOWN
  $(".js-search-input-open").click(function () {
    $(".nav-search").addClass("js-highlight");
    var inputContainer = $(".js-search-dropdown");
    var input = $(".js-search-input");
    inputContainer.addClass("js-show");

    //FOCUSES SEARCH INPUT ON OPEN
    setTimeout(function () {
      input.focus();
    }, 100);

    //CLOSES SEARCH DROPDOWN
    $(".body--container, .js-search-input-close").click(function () {
      inputContainer.removeClass("js-show");
      $(".nav-search").removeClass("js-highlight");
      $("#scrim").removeClass("js-show");
    });
  });


  //AUTO OPENS SEARCH DROPDOWN ON SEARCH VIEW AND 
  if (window.location.pathname == "/search") {
    $(".nav-search").addClass("js-highlight");
    $(".js-search-dropdown").addClass("js-show no-animation");

    //PREPOPULATES INPUT WITH SEARCH QUERY AND
    var searchQuery = decodeURIComponent(window.location.search.replace(/\+/g, " "));
    var seachStart = searchQuery.search("q=");
    var searchString = searchQuery.substr(seachStart + 2);

    $(".js-search-input").val(searchString);

    //PREPOPULATES TITLE WITH SEARCH QUERY
    $(".js-search-results--title").html(searchString);

    //CLOSES SEARCH DROPDOWN
    $(".js-search-input-close").click(function () {
      $(".js-search-dropdown").removeClass("js-show no-animation");
      $(".nav-search").removeClass("js-highlight");
    });
  }
  ;

  $.fn.showPreferredLink = function () {
    this.find("li").hide();
    this.find("li." + detectOs() + detectArch()).show();
    return this;
  };
  $('.download-links').showPreferredLink();


  var moveItemSlider = function () {
    var activeItem = $(".js-item-slider--wrapper .js-item.js-active");
    if (activeItem.length == 0 ) {
      return;
    } else {
      var activeItemPosition = activeItem.position();
      var activeItemOffset = activeItemPosition.left;
      var activeItemWidth = activeItem.outerWidth();

      var slider = $(".js-item--slider");
      var sliderPosition = slider.position();
      var sliderOffset = sliderPosition.left;
      var sliderTarget = activeItemOffset - sliderOffset;

      slider.width(activeItemWidth);
      slider.css("margin-left", sliderTarget);
      
    };
  }

  moveItemSlider();

  $(".js-item").click(function () {
    $(this).addClass("js-active");
    $(this).siblings().removeClass("js-active");
    moveItemSlider();
  });

  var initializeSearch = function () {
    var searchFacet = $(".search-facets");  
    if (searchFacet.length == 0) {
      return;
    } else {
      $(".sub-facet--list, .facet-section--header").addClass('js-close');

      $(".js-toggle-sub-facet-list").click(function() {
        $(this).closest(".facet").find(".sub-facet--list:first").toggleClass('js-close');
      });
      
      $(".projects-facet .js-toggle-sub-facet-list:first").click(function() {
        $(".facet-section--header").toggleClass('js-close');
      });


      $(".js-checkbox-pill").click(function() {
        var checkBoxes = $(this).closest(".facet").find("input[type='checkbox']");
        if (checkBoxes.prop('checked') == false ){
          $(this).prop('checked', false);
          $(this).parents(".facet").find("input[type='checkbox']:first").prop('checked',false);
          $(this).parents(".facet").find("input[type='checkbox']").prop('checked',false);
        } else {
          checkBoxes.prop('checked',true);
        };
      });
    }
  }
  initializeSearch();

  $(".js-team-map--wrapper").mouseenter(function() {
    $(".js-team-map--container").fadeOut("100");
    $(".js-team-map--wrapper").mouseleave(function() {
      $(".js-team-map--container").fadeIn("100");
    });
  });

  

});


var detectOs = function () {
  if (navigator.appVersion.indexOf("Win") != -1) return "Windows";
  if (navigator.appVersion.indexOf("Mac") != -1) return "Mac";
  if (navigator.appVersion.indexOf("Linux") != -1) return "Linux";
  return "Unknown";
}

var detectArch = function () {
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



/* Marketo Newsletter Subscription Form */

function fieldValidate(field) {
  return true;
}
var profiling = {
  isEnabled: false,
  numberOfProfilingFields: 3,
  alwaysShowFields: [ 'mktDummyEntry']
};
var mktFormLanguage = 'English'
function mktoGetForm() {return document.getElementById('mktForm_1035'); }



/* Subscribed cookie */

function getCookie(c_name)
{
var c_value = document.cookie;
var c_start = c_value.indexOf(" " + c_name + "=");
if (c_start == -1)
  {
  c_start = c_value.indexOf(c_name + "=");
  }
if (c_start == -1)
  {
  c_value = null;
  }
else
  {
  c_start = c_value.indexOf("=", c_start) + 1;
  var c_end = c_value.indexOf(";", c_start);
  if (c_end == -1)
    {
    c_end = c_value.length;
    }
  c_value = unescape(c_value.substring(c_start,c_end));
  }
return c_value;
}

function setCookie(c_name,value,exdays)
{
  var exdate=new Date();
  exdate.setDate(exdate.getDate() + exdays);
  var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
  document.cookie=c_name + "=" + c_value;
}

var subscribed=getCookie("subscribed");

function setSubscribeCookie() 
{
  setCookie("subscribed",subscribed,365);
}

function checkCookie()
{
if (subscribed)
  {
  document.getElementsByName('Email')[0].placeholder='Subscribed';
  }
}





