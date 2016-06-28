/**
 * Created by Dan on 28.06.2016.
 */
//Jump to anchor -100px with animation
$(document).ready(function() {
    $('a[href^="#"]').click(function() {
        var target = $(this.hash);
        if (target.length == 0) target = $('a[name="' + this.hash.substr(1) + '"]');
        if (target.length == 0) target = $('html');
        $('html, body').animate({ scrollTop: target.offset().top-100 }, 500);
        return false;
    });
});