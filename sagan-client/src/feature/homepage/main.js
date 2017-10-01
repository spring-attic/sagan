var $ = require('jquery');

var activeBannerSelector = ".homepage--body .homepage--banner section.active";
var dotSelector = ".homepage--body .homepage--banner .dot";
var activeDotSelector = dotSelector + ".active";
var bannerDelay = 4000;
var slideLoop, slideRunner;

module.exports = function initHomepage() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        runSlideshow();
        var x = 0;
        slideLoop = setInterval(function () {
            runSlideshow();
            if (++x === 3) {
                window.clearInterval(slideLoop);
            }
        }, bannerDelay);

        $(dotSelector).click(selectBanner);

        //Modern Java diagram fader
        $(document).on('mousemove', moveDiagram);
        //Spring boot video click to play
        $('.homepage--body #video-with-overlay').click(playBootVideo);

    }

    function destroy() {
        window.clearInterval(slideLoop);
        $(dotSelector).off('click', selectBanner);
        $(document).off('mousemove', moveDiagram);
        $('.homepage--body #video-with-overlay').off('click', playBootVideo);
    }
};

//Homepage banner slides
function runSlideshow() {
    slideRunner = setTimeout(function () {
        $(activeBannerSelector).fadeOut(1000);
        $(activeDotSelector).removeClass('active').next().addClass('active').siblings().removeClass('active');
        $(activeBannerSelector).next().addClass('active').fadeIn(900).siblings().removeClass('active');
    }, bannerDelay);
}

function selectBanner() {
    clearTimeout(slideRunner);
    window.clearInterval(slideLoop);
    $(this).addClass('active').siblings().removeClass('active');
    $('.homepage--body .homepage--banner').children().eq($(this).index()).each(function () {
        $('.homepage--body .homepage--banner section').hide();
        $(this).show();
    });
}

function moveDiagram(e) {
    var x = e.pageX - $('.homepage--body #fader-diagram').offset().left;
    if ((e.pageY < 960 && e.pageY > 630) && (x < 800 && x > 0)) {
        $('.homepage--body #fader-diagram #fader').css({left: x});
        var fader = $("#fader-diagram #fader").css('left');
        $(".homepage--body #fader-diagram #fader-diagram-modern-java-color").css('width', fader);
    }
}

function playBootVideo() {
    ytVideoLink = $(this).children('iframe').attr('src');
    $(this).children('iframe').attr('src', ytVideoLink + '?autoplay=1&amp;autohide=1&amp;showinfo=0&amp;controls=1');
    $(this).children('img').fadeOut();
}
