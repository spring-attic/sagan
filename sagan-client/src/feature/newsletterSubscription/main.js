var $ = require('jquery');
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
        // <iframe src="http://play.gopivotal.com/OSS_Website_Spring_SpringNewsletterSubscriptionEmailOnly.html" frameborder="0" scrolling="no" height="42px" width="324px" style="border:none" kwframeid="1"></iframe>
        $('.footer-newsletter--container')
            .append("<iframe src='/newsletter.html' frameborder='0' scrolling='no' height='42px' width='332px' style='border:none'/>");
        $('.footer-newsletter--container').find("iframe")
            .load(function() {
                // This should use an oocss state, like .loading instead
                // of bootstrap's .invisible, but going with .invisible until
                // there's time for CSS refactoring
                $('.footer-newsletter--container').removeClass('invisible');
            });
    });

    return {
        destroy: function() {}
    };
}


