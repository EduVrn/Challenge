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
        $('#step_div').css('display', 'block');
    else
        $('#step_div').css('display', 'none');
});

$(document).on('change', 'input[type="file"]', function () {
    var file = this.files[0];
    if (Math.round(file.size / 1024) >= 1300) {
        var $errorDiv = $('<div />', {
            "class": "alert alert-danger",
            "id": "image-error"
        });
        var $p = $('<p />');
        $p.html($('#image-size-error').val());
        $errorDiv.append($p);
        $('#image-name').after($errorDiv);
        $('#new-image-btn').after($errorDiv);
        return;
    }

    $('#image-error').remove();
    var $p = $('<p />');
    $p.html($('#input-file').val().split('\\').pop());
    $p.addClass('image-name');
    $p.css('display', 'inline-block');
    $('p.image-name').remove();
    $('#input-file').after($p);
    var $k = $('#image-name');
    $k.val($('#input-file').val().split('\\').pop());

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
