var $ = require('jquery');
var bannersSelector = ".homepage--body .hero--banner > section";
var previousSelector = ".homepage--body .hero--banner .blockarrows .previous";
var nextSelector = ".homepage--body .hero--banner .blockarrows .next";
var intervalDelay = 7000;
var intervalId;

module.exports = function initHeroBanner() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        $(previousSelector).on('click', previousBanner);
        $(nextSelector).on('click', nextBanner);
        intervalId = window.setInterval(nextBanner, intervalDelay);
    }

    function destroy() {
        $(previousSelector).off('click', previousBanner);
        $(nextSelector).off('click', nextBanner);
        window.clearInterval(intervalId);
    }
};

function nextBanner() {
  if($(bannersSelector + ":visible").next("section").length == 0) {
    $(bannersSelector + ":last").hide();
    $(bannersSelector + ":first").fadeIn(250);
  } else {
    $(bannersSelector + ":visible").hide().next("section").fadeIn(250);
  }
};

function previousBanner() {
  if($(bannersSelector + ":visible").prev("section").length == 0) {
    $(bannersSelector + ":first").hide();
    $(bannersSelector + ":last").fadeIn(250);
  } else {
    $(bannersSelector + ":visible").hide().prev("section").fadeIn(250);
  }
};