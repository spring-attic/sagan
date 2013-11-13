/**
 * @module
 * @type {Guide}
 */
module.exports = function () {
    return new Guide();
};

var copyButtonProvider = require('./copyButtonProvider');
var buttonTemplate = require('text!./button.html');
var createClipboard = require('component/clipboard/main');
var $ = require('jquery');
var cssSwitch = require('./cssSwitch');

require('css!./states.css');

/**
 * Creates and initializes the plan for decorating guide pages.
 * @constructor
 */
function Guide () {

    var addButton, self;

    this.eventRemovers = [];

    addButton = copyButtonProvider($(buttonTemplate)[0]);

    this.clipboard = createClipboard({
        buttonProvider: addButton,
        'ZeroClipboard.swf': '/lib/zeroclipboard/ZeroClipboard.swf'
    });

    self = this;
    $(function () {
        // do not use self.bind here so that using code can advise ready()
        self.ready();
    });

}

Guide.prototype = {

    /**
     * Call this when the dom is ready.
     */
    ready: function () {
        var switchRepoAction, $repoSwitchButtons,
            $repoCopyButtons, $actionsSection, $codeSnippets;

        $codeSnippets = $('article .highlight pre');
        $actionsSection = $('.github-actions');
        $repoCopyButtons = $actionsSection.find('button.copy-button.github');
        $repoSwitchButtons = $actionsSection.find('[data-protocol]');

        switchRepoAction = cssSwitch($actionsSection[0]);

        $repoSwitchButtons.on('click', getProtocol);
        this.eventRemovers.push(function () {
            $repoSwitchButtons.off('click', getProtocol);
        });

        this.clipboard.ready();

        this.clipboard.connectToClipboard($repoCopyButtons);
        this.clipboard.attachClipboardElements($codeSnippets);

        function getProtocol (e) {
            return switchRepoAction($(e.target).data('protocol'));
        }

    },

    /**
     * Call this when this component is no longer needed.
     */
    destroy: function () {
        var removers, remover;

        this.clipboard.destroy();

        removers = this.eventRemovers;

        $(function () {
            while (remover = removers.pop()) {
                remover();
            }
        });
    }

};
