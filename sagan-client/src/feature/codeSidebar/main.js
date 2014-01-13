var $ = require('jquery');

/**
 * The composition plan for the sidebar on pages that use the
 * data-code-sidebar feature attribute.
 * @module
 */
module.exports = initSidebar;

/**
 * Creates and initializes the composition plan for the sidebar.
 * Calling this function will automatically prepare `ready` to be called
 * when the DOM is ready.
 * This plan improves on the previous code that used style manipulation.
 * Now it uses OOCSS state rules to hide/show the selected button and the
 * appropriate repo url box.
 * @returns {{ready: Function, switchProtocol: Function, destroy: Function}}
 */
function initSidebar () {

    var plan = {

        /**
         * Called when the dom is ready.  This function does the bulk of
         * the DOM work for this feature.
         */
        ready: function () {
            var switchProtocol, $repoSwitchButtons, $actionsSection;

            $actionsSection = $('.github-actions');
            $repoSwitchButtons = $actionsSection.find('[data-protocol]');

            // memoize the className switcher function and the previous
            // protocol className.
            switchProtocol = (function (switcher) {
                var prevProtocol = 'https';
                return function (e) {
                    var protocol = $(e.target).data('protocol');
                    switcher(prevProtocol, protocol);
                    prevProtocol = protocol;
                };
            }(this.switchProtocol.bind(this, $actionsSection)));

            $repoSwitchButtons.on('click', switchProtocol);
            this._destroy = function () {
                $repoSwitchButtons.off('click', switchProtocol);
            };
        },

        /**
         * Adds a className specified by `protocol` on $root and removes
         * a className specified by `prevProtocol`.
         * @param {jQuery} $root
         * @param {string} prevProtocol
         * @param {string} protocol
         * @returns {string} the protocol specified in the input.
         */
        switchProtocol: function ($root, prevProtocol, protocol) {
            $root.removeClass(prevProtocol);
            $root.addClass(protocol);
            return protocol;
        },

        /**
         * Cleans up any event handlers.
         */
        destroy: function () {
            return this._destroy();
        },

        /**
         * The actual implementation of the destroy function.  This noop
         * function is replaced at run-time if event handlers are added.
         * @private
         */
        _destroy: function () {}
    };

    $(function () { plan.ready(); }.bind(plan));

    return plan;

}
