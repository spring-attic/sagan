import '../css/project.css'
import $ from 'jquery';


$(document).ready(function () {

    var tabs = $('#content-project .tab-pane');
    var lis = $('#nav-project li');

    $('#nav-project a').click(function (e) {
        e.preventDefault();
        tabs.removeClass('active');
        lis.removeClass('active');
        $(this).parent().addClass('active');
        $($(this).attr('href')).addClass('active');
    });

})