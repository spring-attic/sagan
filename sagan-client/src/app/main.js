import $ from 'jquery';
import '@fancyapps/fancybox'
import '@fancyapps/fancybox/dist/jquery.fancybox.css'
import '@fortawesome/fontawesome-free/css/all.css'
import '../css/main.css'


$(document).ready(function() {

    // Open external links in new tab
    $("a[href^='http']").attr('target', '_blank');

    $(".lightbox, .topics-resources a[href*=youtu], #quote a[href*=youtu], .half a[href*=youtu]").click(function() {
        var href = $(this).attr('href');
        $(this).attr('href', href + '?autoplay=1&autohide=1&showinfo=0&controls=1');
        $.fancybox({
            'padding'   : 0,
            'href'      : this.href.replace(new RegExp("watch\\?v=", "i"), 'embed/'),
            'type'      : 'iframe',
            'width'     : 1000,
            'autoPlay'  : true,
            'height'    : 560,
            'autoSize'  : false,
            fitToView   : true
        });

        return false;
    });

    $('li#search').click(function(){
        $('#search-nav').slideToggle();
        $('li#search').toggleClass('close');
        $('#searchheaderform input').focus();
    });

    //Guide
    $('body.guide main h2[id]').click(function(){
        $(this).parent().next().toggle();
        $(this).toggleClass('open');
    });

});

// Terminal animation compiled with Babel
"use strict";

window.onload = function () {
    if (document.querySelector("body").id == 'index') {
        var type = function type() {
            if (i < txt.length) {
                document.querySelector(".terminal .typed").innerHTML += txt.charAt(i);
                i++;
                setTimeout(type, speed);

                if (i == 22) {
                    document.querySelector(".terminal .typed").innerHTML = document.querySelector(".terminal .typed").innerHTML.replace("\"Hello World!\"", "<span class='terminal-lime'>\"Hello World!\"</span>");
                }
            }
        };

        var handler = function handler(entries, observer) {
            var _iteratorNormalCompletion = true;
            var _didIteratorError = false;
            var _iteratorError = undefined;

            try {
                for (var _iterator = entries[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
                    var entry = _step.value;

                    if (entry.isIntersecting) {
                        document.querySelector(".terminal .typed-placeholder").style.display = 'none';
                        type();
                    }
                }
            } catch (err) {
                _didIteratorError = true;
                _iteratorError = err;
            } finally {
                try {
                    if (!_iteratorNormalCompletion && _iterator.return != null) {
                        _iterator.return();
                    }
                } finally {
                    if (_didIteratorError) {
                        throw _iteratorError;
                    }
                }
            }
        };

        var i = 0;
        var txt = 'return "Hello World!";';
        var speed = 100;
        var observer = new IntersectionObserver(handler);
        observer.observe(document.querySelector(".terminal .terminal-blue"));
    }
};

// Filter toggle
$('#projects #proj-filters li').click(function(){

    if($(this).hasClass('active')) {
        $(this).toggleClass('active');
    }
    else {
        $('#projects #proj-filters li').removeClass('active');
        $(this).toggleClass('active');
    }
});

//Nav animation
function removeNavClasses () {
    $('#scope').removeClass('why-scope learn-scope projects-scope community-scope');
    $('.drop-menu').removeClass('active');
    $('.has-menu').removeClass('active');
}
$('#why-target').mouseenter(function(){
    removeNavClasses();
    $('#scope').addClass('why-scope');
    $('#why-items').addClass('active');
});
$('#learn-target').mouseenter(function(){
    removeNavClasses();
    $('#scope').addClass('learn-scope');
    $('#learn-items').addClass('active');
});
$('#project-target').mouseenter(function(){
    removeNavClasses();
    $('#scope').addClass('projects-scope');
    $('#project-items').addClass('active');
});
$('#community-target').mouseenter(function(){
    removeNavClasses();
    $('#scope').addClass('community-scope');
    $('#community-items').addClass('active');
});
$('.drop-menu').mouseenter(function () {
    $('this').addClass('active');
});
$('.drop-menu ul').mouseleave(function () {
    removeNavClasses();
});
$('.drop-menu').mouseleave(function () {
    removeNavClasses();
});

// Mobile nav
document.querySelector('#hamburger').onclick = function(){
    $('.mobile-close').fadeIn();
    $('#mobile-nav').slideDown();
    $('#mobile-nav-open #hamburger').removeClass('grow').addClass('shrink');
    $('#mobile-nav-open .mobile-class').addClass('grow');
    $('body').addClass('noscroll');
    $('.mobile-nav-arrow').delay(250).fadeIn();
}
document.querySelector('.mobile-close').onclick = function(){
    $('.mobile-close').fadeOut();
    $('#mobile-nav').slideUp();
    $('#mobile-nav-open .mobile-class').removeClass('grow').addClass('shrink');
    $('#mobile-nav-open #hamburger').addClass('grow');
    $('body').removeClass('noscroll');
    $('.mobile-nav-arrow').fadeOut();
}