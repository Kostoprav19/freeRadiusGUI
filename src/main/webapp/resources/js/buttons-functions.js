/**
 * Created by Dan on 14.11.2015.
 */

contextPath = '/freeRadiusGui'

function goToUrl(url) {
    location.href = url;
}

function goToUrlIfSure(url, text) {
    var r = confirm(text);
    if (r == true) {
        location.href = url;
    }
}

function goToUrlWithId(url, id){
  //  alert(url + id);
    location.href=window.location.pathname + url + "/" + id;
  //  location.href=contextPath + url + id;

}

function openModalWindow(str) {
    document.getElementById(str).showModal();
    $('<div id="__msg_overlay">').css({
        "width" : "100%"
        , "height" : "100%"
        , "background" : "#000"
        , "position" : "fixed"
        , "top" : "0"
        , "left" : "0"
        , "zIndex" : "50"
        , "MsFilter" : "progid:DXImageTransform.Microsoft.Alpha(Opacity=60)"
        , "filter" : "alpha(opacity=60)"
        , "MozOpacity" : 0.6
        , "KhtmlOpacity" : 0.6
        , "opacity" : 0.6

    }).appendTo(document.body);
}
