var $ = require('jquery');

module.exports = initFormWidgets;

function initFormWidgets () {

    $(ready);

    return {
        destroy: destroy
    };

    function ready () {
        $('form .date').datetimepicker({pickSeconds: false});
    }

    function destroy () {}
}
