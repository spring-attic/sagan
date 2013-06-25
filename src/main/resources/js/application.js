$(function(){

    $.fn.springPopover = function(){
      this.each(function(i,e){
          var $e = $(e);
          var contents = $e.html();

          $e.html("<span class='btn'>"+$e.data('title')+"</span>").
             popover({content: contents, trigger: 'click', html: true});
      });

      return this;
    };

    $('.js-spring-popover').springPopover();
});

ZeroClipboard.setDefaults( { moviePath: 'http://sagan.cfapps.io/js/ZeroClipboard.swf' } );

$(document).ready(function() {
    $('article .highlight pre').each(function(index) {
            var codeBlockId = "code-block-"+ index;
            $(this).attr('id', codeBlockId);
            var button = $('<button class="copy-button" id="copy-button-"' + index + ' data-clipboard-target="' + codeBlockId + '" title="Click to copy to clipboard.">Copy</button>');
            $(this).before(button);
            new ZeroClipboard(button);
        }
    );
});
