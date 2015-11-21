var $ = require('jquery');
var timeAgoSelector = 'time.timeago';
require('jquery-timeago');

module.exports = function initTimeAgo() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        $(timeAgoSelector).timeago();
    }

    function destroy() {}
};