var $ = require('jquery');
var subscriptionTemplate = require('text!./subscription.html');

/**
 * Composition plan for the data-newsletter-subscription feature.
 * @module
 */
module.exports = initNewsletterSubscription;

/**
 * Injects the newsletter subscriptioniframe and makes it visible only
 * after it has fully loaded.
 * @returns {{destroy: Function}}
 */
function initNewsletterSubscription() {
    // The iframe was previously inline in layout.html, and thus loaded
    // its content synchronously, delaying DOMReady.  Since most of the site's
    // JavaScript-driven features can't fully initialize until DOMReady, this was
    // delaying lots of feature initialization that could have been proceeding
    // in parallel.  By injecting it dynamically, it doesn't block DOMReady.

    $(function() {
        var subscription = $(subscriptionTemplate);

        subscription
            .appendTo($('.footer-newsletter--wrapper'))
            .find('iframe')
            .load(function() {
                // This should use an oocss state, like .loading instead
                // of bootstrap's .invisible, but going with .invisible until
                // there's time for CSS refactoring
                subscription.removeClass('invisible');
            });
    });

    return {
        destroy: function() {}
    };
}


