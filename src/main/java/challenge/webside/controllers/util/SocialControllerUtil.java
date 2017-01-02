package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeDefinitionStatus;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStatus;
import challenge.dbside.models.Comment;
import challenge.dbside.models.User;
import challenge.webside.dao.UsersDao;
import challenge.webside.model.UserConnection;
import challenge.webside.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.authorization.UserActionsProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Component
public class SocialControllerUtil {

    private static final String USER_CONNECTION = "MY_USER_CONNECTION";
    private static final String USER_PROFILE = "MY_USER_PROFILE";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UsersDao usersDao;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    public void dumpDbInfo() {
        try {
            Connection c = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData md = c.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                if (rs.getString(4).equalsIgnoreCase("TABLE")) {

                    String tableName = rs.getString(3);
                    List<String> sl = jdbcTemplate.query("select * from " + tableName, (ResultSet rs1, int rowNum) -> {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 1; i <= rs1.getMetaData().getColumnCount(); i++) {
                            sb.append(rs1.getString(i)).append(' ');
                        }
                        return sb.toString();
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void setModel(HttpServletRequest request, Principal currentUser, Model model) {
        // SecurityContext ctx = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        String userId = currentUser == null ? null : currentUser.getName();
        String path = request.getRequestURI();
        HttpSession session = request.getSession();

        UserConnection connection = null;
        UserProfile profile = null;
        String displayName = null;

        // Collect info if the user is logged in, i.e. userId is set
        if (userId != null) {
            // Get the current UserConnection from the http session
            connection = getUserConnection(session, userId);
            // Get the current UserProfile from the http session
            profile = getUserProfile(session, userId);
            // Compile the best display name from the connection and the profile
            displayName = getDisplayName(connection, profile);
        }

        Throwable exception = (Throwable) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        model.addAttribute("exception", exception == null ? null : exception.getMessage());
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentUserProfile", profile);
        model.addAttribute("currentUserConnection", connection);
        model.addAttribute("currentUserDisplayName", displayName);
        //TODO:bike:-)
        model.addAttribute("foto", "AvaDefault.jpg");
        if (profile != null)
            model.addAttribute("challengeRequests", ((User)serviceEntity.findById(profile.getUserEntityId(), User.class)).getChallengeRequests());
    }

    public void setModelForMain(HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        model.addAttribute("mainChallenge", serviceEntity.getAll(ChallengeDefinition.class).get(0));
        model.addAttribute("Challenges", serviceEntity.getAll(ChallengeDefinition.class));     
    }

    public void setModelForChallengeShow(int id, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        model.addAttribute("challenge", (ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class));
        model.addAttribute("listOfAcceptors", ((ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class)).getAllAcceptors());
        model.addAttribute("userProfile", getSignedUpUser(request, currentUser));
        Comment comment = new Comment();
        comment.setDate(new Date());
        comment.setAuthor(getSignedUpUser(request, currentUser));
        model.addAttribute("comment", comment);
        model.addAttribute("comments", ((ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class)).getComments());
    }
    
    public void setModelForNewOrUpdatedChalShow(ChallengeDefinition challenge, HttpServletRequest request, Principal currentUser, Model model) {

        setModel(request, currentUser, model);
        User curDBUser = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        
        if (challenge.getId() != null) {
            ChallengeDefinition chalToUpdate = (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class);
            chalToUpdate.setDescription(challenge.getDescription());
            chalToUpdate.setName(challenge.getName());
            chalToUpdate.setDate(challenge.getDate());
            chalToUpdate.setImageRef(challenge.getImageRef());
            //TODO:check if creator
            if (Objects.equals(chalToUpdate.getCreator().getId(), curDBUser.getId())) {
                serviceEntity.update(chalToUpdate);
            }
        } else {
            serviceEntity.save(challenge);
            curDBUser.addChallenge(challenge);
            serviceEntity.update(curDBUser);
        }
        model.addAttribute("challenge", (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class));
        model.addAttribute("listOfAcceptors", ((ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class)).getAllAcceptors());
    }
    
    public void setModelForNewComment(int id, HttpServletRequest request, Principal currentUser, Model model, Comment comment) {
        setModel(request, currentUser, model);
        User curDBUser = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        ChallengeDefinition currentChallenge = (ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class);
        
        comment.setDate(new Date());
        serviceEntity.save(comment);
        
        currentChallenge.addChild(comment);
        serviceEntity.update(currentChallenge);
        
        curDBUser.addComment(comment);
        serviceEntity.update(curDBUser);
    }

    public void setModelForNewChallenge(HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        ChallengeDefinition chalDefNew = new ChallengeDefinition();
        chalDefNew.setDate(new Date());
        model.addAttribute("challenge", chalDefNew);
    }

    protected UserProfile getUserProfile(HttpSession session, String userId) {
        UserProfile profile = (UserProfile) session.getAttribute(USER_PROFILE);

        // Reload from persistence storage if not set or invalid (i.e. no valid userId)
        if (profile == null || !userId.equals(profile.getUserId())) {
            profile = usersDao.getUserProfile(userId);
            session.setAttribute(USER_PROFILE, profile);
        }
        return profile;
    }

    /**
     * Get the current UserConnection from the http session
     *
     * @param session
     * @param userId
     * @return
     */
    public UserConnection getUserConnection(HttpSession session, String userId) {
        UserConnection connection;
        connection = (UserConnection) session.getAttribute(USER_CONNECTION);

        // Reload from persistence storage if not set or invalid (i.e. no valid userId)
        if (connection == null || !userId.equals(connection.getUserId())) {
            connection = usersDao.getUserConnection(userId);
            session.setAttribute(USER_CONNECTION, connection);
        }
        return connection;
    }

    protected String getDisplayName(UserConnection connection, UserProfile profile) {

        // The name is set differently in different providers so we better look in both places...
        if (connection.getDisplayName() != null) {
            return connection.getDisplayName();
        } else {
            return profile.getName();
        }
    }
    
    private User getSignedUpUser(HttpServletRequest request, Principal currentUser) {
        return (User) serviceEntity.findById(getUserProfile(request.getSession(), 
                currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
    }

    public void setProfileShow(int userDBId, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        User userWhichProfileRequested = (User) serviceEntity.findById(userDBId, User.class);
        
        User signedUpUser = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        
        model.addAttribute("userProfile", userWhichProfileRequested);             
        model.addAttribute("listOfDefined", userWhichProfileRequested.getChallenges());
        model.addAttribute("listOfAccepted", userWhichProfileRequested.getAcceptedChallenges());
        model.addAttribute("actions", UserActionsProvider.getActionsForProfile(signedUpUser, userWhichProfileRequested));
    }
    
    public void setModelForAcceptOrDeclineChallenge(HttpServletRequest request, Principal currentUser, Model model, int chalId, boolean accept) {
        setModel(request, currentUser, model); 
        ChallengeInstance chalToAccept = (ChallengeInstance)serviceEntity.findById(chalId, ChallengeInstance.class);
        User user = getSignedUpUser(request, currentUser);
        if (accept) 
            user.acceptChallenge(chalToAccept);
        else
            user.declineChallenge(chalToAccept);
        serviceEntity.update(user);
        
        model.addAttribute("userProfile", user);
        model.addAttribute("listOfDefined", user.getChallenges());
        model.addAttribute("listOfAccepted", user.getAcceptedChallenges());
        model.addAttribute("challengeRequests", user.getChallengeRequests());
        model.addAttribute("actions", UserActionsProvider.getActionsForProfile(user, user));
    }
    
    public void setModelForAcceptChallengeDefinition(HttpServletRequest request, Principal currentUser, Model model, int chalId) {
        setModel(request, currentUser, model);
        ChallengeDefinition chalToAccept = (ChallengeDefinition)serviceEntity.findById(chalId, ChallengeDefinition.class);
        User user = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        
        if (chalToAccept.getStatus() != ChallengeDefinitionStatus.ACCEPTED) {
            ChallengeInstance chalInstance = new ChallengeInstance(chalToAccept);      
            chalInstance.setStatus(ChallengeStatus.ACCEPTED);
            serviceEntity.save(chalInstance);
            chalToAccept.setStatus(ChallengeDefinitionStatus.ACCEPTED);
            chalInstance.setAcceptor(user);
            user.addAcceptedChallenge(chalInstance);
            serviceEntity.update(user);
        }
        
        model.addAttribute("userProfile", user);        
        model.addAttribute("listOfDefined", user.getChallenges());
        model.addAttribute("listOfAccepted", user.getAcceptedChallenges());
        model.addAttribute("challengeRequests", user.getChallengeRequests());
        model.addAttribute("actions", UserActionsProvider.getActionsForProfile(user, user));    
    }
    
    
    /*
     * Select user from list 4 challenge
     * click 'throw' to challenge from your panel (may be make from all place?) 
     * 
     * */
    public void setModelForThrowChallenge2User(HttpServletRequest request, Principal currentUser, Model model, int chalId) {
    	setModel(request, currentUser, model);
    	//TODO: ignored all include users
    	List<User> users = serviceEntity.getAll(User.class);
    	
    	model.addAttribute("listSomething", users);
    	model.addAttribute("idParent", chalId);
        model.addAttribute("challengeRequests", getSignedUpUser(request, currentUser).getChallengeRequests());
    	model.addAttribute("handler", "throwChallengeFromChallengeList");
    }
    
    public void throwChallenge2User(int userId, int challengeId) {    	
    	ChallengeDefinition chal = (ChallengeDefinition)serviceEntity.findById(challengeId, ChallengeDefinition.class);
    	User user = (User)serviceEntity.findById(userId, User.class);
    	
    	ChallengeInstance chalIns = new ChallengeInstance();
    	chalIns.setName(chal.getName());
    	chalIns.setStatus(ChallengeStatus.AWAITING);
    	serviceEntity.save(chalIns);
    	    	
    	chal.getChildren().add(chalIns);
    	serviceEntity.update(chal);
    	
    	user.addAcceptedChallenge(chalIns);
    	serviceEntity.update(user);    	
    }
    
    
    /*
     * Select challenge from list 4 user 
     * click 'throw user' from your profile
     * 
     */
    public void setModelForThrowUser2Challenge(HttpServletRequest request, Principal currentUser, Model model, int userId) {
    	setModel(request, currentUser, model);
    	//TODO: ignored all include users
    	User user = (User)serviceEntity.findById(userId, User.class);
    	
    	model.addAttribute("listSomething", user.getChallenges());
    	model.addAttribute("idParent", userId);
    	model.addAttribute("handler", "throwChallengeFromUserList");
        model.addAttribute("challengeRequests", user.getChallengeRequests());
    }
    
    
    
    public void setModelForShowFriends(HttpServletRequest request, Principal currentUser, Model model, int userId) {
    	setModel(request, currentUser, model);
    	
    	User user = (User)serviceEntity.findById(userId, User.class);
    	
    	model.addAttribute("listSomething", user.getFriends());
    	model.addAttribute("idParent", userId);
    	model.addAttribute("handler", "profile");
        model.addAttribute("challengeRequests", user.getChallengeRequests());    	
    } 
}
