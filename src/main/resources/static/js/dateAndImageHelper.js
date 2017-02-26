$(function () {
    $('#datetimepicker1').datetimepicker({format: 'DD/MM/YYYY HH:mm'})
            .data("DateTimePicker").setMinDate(new Date());
});

$("#upload-file-btn").click(function () {
    $("#input-file").trigger('click');
    $('#modal-challenge-images').modal('hide');
    $('#modal-user-images').modal('hide');
});

$(window).load(function () {
    $('.selectpicker').selectpicker();
});

$(document).on('click', 'img[name="image-link"]', function () {
    $('#new-image').attr('src', $(this).attr('src'));
    $('input[name="image"]').val($(this).prev().val());
    $('#modal-challenge-images').modal('hide');
    $('#modal-user-images').modal('hide');
    $('#image-error').remove();
});
