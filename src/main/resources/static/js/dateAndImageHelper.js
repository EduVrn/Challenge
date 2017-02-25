$(function () {
    $('#datetimepicker1').datetimepicker({format: 'DD/MM/YYYY HH:mm'})
            .data("DateTimePicker").setMinDate(new Date());
});

$("#upload-file-btn").click(function () {
    $("#input-file").trigger('click');
    $('#modal-challenge-images').modal('hide');
});
$('img[name="image-link"]').on('click', function () {
    $('#new-image').attr('src', $(this).attr('src'));
    $('input[name="image"]').val($(this).prev().val());
    $('#modal-challenge-images').modal('hide');
});
window.onload = function () {
    $('.selectpicker').selectpicker();
};

