var $ = require('jquery');
var sidebarSelector = 'aside div.related_courseware';

module.exports = function initCourseware() {

    $(ready);

    return {
        destroy: destroy
    };

    function ready() {
        $.getJSON( "https://pivotallms.biglms.com/api/courses", function( data ) {
            if(data["eng1"] != null || data["edu1"] != null) {
                var items = [];
                if(data["edu1"] != null) {
                    items.push("<h3>Pivotal Academy</h3><ul>");
                    data["edu1"].map(function(value){
                        items.push("<li><a href='"+value.url+"'>"+value.name+"</a></li>");
                    });
                    items.push("</ul>");
                }
                if(data["eng1"] != null) {
                    items.push("<h3>Engineering Talks</h3><ul>");
                    data["eng1"].map(function(value){
                        items.push("<li><a href='"+value.url+"'>"+value.name+"</a></li>");
                    });
                    items.push("</ul>");
                }
                $(sidebarSelector).append(items.join(""));
                $(sidebarSelector).parent().show();
            }
        });
    }

    function destroy() {}
};