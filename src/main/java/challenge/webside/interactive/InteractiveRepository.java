package challenge.webside.interactive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InteractiveRepository {

	private static final Logger logger = LoggerFactory.getLogger(InteractiveEventListener.class);
	
    //TODO synchronous it??
    private MultiMap storageConnections = MultiValueMap.multiValueMap(new HashMap<Integer, ArrayList>(), ArrayList.class);

    /* use it for management function */
    private Map<String, Integer> actionUser = new HashMap();

    /* use it for notification */
    private Map<Integer, String> notificationUsers = new HashMap();

    public void addCommonCon(Integer mainObjectId, String userId) {
        actionUser.put(userId, mainObjectId);
        storageConnections.put(mainObjectId, userId);
    }

    public Integer getMainObjectId(String userId) {
        return actionUser.get(userId);
    }

    public Boolean rmCommonCon(String userId) {
        Integer mainObjectId = getMainObjectId(userId);
        if (mainObjectId != null) {
        	boolean isRemCon = storageConnections.removeMapping(mainObjectId, userId);
            //storageConnections.remove(mainObjectId, userId);
            boolean isRemUser = actionUser.remove(userId) == null ? false : true;
            //TODO redundant logging
            logger.info("remove mainObjectId: " + mainObjectId + ", userId: " + userId
            		+ ", remCon: " + isRemCon + ", remUser: " + isRemUser );
            return true;
        }
        return false;
    }

    public void addNotificationCon(Integer idUser, String username) {
        notificationUsers.put(idUser, username);
    }

    public boolean rmNotificationCon(Integer idUser) {
    	boolean isRemNotifUser = notificationUsers.remove(idUser) == null ? false : true;
    	//TODO redundant logging 
    	logger.info("remove idUser: " + idUser + ", isRemNotifUser: " + isRemNotifUser);
        return isRemNotifUser;
    }

    public String getNotificationCon(Integer idUser) {
        return notificationUsers.get(idUser);
    }

    public Set<String> getCommonConnection4Object(Integer mainObjectId) {
        Collection c = (Collection) storageConnections.get(mainObjectId);
        Set<String> l = new HashSet();
        if (c != null) {
            l.addAll(c);
        }
        return l;
    }

}
