/**
 * Created by Dan on 04.05.2016.
 */
$(document).ready(function() {
    $('li.active').removeClass('active');
    $('a[href="' + location.pathname + '"]').closest('li').addClass('active');
});