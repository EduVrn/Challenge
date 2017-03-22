var interactiveNotification;
var interactiveLike;
var interactiveComment;

var Interactive = {
    stompClient: null,
    username: null,
    mainObjectId: null,
    typeMainObject: null,
    connect: function () {
        var socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        this.mainObjectId = $("#mainObjectId").first().val();
        if ($(".ChallengeDefinitionType").length >= 1) {
            Interactive.typeMainObject = "ChallengeDefinitionType";
        } else if ($(".ChallengeInstanceType").length >= 1) {
            Interactive.typeMainObject = "ChallengeInstanceType";
        }

        if (jQuery.type(interactiveNotification) === "undefined") {
            interactiveNotification = false;
        } else if (jQuery.type(interactiveLike) === "undefined") {
            interactiveLike = false;
        } else if (jQuery.type(interactiveComment) === "undefined") {
            interactiveComment = false;
        }

        Interactive.stompClient.connect({'mainObjectId': this.mainObjectId,
            'interactiveNotification': interactiveNotification,
            'interactiveLike': interactiveLike,
            'interactiveComment': interactiveComment
        }, function (frame) {
            console.log('Connected: ' + frame);
            Interactive.username = frame.headers['user-name'];
            if (interactiveNotification === true) {
                console.log("subscribe to /user/exchange/notification");
                Interactive.stompClient.subscribe("/user/exchange/notification", function (resp) {
                    Interactive.notificationHandler(resp);
                });
            }
            if (interactiveComment === true) {
                console.log("subscribe to /user/exchange/comment");
                Interactive.stompClient.subscribe("/user/exchange/comment", function (resp) {
                    Interactive.commentHandler(resp);
                });
            }
            if (interactiveLike === true) {
                console.log("subscribe to /user/exchange/like");
                Interactive.stompClient.subscribe("/user/exchange/like", function (resp) {
                    Interactive.likeHandler(resp);
                });
            }
        }, function (error) {
            showModal(error, "Connection error", "red");
        });
    },
    notificationHandler: function (resp) {
        var obj = JSON.parse(resp.body);
        if (obj.status === 'SUCCESS') {
            console.log("get info: " + obj);
        }
        var template = $("#notification-candidate").clone();
        template.removeAttr("id");
        var storage = $('.mCSB_container');
        template.prependTo(storage);
        if (obj.typeNotification == "ChallengeInstance") {
            showModal(obj.body, $("#challenge-request").val(), "green");
        } else {
            showModal(obj.body + " " + $('#friend-notification-msg').val(), $("#friend-request").val(), "green");
        }
        this.changeContentNotification(template, obj);
        console.log("/user/exchange/notification");
        $('.badge-notify').each(function () {
            $(this).text(+$(this).text() + 1);
            $(this).removeClass("notify_hide");
        });
        setHeightOfNotificationWindow();
    },
    likeHandler: function (resp) {
        var obj = JSON.parse(resp.body);
        var up = $('.vote-value[id=' + obj.idOwner + '].vote-thumbs-up');
        var down = $('.vote-value[id=' + obj.idOwner + '].vote-thumbs-down');

        /* 2  - remove down, 1  - don't remove down
         -2 - remove up  , -1 - don't remove up*/

        switch (obj.changeVote) {
            case 2:
                down.text(+down.text() - 1);
                down.parent().find('.glyphicon-thumbs-down').removeClass('vote_hide');
                down.parent().find('.glyphicon-thumbs-down').addClass('vote_show');
            case 1:
                up.text(+up.text() + 1);
                up.parent().find('.glyphicon-thumbs-up').removeClass('vote_show');
                up.parent().find('.glyphicon-thumbs-up').addClass('vote_hide');
                break;
            case - 2:
                up.text(+up.text() - 1);
                up.parent().find('.glyphicon-thumbs-up').removeClass('vote_hide');
                up.parent().find('.glyphicon-thumbs-up').addClass('vote_show');
            case - 1:
                down.text(+down.text() + 1);
                down.parent().find('.glyphicon-thumbs-down').removeClass('vote_show');
                down.parent().find('.glyphicon-thumbs-down').addClass('vote_hide');
                break;
            default:
                break;
        }
    },
    commentHandler: function (resp) {
        var obj = JSON.parse(resp.body);
        if (obj.status === 'SUCCESS') {
            console.log("get info: " + obj);
            var storage = null;
            var template = null;
            if (obj.idParent == null) {
                template = $("#templateMainComment").clone();
                template.find("#templateComment").removeAttr("style");
                template.find("#templateComment").removeAttr("id");
                template.removeAttr("style");
                template.removeAttr("id");
                storage = $(".comments-list");
            } else {
                template = $("#templateComment").clone();
                template.removeAttr("style");
                template.removeAttr("id");
                template.addClass("subcommentList");
                template.addClass("withShift");
                storage = $(".comments-list").find("ul[id=" + obj.idParent + "]");
            }

            template.prependTo(storage);
            if ($('#current-user-id').val() != obj.userId) {
                showModal(obj.userName + ": \"" + obj.messageContent + "\"", $('#new-comment-header').val(), "blue");
            }
            this.changeContentComment(template, obj);
            var commentCounter = $("#commentCounter");
            commentCounter.text(+commentCounter.text() + 1);
        }
    },
    sendLike: function (obj) {
        var destination = "/app/interactive.like." + this.username;
        var idMessage = $(obj).parent().find("input[name='id']").val();
        var changeVote = -1;
        if ($(obj).hasClass('glyphicon-thumbs-up')) {
            changeVote = 1;
        }

        this.stompClient.send(destination, {},
                JSON.stringify({
                    'idOwner': idMessage,
                    'mainObjectId': this.mainObjectId,
                    'changeVote': changeVote,
                    'typeMain': this.typeMainObject}));
    },
    sendComment: function (obj) {
        var destination = "/app/interactive.comment." + this.username;
        var parent = $(obj).parent();
        var messageInp = parent.find("input[type=text]").first();
        if (messageInp.val().trim().length < 3 || messageInp.val().length > 250) {
            $(obj).parent().find(".commentError").show("slow");
        } else {
            $(obj).parent().find(".commentError").hide("slow");
            var idParent = null;
            if (parent.find("input[name=idMainObject]").first().size() !== 1) {
                idParent = parent.find("input[name=id]").first().val();
            }

            ObjSaved = JSON.stringify({
                'idParent': idParent,
                'mainObjectId': this.mainObjectId,
                'messageContent': messageInp.val(),
                'typeMain': this.typeMainObject});
            this.stompClient.send(destination, {},
                    ObjSaved);
            messageInp.val("");
            if (idParent != null) {
                $('.last-div#reply' + idParent).hide();
            }
        }
    },
    changeContentNotification: function (cont, obj) {
        $(cont).find('#message-content').text(obj.body);
        if (obj.description != null) {
            $(cont).find("#message-header").text(obj.description);
        }
        $(cont).find('#redirect-form input[name="id"]').val(obj.targetId);
        $(cont).find('#accept-form input[name="id"]').val(obj.requestId);
        $(cont).find('#decline-form input[name="id"]').val(obj.requestId);
        if (obj.typeNotification == "FriendRequest") {
            $(cont).find('#redirect-form').attr("action", "/profile");
            $(cont).find('#accept-form').attr("action", "/addFriend");
            $(cont).find('#decline-form').attr("action", "/removeRequest");
            $(cont).find("#message-part-2").text(" " + $('#friend-notification-msg').val());
        } else {
            $(cont).find('#message-part-1').text(obj.extraInfo + " " + $('#chal-notification-msg').val() + " ");
            $(cont).find('#accept-form').attr("action", "/accept");
            $(cont).find('#decline-form').attr("action", "/decline");
            $(cont).find('#redirect-form').attr("action", "/challenge/information");
        }

    },
    changeContentComment: function (cont, obj) {
        var mediaBody = $(cont).find("div[class='media-body']").first();
        var mediaLeft = $(cont).find("div[class='media-left']").first();
        $(mediaLeft).find('img')[0].src = obj.avatarImage;
        $(cont).find("ul").attr("id", obj.messageId);
        $(mediaBody).find("form[action='/profile']")
                .attr("id", "comment_form" + obj.messageId);
        $(mediaBody).find("form[action='/profile']")
                .find("input[name='id']")
                .attr("value", obj.userId);
        $(mediaBody).find("form[action='/profile']")
                .find("a").text(obj.userName);
        if (obj.toWhom != null) {
            $(mediaBody).find("form[action='/profile']")
                    .find("small").text('To ' + obj.toWhom).addClass("embedenceShowToWhom");
        }
        //onclick, username

        $(mediaBody).find("p").first().text(obj.messageContent)

        var lastDiv = $(cont).find("div[class='last-div']").first();
        $(lastDiv).attr("id", "reply" + obj.messageId);
        $(lastDiv).find("input[name='id']").attr("value", obj.messageId);
        $(lastDiv).find("form").submit(function (event) {
            Interactive.sendComment(event.target);
            return false;
        });
        $(mediaBody).find("small").find("a").click(function () {
            $(lastDiv).toggle();
        });
        var newreplyForm = $(lastDiv).find("form[action='/newreply']").first()

        //newreplyForm.find("input[name='_csrf']").val(username);
        newreplyForm.find("input[name='id']").first().val(obj.messageId);
        var voteFor = $(mediaBody).find("form[action='/comment/voteFor']");
        var voteAgainst = $(mediaBody).find("form[action='/comment/voteAgainst']");
        voteFor.find("input[name='id']").attr("value", obj.messageId);
        voteFor.find(".vote-value").attr("id", obj.messageId);
        voteFor.find(".send-vote").click(function (event) {
            Interactive.sendLike(event.target);
            return false;
        });
        if ($("#templateMainComment").find('div').find('.media-body').find('form').find('input').val() == obj.userId) {
            voteFor.find(".send-vote").removeClass('vote_show');
            voteFor.find(".send-vote").addClass('vote_hide');
            voteAgainst.find(".send-vote").removeClass('vote_show');
            voteAgainst.find(".send-vote").addClass('vote_hide');
        }
        voteAgainst.find("input[name='id']").attr("value", obj.messageId);
        voteAgainst.find(".vote-value").attr("id", obj.messageId);
        voteAgainst.find(".send-vote").click(function (event) {
            Interactive.sendLike(event.target);
            return false;
        });
    },
    disconnect: function () {
        if (Interactive.stompClient != null) {
            Interactive.stompClient.disconnect(function () {
            });
        }
        console.log("Disconnected");
    }
}