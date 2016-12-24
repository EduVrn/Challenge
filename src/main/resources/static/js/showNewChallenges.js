var showing = false;

$('#notification').click(function() {
    showing = !showing;
    if (showing) {
        $('#chal-notification').show();
    } else {
        $('#chal-notification').hide();
    }
});

