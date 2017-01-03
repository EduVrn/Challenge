//show notifications popup
var showing = false;
$('#notification').click(function () {
    showing = !showing;
    if (showing) {
        $('#chal-notification').show();
    } else {
        $('#chal-notification').hide();
    }
});

//show tabs' content
$('.nav-tabs a').click(function () {
    var active_tab_selector = $('.nav-tabs > li.active > a').attr('href');

    //find actived navigation and remove 'active' css
    var actived_nav = $('.nav-tabs > li.active');
    actived_nav.removeClass('active');

    //add 'active' css into clicked navigation
    $(this).parents('li').addClass('active');

    //hide displaying tab content
    $(active_tab_selector).removeClass('active');
    $(active_tab_selector).addClass('hide');

    //show target tab content
    var target_tab_selector = $(this).attr('href');
    $(target_tab_selector).removeClass('hide');
    $(target_tab_selector).addClass('active');
});

//send challengeId from challenge-form  to friends-form
$("input[name='throw']").click(function () {
    var chalId = $(this).prev().val();
    $('#friends-form').append($('<input />', {
        "type": "hidden",
        "value": chalId,
        "name": "chal-id"
    }));
});

//checkbox state changed: enable/disable button
$(document).on('change', ':checkbox', function () {
    var checkboxes = $('#friends-form :checkbox').toArray();

    $.each(checkboxes, function () {
        if (this.checked) {
            $('#throw-challenge-btn').removeAttr('disabled');
            return false;
        }
        $('#throw-challenge-btn').attr('disabled', 'disabled');
    });

    if (this.checked) {
        $(this).prev().attr('name', 'id-checked');
    } else {
        $(this).prev().attr('name', 'id');
    }
});

$(document).ready(function ($) {
    $("#filter").on('input', function (event) {
        event.preventDefault();
        searchViaAjax();
    });
});

function searchViaAjax() {
    var search = {};
    search["filter"] = $("#filter").val();
    search["userId"] = $("#user-id").val();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "ajax",
        async: true,
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("X-CSRF-TOKEN", $('#csrf-token').val());
        },
        data: JSON.stringify(search),
        dataType: 'json',
        timeout: 100000,
        success: function (data) {
            console.log("SUCCESS: ", data);
            display(data);
            return false;
        },
        error: function (e) {
            console.log("ERROR: ", e);
            display(e);
        },
        done: function (e) {
            console.log("DONE");
        }
    });
}

//display ajax's response
function display(data) {
    var $friendsList = $('#friends-list');
    $friendsList.empty();

    var results = data["result"];

    $.each(results, function (key, value) {
        var $li = $('<li />', {
            "class": "list-group-item"
        });
        var $hidden = $('<input />', {
            "type": "hidden",
            "value": key
        });
        var $checkbox = $('<input />', {
            "type": "checkbox",
            "name": "check"
        });
        var $label = $('<label />', {
            "text": value
        });
        $li.append($hidden);
        $li.append($checkbox);
        $li.append($label);
        $friendsList.append($li);
    });
}

//display reply form for a comment
var showingReplyForm = false;
$('.media-body small a').click(function () {
    var parent = $(this).parent().parent().parent();
    var children = parent.children();
    showingReplyForm = !showingReplyForm;
    if (showingReplyForm)
        children[children.length - 1].style.display = "block";
    else
        children[children.length - 1].style.display = "none";
});