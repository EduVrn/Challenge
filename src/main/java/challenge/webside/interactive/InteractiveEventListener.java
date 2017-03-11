package challenge.webside.interactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import challenge.webside.dao.UsersDao;

public class InteractiveEventListener {

    private static final Logger logger = LoggerFactory.getLogger(InteractiveEventListener.class);

    private SimpMessagingTemplate messagingTemplate;
    private InteractiveRepository repository;

    @Autowired
    private UsersDao usersDao;

    public InteractiveEventListener(SimpMessagingTemplate messagingTemplate, InteractiveRepository repository) {
        this.messagingTemplate = messagingTemplate;
        this.repository = repository;
    }

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = headers.getUser().getName();
        Boolean interactiveNotification = Boolean.valueOf(headers.getFirstNativeHeader("interactiveNotification"));
        Boolean interactiveLike = Boolean.valueOf(headers.getFirstNativeHeader("interactiveLike"));
        Boolean interactiveComment = Boolean.valueOf(headers.getFirstNativeHeader("interactiveComment"));
        Integer idUser = usersDao.getUserProfile(username).getUserEntityId();
        repository.addNotificationCon(idUser, username);

        String log = "success connect user: " + username
                + ",  idUser: " + idUser;
        if (interactiveLike || interactiveComment) {
            Integer mainObjectId = Integer.parseInt(headers.getFirstNativeHeader("mainObjectId"));
            repository.addCommonCon(mainObjectId, username);
            log += ",  mainObjectId: " + mainObjectId;
        }
        log += ", interactiveNotification: " + interactiveNotification
                + ",  interactiveLike: " + interactiveLike
                + ",  interactiveComment: " + interactiveComment;
        logger.info(log);
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = headers.getUser().getName();
        Integer idUser = usersDao.getUserProfile(username).getUserEntityId();

        Integer mainObjectId = repository.getMainObjectId(username);
        //TODO synchronized it
        Boolean isSuccess = repository.rmCommonCon(username);
        Boolean isSuccessRm = repository.rmNotificationCon(idUser);

        if (mainObjectId == null) {
            logger.info("user: " + username + " already removed ");
        } else {
            String info = "remove user: " + username;
            if (isSuccessRm) {
                info += ", idUser: " + idUser;
            }
            info += ",  mainObjectId: " + mainObjectId
                    + ", success: " + isSuccess;
            logger.info(info);
        }
    }
}
