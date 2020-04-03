import 'bulma/css/bulma.css'
import '@fortawesome/fontawesome-free/css/all.css'
import $ from 'jquery';
import 'jquery-datetimepicker/jquery.datetimepicker';
import 'jquery-datetimepicker/jquery.datetimepicker.css';
import {DateTime} from 'luxon';
import '../css/admin.css'

$.datetimepicker.setLocale('en');

$.datetimepicker.setDateFormatter({
    parseDate: function (date, format) {
        var d = DateTime.fromFormat(date, format);
        return d.isValid ? d.toJSDate() : false;
    },
    formatDate: function (date, format) {
        return DateTime.fromJSDate(date).toFormat(format);
    },
});

if ($('#datetimepicker').length > 0) {
    $('#datetimepicker').datetimepicker({
        format: 'y-MM-dd HH:mm',
        formatDate: 'y-MM-dd',
        formatTime: 'HH:mm',
        closeOnDateSelect: true,
        scrollMonth: false,
        defaultTime: '12:00'
    });
}