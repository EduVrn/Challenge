
$(function () {
		
	//}
	
	
});


$( window ).unload(function() {
	Interactive.disconnect();

	return "Handler for .unload() called.";
});


$(window).load(function() {
	console.log("try connect ");
	Interactive.connect();
	
	if(interactiveComment) {
		$('.send-comment').submit(function(event) {
			Interactive.sendComment(event.target);
			return false;
		});
	}
	
	if(interactiveLike == true) {
		$('.send-vote').click(function(event) {
			Interactive.sendLike(event.target);
			return false;
		});
	}
	
});