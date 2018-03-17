/* Enhance bootstrap tabs so that it updates the location with
*   the correct fragment. Therefore navigation is enhanced,
*   for example when navigating to api docs, when the user clicks
*   the back button, they should return to the 'learn' tab.
*/
var $ = require('jquery');

// bootstrap-tab expects to find a jQuery object defined on the window
window.jQuery = $;
require('bootstrap/js/bootstrap-tab');


$(document).ready(function () {
    // show active tab on reload (or if the user navigates back)
    if (location.hash !== '') $('a[href="' + location.hash + '"]').tab('show');

    // remember the hash in the URL without jumping
    $('a[data-toggle="tab"]').on('shown', function (e) {
        var tabName = $(e.target).attr('href');
        history.pushState(null, null, tabName);
    });

    // Work around: img SVG to SVG
    // https://stackoverflow.com/questions/24933430/img-src-svg-changing-the-fill-color
    $('img.svg').each(function () {
        var $img = jQuery(this);
        var imgID = $img.attr('id');
        var imgClass = $img.attr('class');
        var imgURL = $img.attr('src');
        $.get(imgURL, function (data) {
            // Get the SVG tag, ignore the rest
            var $svg = jQuery(data).find('svg');
            // Add replaced image's ID to the new SVG
            if (typeof imgID !== 'undefined') {
                $svg = $svg.attr('id', imgID);
            }
            // Add replaced image's classes to the new SVG
            if (typeof imgClass !== 'undefined') {
                $svg = $svg.attr('class', imgClass + ' replaced-svg');
            }
            // Remove any invalid XML tags as per http://validator.w3.org
            $svg = $svg.removeAttr('xmlns:a');
            // Check if the viewport is set, if the viewport is not set the SVG wont't scale.
            if (!$svg.attr('viewBox') && $svg.attr('height') && $svg.attr('width')) {
                $svg.attr('viewBox', '0 0 ' + $svg.attr('height') + ' ' + $svg.attr('width'))
            }
            $img.replaceWith($svg);
        }, 'xml');
    });


});
