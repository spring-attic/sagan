ZeroClipboard.setDefaults( { moviePath: '/js/ZeroClipboard.swf' } );

$(document).ready(function() {
    if (ZeroClipboard.detectFlashSupport()) {
        createCodeCopyButtons();
        activateGithubCopyButton();
    }

    $('.github-actions button').click(function() {
        $('.github-actions button').removeClass('active');
        $(this).addClass('active');

        $('.clone-url').hide();
        $('.clone-url.' + $(this).data('protocol')).show();
    });

    if (typeof(sts_import) === 'function') {
        $(".gs-guide-import").click(function (e) {
            var linkElement = e.target;
            var url = linkElement.href;
            sts_import("guide", url);
            e.preventDefault();
        });
    } else {
        $(".gs-guide-import").hide();
    }

});

function createCodeCopyButtons() {
    $('article .highlight pre').each(function(index) {
            var codeBlockId = "code-block-"+ index;
            $(this).attr('id', codeBlockId);
            var button = $('<button class="copy-button snippet" id="copy-button-"' + index + ' data-clipboard-target="' + codeBlockId + '" title="Click to copy to clipboard.">Copy</button>');
            $(this).before(button);
            new ZeroClipboard(button);
        }
    );
}

function activateGithubCopyButton() {
    new ZeroClipboard($('button.copy-button.github'));
}