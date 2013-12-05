var $ = require('jquery');
var prettify = require('prettify');

module.exports = initPrettify;

function initPrettify () {

    $(prettify.prettyPrint);

    return {
        destroy: function () {}
    }
}
