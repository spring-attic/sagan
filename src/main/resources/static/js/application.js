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

    //CLOSES SEARCH DROPDOWN
    $(".js-search-input-close").click(function () {
      $(".js-search-dropdown").removeClass("js-show no-animation");
      $(".nav-search").removeClass("js-highlight");
    });
  }
  ;

  $.fn.showPreferredLink = function () {
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

  var initializeFacetSearchWidget = function () {
    var searchFacet = $(".search-facets");
    if (!searchFacet.length) {
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
        var facet = $(this).closest('.facet');
        var group = facet.parents(".facet").first();
        var checkBoxes = facet.find("input[type='checkbox']");
        var checkBox = checkBoxes.first();
        var uncheckedCheckBoxes = group.find(".sub-facet--list input[type='checkbox']:not(:checked)");


        if (checkBox.prop('checked') == false ){
          //IF IT IS CHECKED

          //UNCHECKES ITSELF
          $(this).prop('checked', false);

          function firstCheckbox(context) {
            context.siblings(".facet--wrapper").find("input[type='checkbox']").first().prop('checked', false);
          }
          // UNCHECKES HIGHEST PARENT 
          firstCheckbox($(this).parents(".sub-facet--list"));

          // UNCHECKS CLOSEST PARENT
          firstCheckbox($(this).closest(".sub-facet--list"));
          
          // UNCHECKS ALL CHILDRED
          $(this).parents(".facet--wrapper").siblings(".sub-facet--list, .facet-section--header").find("input[type='checkbox']").prop('checked', false);

        } else {
          //IF IT IS NOT CHECKED

          //CHECKS ALL CHILDRED
          checkBoxes.prop('checked',true);
        };
        if (uncheckedCheckBoxes.length == 0) {
          group.find("input[type='checkbox']").first().prop('checked', true);
        }
      });

      $('.sub-facet--list').each(function () {
        var checkedCheckBoxes = $("input[type='checkbox']:checked", this);
        var uncheckedBoxes = $("input[type='checkbox']:not(:checked)", this);
        if (checkedCheckBoxes.length !== 0 && uncheckedBoxes.length !== 0) {
          $(this).removeClass('js-close');
        }
      });


      
      if ($(".projects-facet input[type='checkbox']:checked").length) {
        $(".facet-section--header").removeClass("js-close");
        $(".projects-facet .sub-facet--list").first().removeClass("js-close");
      }
      
      $(".facets--clear-filters").click(function() {
        $(".facet--wrapper input[type='checkbox']:checked").prop('checked', false);
        $(".sub-facet--list, .facet-section--header").addClass("js-close");
      });
    }
  }
  initializeFacetSearchWidget();

  $(".js-team-map--wrapper").mouseenter(function() {
    $(".js-team-map--container").fadeOut("100");
    $(".js-team-map--wrapper").mouseleave(function() {
      $(".js-team-map--container").fadeIn("100");
    });
  });

  // $(".spring-logo--container .spring-logo").bind("contextmenu",function(e){
  //   var currentWindow = window.location.origin;
  //   window.location = currentWindow + '/logos'
  //   return false;  
  // });

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




