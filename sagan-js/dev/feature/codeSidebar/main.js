var $ = require('jquery');

module.exports = function () {

    var sidebar = {

        init: function () {
            $(function () { this.ready(); }.bind(this));
            return this;
        },

        ready: function () {
            var switchProtocol, $repoSwitchButtons, $actionsSection;

            $actionsSection = $('.github-actions');
            $repoSwitchButtons = $actionsSection.find('[data-protocol]');

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

        switchProtocol: function ($root, prevProtocol, protocol) {
            $root.removeClass(prevProtocol);
            $root.addClass(protocol);
            return protocol;
        },

        destroy: function () {
            return this._destroy();
        },

        _destroy: function () {}
    };

    sidebar.init();

    return sidebar;

};

