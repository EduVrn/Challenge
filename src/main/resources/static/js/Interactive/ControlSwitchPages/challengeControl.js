
$( window ).unload(function() {
	disconnect();

	return "Handler for .unload() called.";
});


$(window).load(function() {

	console.log("load page success");
	$('.send-message[type="submit"]').parent().submit(function(event) {
		sendMessage(event.target);
		return false;
	});
	
	$('.send-vote[type="submit"]').parent().submit(function(event) {
		alert("he")
		sendMessage(event.target);
		return false;
	});
	
});