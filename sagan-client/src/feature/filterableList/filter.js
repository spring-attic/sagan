module.exports = {
    create: createFilter,
    partitionMatches: partitionMatches
};

/**
 * Creates a function that filters the provided containers and their children
 * @param {function(boolean, HTMLElement)} handleMatch matching function will be called for
 *  every element, with a boolean indicating if it matches or not.
 * @param {[{ node: HTMLElement, children: [HTMLElement] }]} containers 2-level tree
 *  describing the containers and children to filter
 * @returns {function} function that accepts a matcher function
 */
function createFilter(handleMatch, containers) {
    return function(matcher) {
        containers.forEach(function(container) {
            var shown = 0;
            partitionMatches(
                matcher,
                function(matched, node) {
                    handleMatch(matched, node);
                    if(matched) {
                        shown += 1;
                    }
                },
                container.children
            );

            handleMatch(shown > 0, container.node);
        });
    };
}

/**
 * Executes the provided matcher predicate for each node, then passes both
 * the predicate result (true or false) and node to handleMatch
 * @param {function(HTMLElement):boolean} matcher predicate to evaluate for each node
 * @param {function(boolean, HTMLElement):void} handleMatch process a node and its predicate result
 * @param {[HTMLElement]} nodes to evaluate
 */
function partitionMatches(matcher, handleMatch, nodes) {
    nodes.forEach(function(node) {
        handleMatch(matcher(node), node);
    });
}
