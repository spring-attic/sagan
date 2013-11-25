module.exports = function createAttributeMatcher(attribute) {
    return function(wordsString) {
        if(typeof wordsString !== 'string') {
            wordsString = '';
        }

        var rxString, rx;

        // Construct a RegExp that looks lookahead (?=) to ensure that the
        // string to match contains all the words in wordsString.
        // Inspired by:
        // http://stackoverflow.com/questions/13911053/regular-expression-to-match-all-words-in-a-query-in-any-order
        rxString = wordsString.split(/\s+/).reduce(function(rxString, word) {
            return rxString + '(?=.*' + word + ')';
        }, '^') + '.+';

        rx = new RegExp(rxString, 'i');

        return function(node) {
            return rx.test(node.getAttribute(attribute));
        };
    };
};