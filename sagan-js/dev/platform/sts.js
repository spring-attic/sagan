/*global sts_import */
module.exports = sts;

var $ = require('jquery');

function sts (thing) {
    // `sts_import` is a global that the Spring Tool Suite inerts onto
    // the widow object when this page is run inside STS.
    if (typeof sts_import  === 'function') {
        $(thing).show().click(function (e) {
            e.preventDefault();
            sts_import('guide', e.target.href);
        });
    }
}
