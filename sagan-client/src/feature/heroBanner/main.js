//Homepage js

var $ = require('jquery');

//Homepage banner slides
function runSlideshow() {
  slideRunner = setTimeout(function () {
    $('.homepage--body .homepage--banner section.active').fadeOut(1000);
    $('.homepage--body .homepage--banner .dot.active').removeClass('active').next().addClass('active').siblings().removeClass('active');
    $('.homepage--body .homepage--banner section.active').next().addClass('active').fadeIn(900).siblings().removeClass('active');
  }, 4000);
}
runSlideshow();
var x = 0;
var slideLoop = setInterval(function () {
  runSlideshow();
 if (++x === 3) {
    window.clearInterval(slideLoop);
 }
}, 4000);

$('.homepage--body .homepage--banner .dot').click(function (){
    clearTimeout(slideRunner);
    window.clearInterval(slideLoop);
    $(this).addClass('active').siblings().removeClass('active');
    $('.homepage--body .homepage--banner').children().eq($(this).index()).each(function () {
      $('.homepage--body .homepage--banner section').hide();
      $(this).show();
    });
});

//Modern Java diagram fader
$(document).on('mousemove', function(e){
  if (e.pageX < 1020 && e.pageX > 120) {
    $('.homepage--body #fader-diagram #fader').css({left:  e.pageX});
    var fader = $("#fader-diagram #fader").css('left');
    $(".homepage--body #fader-diagram #fader-diagram-modern-java-color").css('width',fader);        
  }
});

//Spring boot video click to play
$('.homepage--body #video-with-overlay').click(function(){
  ytVideoLink = $(this).children('iframe').attr('src');
  $(this).children('iframe').attr('src', ytVideoLink + '?autoplay=1&amp;autohide=1&amp;showinfo=0&amp;controls=1');
  $(this).children('img').fadeOut();
});
