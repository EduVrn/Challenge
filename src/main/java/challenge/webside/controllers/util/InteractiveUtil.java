package challenge.webside.controllers.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Request;
import challenge.dbside.models.User;
import challenge.webside.interactive.InteractiveRepository;
import challenge.webside.interactive.model.InteractiveNotification;

@Component
public class InteractiveUtil {

    @Autowired
    private InteractiveRepository commonInteractiveHandler;

    @Autowired
    private SimpMessagingTemplate template;

    public void interactiveThrowChallenge(int idUser, Request request) {
        String username = commonInteractiveHandler.getNotificationCon(idUser);
        InteractiveNotification notification = new InteractiveNotification(idUser);
        notification.setMainObjectId(request.getSubject().getId());
        notification.setDescription(request.getMessage());
        notification.setBody(request.getSubject().getName());
        notification.setTypeNotification("ChallengeInstance");
        notification.setTargetId(request.getSubject().getId());
        notification.setExtraInfo(request.getSubject().getCreator().getName());
        notification.setRequestId(request.getId());
        template.convertAndSend("/user/" + username + "/exchange/notification", notification);
    }
    
    public void interactiveFriendRequest(int idUser, Request request) {
        String username = commonInteractiveHandler.getNotificationCon(idUser);
        InteractiveNotification notification = new InteractiveNotification(idUser);
        notification.setMainObjectId(request.getSender().getId());
        notification.setBody(request.getSender().getName());
        notification.setTypeNotification("FriendRequest");
        notification.setTargetId(request.getSender().getId());
        notification.setRequestId(request.getId());
        template.convertAndSend("/user/" + username + "/exchange/notification", notification);
    }
    
}
