/**
 * Created by Dan on 14.11.2015.
 */

rootPath = '/freeRadiusGui';

function goToUrl(url) {
    location.href = rootPath + url;
}

function goToUrlIfSure(url, text) {
    var r = confirm(text);
    if (r == true) {
        location.href = url;
    }
}

function goToUrlWithId(url, id){
    location.href=window.location.pathname + url + "?id=" + id;
}

function goToUrlWithIdAndAction(url, id, action){
    location.href=window.location.pathname + url + "?id=" + id + "&action=" + action;
}

function goToUrlWithAction(url, action){
    location.href=window.location.pathname + url + "?action=" + action;
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
