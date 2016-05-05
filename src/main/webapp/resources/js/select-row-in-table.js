function highlight(e) {
    if (typeof selected01 != 'undefined' && selected01[0]) {
        selected01[0].className = '';
    }

    if (typeof selected02 != 'undefined' && selected02[0]) {
        selected02[0].className = '';
    }

    if (typeof selected03 != 'undefined' && selected03[0]) {
        selected03[0].className = '';
    }

    if (typeof selected04 != 'undefined' && selected04[0]) {
        selected04[0].className = '';
    }

    e.target.parentNode.className = 'selected';


    var button = document.getElementById('add');
    if (button != null) {
        button.disabled = false;
    }

    if ($(this).closest('table').attr('id') == 't03') {
        var button = document.getElementById('add03');
        if (button != null) {
            button.disabled = false;
        }
    }

    if ($(this).closest('table').attr('id') == 't04') {
        var button = document.getElementById('add04');
        if (button != null) {
            button.disabled = false;
        }
    }

    if ($(this).closest('table').attr('id') == 't01') {
        var button = document.getElementById('delete01');
        if (button != null) {
            button.disabled = false;
        }
        var button = document.getElementById('delete02');
        if (button != null) {
            button.disabled = true;
        }
    }

    if ($(this).closest('table').attr('id') == 't02') {
        var button = document.getElementById('delete02');
        if (button != null) {
            button.disabled = false;
        }
        var button = document.getElementById('delete01');
        if (button != null) {
            button.disabled = true;
        }
    }


    var button = document.getElementById('edit');
    if (button != null) {
        button.disabled = false;
    }

}
var button = document.getElementById('add');
if (button != null) {
    button.disabled = true;
}

var button = document.getElementById('add02');
if (button != null) {
    button.disabled = true;
}

var button = document.getElementById('add03');
if (button != null) {
    button.disabled = true;
}

var button = document.getElementById('add04');
if (button != null) {
    button.disabled = true;
}

var button = document.getElementById('delete01');
if (button != null) {
    button.disabled = true;
}

var button = document.getElementById('delete02');
if (button != null) {
    button.disabled = true;
}

var button = document.getElementById('edit');
if (button != null) {
    button.disabled = true;
}

var table = document.getElementById('t01');
if (table != null) {
    selected01 = table.getElementsByClassName('selected');
    table.onclick = highlight;
}

var table = document.getElementById('t02');
if (table != null) {
    selected02 = table.getElementsByClassName('selected');
    table.onclick = highlight;
}

var table = document.getElementById('t03');
if (table != null) {
    selected03 = table.getElementsByClassName('selected');
    table.onclick = highlight;
}

var table = document.getElementById('t04');
if (table != null) {
    selected04 = table.getElementsByClassName('selected');
    table.onclick = highlight;
}

function gotoSelectedId(url){
    var id = $("tr.selected td:first").html();
    location.href=url + id;
}
function gotoSelectedIdIfSure(url, text) {
    var r = confirm(text);
    var id = $("tr.selected td:first").html();
    if (r == true) {
        location.href = url + id;
    }
}