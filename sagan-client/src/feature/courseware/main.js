var $ = require('jquery');
var sidebarSelector = 'aside div.related_courseware';

module.exports = function initCourseware() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        $.getJSON( "https://pivotallms.biglms.com/api/courses", function( data ) {
            if(data.length) {
                var items = [];
                items.push("<h3>Pivotal Academy</h3><ul>");
                $.each(data, function(idx, arr) {
                    arr.map(function(value){
                        items.push("<li><a href='"+value.url+"'>"+value.name+"</a></li>");
                    });
                });
                items.push("</ul>");
                $(sidebarSelector).append(items.join(""));
                $(sidebarSelector).parent().show();
            }
        });
    }

    function destroy() {}
};