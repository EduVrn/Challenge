var labelChallengeMessage, placeholderChallengeMessage;

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
    $("#users-filter").on('input', function (event) {
        event.preventDefault();
        filterUsers();
    });
});

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

function filterUsers() {
    var search = {};
    search["filter"] = $("#users-filter").val();
    search["userId"] = $("#user-id").val();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "getUsers",
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
            displayUsers(data);
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
function displayUsers(data) {

    var $list = $("#list-users");
    $list.empty();

    var results = data["result"];

    $.each(results, function (key, value) {
        var $li = $('<li />', {
            "class": "list-group-item text-left"
        });
        var $image = $('<img />', {
            "class": "img-thumbnail",
            "style": "width: 80px; height: 80px; display: inline-block;",
            "src": value.image
        });
        var $formProfile = $('<form />', {
            "style": "display: inline-block;",
            "method": "GET",
            "action": "/profile"
        });
        var $hidden = $('<input />', {
            "type": "hidden",
            "name": "id",
            "value": key
        });
        var $a = $('<a />', {
            "class": "name",
            "onclick": "this.parentNode.parentNode.submit();"
        });
        $a.html(value.name);
        var $h3 = $('<h3 />', {
            "style": "margin-left: 5px;"
        });
        $h3.append($a);
        $formProfile.append($hidden);
        $formProfile.append($h3);

        var $rightDiv = $('<div />', {
            "class": "pull-right"
        });
        if (value.isFriend) {
            var $friendLabel = $('<h3 />');
            var $span = $('<span />', {
                "class": "label label-info",
                "text": $('#friend-label').val()
            });
            $friendLabel.append($span);
            $rightDiv.append($friendLabel);
        } else {
            var $formFriend = $('<form />', {
                "method": "GET",
                "action": "/addFriend"
            });
            var $friendId = $('<input />', {
                "type": "hidden",
                "value": key,
                "name": "id"
            });
            var $submit = $('<input />', {
                "type": "submit",
                "class": "btn btn-default",
                "value": $('#add-friend-text').val()
            });
            $formFriend.append($friendId);
            $formFriend.append($submit);
            $rightDiv.append($formFriend);
        }
        var $break = $('<div />', {
            "style": "width: 100%; margin: 20px;"
        });

        $li.append($image);
        $li.append($formProfile);
        $li.append($rightDiv);
        $li.append($break);
        $list.append($li);
    });
}

//display ajax's response on challenges and friends
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
            "text": value.name,
            "style": "margin-left: 5px"
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