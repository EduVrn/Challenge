
var modal = document.getElementById('myCustomModal');
var modalBodyText = document.getElementById('modalBodyText');
var modalHeaderText = document.getElementById('modalHeaderText');
var modalHeader = document.getElementsByClassName('custom-modal-header')[0];
var modalContent = document.getElementsByClassName('custom-modal-content')[0];
var span = document.getElementsByClassName("custom-close")[0];


function showModal(text, header, color) {
    modalBodyText.innerHTML = text;
    modalHeaderText.innerHTML = header;
    if (color === "red") {
        modalHeader.style.backgroundColor = "#fd5e53";
        modalContent.style.borderColor = "#fd5e53";
    } else {
        if (color === "green") {
            modalHeader.style.backgroundColor = "#5cb85c";
            modalContent.style.borderColor = "#5cb85c";
        } else {
            modalHeader.style.backgroundColor = "#4863A0";
            modalContent.style.borderColor = "#4863A0";
        }
    }
    modal.style.display = "block";
    $("#myCustomContent").shakeit(4, 20, 1000);
}

span.onclick = function () {
    modal.style.display = "none";
};
//works only when somebody clicks upper
window.onclick = function (event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
};

//ShakeIt Plugin 
jQuery.fn.shakeit = function (intShakes, intDistance, intDuration) {
    this.each(function () {
        for (var x = 1; x <= intShakes; x++) {
            $(this).animate({right: (intDistance * -1)}, (((intDuration / intShakes) / 4)))
                    .animate({right: intDistance}, ((intDuration / intShakes) / 2))
                    .animate({right: 10}, (((intDuration / intShakes) / 4)));
        }
    });
    return this;
};


