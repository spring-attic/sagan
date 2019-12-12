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
        $('li#search .fa-search').toggleClass('close');
        $('#searchheaderform input').focus();
    });

    //Guide
    $('body.guide main h2[id]').click(function(){
        $(this).parent().next().toggle();
        $(this).toggleClass('open');
    });

});

// Terminal animation
window.onload = function() {
    if (document.querySelector("body").id == 'index') {
        var i = 0;
        var txt = 'return String.format("Hello World!");';
        var speed = 100;

        function type() {
            if (i < txt.length) {
                document.querySelector(".terminal .typed").innerHTML += txt.charAt(i);
                i++;
                setTimeout(type, speed);
                if (i == 37) {
                    document.querySelector(".terminal .typed").innerHTML = document.querySelector(".terminal .typed").innerHTML.replace(`"Hello World!"`, `<span class='terminal-lime'>"Hello World!"</span>`);
                }
            }
        }

        function handler(entries, observer) {
            for (entry of entries) {
                if (entry.isIntersecting) {
                    document.querySelector(".terminal .typed-placeholder").style.display = 'none';
                    type();
                }
            }
        }

        let observer = new IntersectionObserver(handler);
        observer.observe(document.querySelector(".terminal .terminal-blue"));
    }
};

// Mobile nav
document.querySelector('#hamburger').onclick = function(){
    document.querySelector('#popup').classList.add('active');
};
document.querySelector('#exit').onclick = function(){
    document.querySelector('#popup').classList.remove('active');
};