var stompClient = null;
var username;


function connect() {
	var socket = new SockJS('/ws');
	stompClient = Stomp.over(socket);

	stompClient.connect({}, function (frame) {
		console.log('Connected: ' + frame);
		isConnect = true;
		username = frame.headers['user-name'];
		sendConnectMessage();
		
		stompClient.subscribe('/topic/greetings', function (greeting) {
			console.log("/topic/greetings");
			
		});
		stompClient.subscribe("/user/exchange/like", function(resp){
			var obj = JSON.parse(resp.body);
			
			
		});
		
		
		stompClient.subscribe("/user/exchange/comment", function(resp){
			var obj = JSON.parse(resp.body);
			var template = $("#templateComment").clone();
			template.removeAttr("style");
			template.removeAttr("id");
			var storage = null;
			
			if(obj.idParent == null) {
				storage = $(".comments-list");
			}
			else {
				var storageCandidate = $(".comments-list").find("input[value=" + obj.idParent + "]").parent().parent();
				if(storageCandidate.find("ul").first().size() !== 1) {
					storageCandidate = storageCandidate.parent();
				}
				storage = storageCandidate.find("ul").first();
			}
			template.prependTo(storage);
			changeContent(template, obj);
		});

	});
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	isConnect = false;
	sendDisconnectMessage();
	console.log("Disconnected");
}

function sendLike(obj) {
	var destination = "/app/interactive.like." + username;
	var idMessage = $(obj.target).parent().find("input[name='id']").val();
	var mainObjectId = $("#mainObjectId").first().val();
	var typeMainObject = "ChallengeDefinition";
	var isDown = true;
	if($(obj.target).parent().find("span[class='glyphicon-thumbs-up']")) {
		isDown = false;
	}
	
	stompClient.send(destination, {}, JSON.stringify({
		'idMessage': idMessage,
		'mainObjectId': mainObjectId,
		'type': messageInp.val(),
		'isDown': isDown}));
}

function sendMessage(obj) {
	var destination = "/app/interactive.comment." + username;
	var parent = $(obj).parent();
	var messageInp = parent.find("input[type=text]").first();
	
	var idParent = null;
	
	if(parent.find("input[name=idMainObject]").first().size() !== 1) {
		idParent = parent.find("input[name=id]").first().val(); 
	}
	
	var mainObjectId = $("#mainObjectId").first().val();
	var typeMainObject = "ChallengeDefinition";

	stompClient.send(destination, {}, JSON.stringify({
		'idParent': idParent, 
		'mainObjectId': mainObjectId,
		'messageContent': messageInp.val(),
		'type': typeMainObject}));
	
	messageInp.val("");
	if(idParent != null) {
		//toggle input 
		messageInp.parent().css('display', 'none');		
	}
}

function sendConnectMessage() {
	var destination = "/app/interactive.open." + username;
	
	var idParent = null;
	var mainObjectId = $("#mainObjectId").first().val();
	
	
	stompClient.send(destination, {}, JSON.stringify({
		'idParent': idParent, 
		'mainObjectId': mainObjectId,
		'messageContent': null,
		'type': null}));	
}

function sendDisconnectMessage() {
	var destination = "/app/interactive.close." + username;
	
	var idParent = null;
	var mainObjectId = $("#mainObjectId").first().val();
	
	stompClient.send(destination, {}, JSON.stringify({
		'idParent': idParent, 
		'mainObjectId': mainObjectId,
		'messageContent': null,
		'type': null}));	
}


function changeContent(cont, obj) {	
	var mediaBody = $(cont).find("div[class='media-body']").first();
	
	$(mediaBody).find("form[action='/profile']")
		.attr("id", "comment_form" + obj.messageId);
	$(mediaBody).find("form[action='/profile']")
		.find("input[name='id']")
		.attr("value", obj.userId);	
	$(mediaBody).find("form[action='/profile']")
		.find("a").text(obj.userName);
	//onclick, username

	
	
	
	$(mediaBody).find("p").first().text(obj.messageContent)
	
	var lastDiv = $(cont).find("div[class='last-div']").first();
	$(lastDiv).attr("id", "reply" + obj.messageId);
	$(lastDiv).find("input[name='id']").attr("value", obj.messageId);
	
	
	
	$(lastDiv).find("form").submit(function(event) {
		sendMessage(event.target);
		return false;
	});
	
	$(mediaBody).find("small").find("a").click(function(){
		$(lastDiv).toggle();
	});
	
	var newreplyForm = $(lastDiv).find("form[action='/newreply']").first()

	//newreplyForm.find("input[name='_csrf']").val(username);
	newreplyForm.find("input[name='id']").first().val(obj.messageId);	
} 


