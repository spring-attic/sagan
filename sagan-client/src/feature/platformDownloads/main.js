var $ = require('jquery');
var os = require('../../platform/os');

module.exports = function initPlatformDownloads() {
    var collapsibleEvents = {
        shown: showCollapsible,
        hide: hideCollapsible
    };

    $(function() {
        $('.download-links li.' + os.type() + os.arch()).show();

        $('#platform--' + os.type())
            .addClass('in')
            .css('overflow', 'visible')
            .css('height', 'auto')
            .parent()
            .find('.platform-dropdown--icon')
            .removeClass('icon-chevron-down')
            .addClass('icon-chevron-up');

        $('.collapse')
            .removeClass('in')
            .on(collapsibleEvents);

    });

    return {
        destroy: function() {
            $('collapse').off(collapsibleEvents);
        }
    };
};

function showCollapsible() {
    $(this)
        .css({ overflow: 'visible', height: 'auto' })
        .parent().find('.platform-dropdown--icon')
        .removeClass('icon-chevron-down')
        .addClass('icon-chevron-up');
}

function hideCollapsible() {
    $(this)
        .css({ overflow: 'hidden', height: 0 })
        .parent().find('.platform-dropdown--icon')
        .removeClass('icon-chevron-up')
        .addClass('icon-chevron-down');
}