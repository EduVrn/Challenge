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
    var $parentForm = $(this).closest('form');
    var checkboxes = $parentForm.find(':checkbox').toArray();
    $.each(checkboxes, function () {
        if (this.checked) {
            $parentForm.find('input[type="submit"]').removeAttr('disabled');
            return false;
        }
        $parentForm.find('input[type="submit"]').attr('disabled', 'disabled');
    });

    if (this.checked) {
        $(this).prev().attr('name', 'id-checked');
        $(this).next().next().show();
        $(this).next().next().children('input').attr('name', 'challenge-info');
    } else {
        $(this).prev().attr('name', 'id');
        $(this).next().next().hide();
        $(this).next().next().children('input').attr('name', 'challenge');
    }
});

$(document).ready(function ($) {
    $('#modal-challenges').on('shown.bs.modal', function () {
        labelChallengeMessage = $('#challenge-label').text();
        placeholderChallengeMessage = $('#challenge-input').attr('placeholder');
    });
    $("#filter-friends").on('input', function (event) {
        event.preventDefault();
        searchViaAjax(true);
    });
    $("#filter-challenges").on('input', function (event) {
        event.preventDefault();
        searchViaAjax(false);
    });
});

var labelChallengeMessage, placeholderChallengeMessage;

function searchViaAjax(friends) {
    var search = {};
    var action;
    if (friends) {
        search["filter"] = $("#filter-friends").val();
        search["userId"] = $("#user-id").val();
        action = "getFriends";
    } else {
        search["filter"] = $("#filter-challenges").val();
        search["userId"] = $("#current-user-id").val();
        action = "getChallenges";
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: action,
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
            display(data, friends);
            return false;
        },
        error: function (e) {
            console.log("ERROR: ", e);
        },
        done: function (e) {
            console.log("DONE");
        }
    });
}

//display ajax's response
function display(data, friends) {

    var $list;
    if (friends) {
        $list = $('#friends-list');
    } else {
        $list = $('#challenges-list');
    }
    $list.empty();

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
        if (!friends) {
            var $div = $('<div />', {
                "style": "display: none;"
            });
            var $inputMessage = $('<input />', {
                "type": "text",
                "name": "challenge-info",
                "placeholder": placeholderChallengeMessage,
                "style": "width: 100%; box-sizing: border-box;"
            });
            $li.append($('<input />', {
                "type": "hidden",
                "name": "user-id",
                "value": $("#friend-id").val()
            }));
            $div.append('<label>' + labelChallengeMessage + '</label><br />');
            $div.append($inputMessage);
        }
        $li.append($hidden);
        $li.append($checkbox);
        $li.append($label);
        if (!friends) {
            $li.append($div);
        }
        $list.append($li);
    });
}

//display reply form for a comment
var showingReplyForm = false;
$('.media-body small a').on('click', function () {
    var parent = $(this).parent().parent().parent();
    var children = parent.children();
    showingReplyForm = !showingReplyForm;
    if (showingReplyForm)
        children[children.length - 2].style.display = "block";
    else
        children[children.length - 2].style.display = "none";
});

var showingStepForm = false;
$('.new_step small a').on('click', function () {
    showingStepForm = !showingStepForm;
    if (showingStepForm)
        $('#step_div').css('display' , 'block');
    else
        $('#step_div').css('display' , 'none');
});

$(document).on('change', 'input[type="file"]', function () {
    var $p = $('<p />');
    $p.html($('#input-file').val().split('\\').pop());
    $p.addClass('image-name');
    $p.css('display', 'inline-block');
    $('p.image-name').remove();
    $('#input-file').after($p);
    var $k = $('#image-name');
    $k.val($('#input-file').val().split('\\').pop());

    var file = document.querySelector('input[type="file"]').files[0];
    var reader = new FileReader();

    reader.onloadend = function () {
        var result = reader.result;
        $('input[name="image"]').val(result);
        $('#new-image').attr('src', result);
    };

    if (file) {
        reader.readAsDataURL(file);
    }
});