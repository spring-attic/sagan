/* Enhance bootstrap tabs so that it updates the location with
*   the correct fragment. Therefore navigation is enhanced,
*   for example when navigating to api docs, when the user clicks
*   the back button, they should return to the 'learn' tab.
*/
var $ = require('jquery');

// bootstrap-tab expects to find a jQuery object defined on the window
window.jQuery = $;
require('bootstrap/js/bootstrap-tab');


$(document).ready(function() {
    // show active tab on reload (or if the user navigates back)
    if (location.hash !== '') $('a[href="' + location.hash + '"]').tab('show');

    // remember the hash in the URL without jumping
    $('a[data-toggle="tab"]').on('shown', function(e) {
        var tabName = $(e.target).attr('href');
        history.pushState(null, null, tabName);
    });
});
