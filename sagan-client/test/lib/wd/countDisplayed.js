var when = require('when');

module.exports = function countVisible(elements) {
    return when.reduce(elements, function(count, element) {
        return element.isDisplayed().then(function(displayed) {
            return displayed ? count+1 : count;
        });
    }, 0);
};