package challenge.webside.controllers.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.User;
import challenge.webside.interactive.InteractiveRepository;
import challenge.webside.interactive.model.InteractiveNotification;

@Component
public class InteractiveUtil {

    @Autowired
    private InteractiveRepository commonInteractiveHandler;

    @Autowired
    private SimpMessagingTemplate template;

    public void interactiveThrowChallenge(int idUser, ChallengeInstance chalIns) {
        String username = commonInteractiveHandler.getNotificationCon(idUser);
        InteractiveNotification notification = new InteractiveNotification(idUser);
        notification.setMainObjectId(chalIns.getId());
        notification.setDescription(chalIns.getMessage());
        notification.setBody(chalIns.getName());
        notification.setTypeNotification("ChallengeInstance");
        notification.setTargetId(chalIns.getId());
        notification.setExtraInfo(chalIns.getChallengeRoot().getCreator().getName());
        template.convertAndSend("/user/" + username + "/exchange/notification", notification);
    }
    
    public void interactiveFriendRequest(int idUser, User userWhichMakesRequest) {
        String username = commonInteractiveHandler.getNotificationCon(idUser);
        InteractiveNotification notification = new InteractiveNotification(idUser);
        notification.setMainObjectId(userWhichMakesRequest.getId());
        notification.setBody(userWhichMakesRequest.getName());
        notification.setTypeNotification("FriendRequest");
        notification.setTargetId(userWhichMakesRequest.getId());
        template.convertAndSend("/user/" + username + "/exchange/notification", notification);
    }
    
}
