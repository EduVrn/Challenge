package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Comment;
import challenge.dbside.models.User;
import challenge.dbside.models.status.ChallengeDefinitionStatus;
import challenge.dbside.models.status.ChallengeStatus;
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
import java.util.Iterator;
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
        if (profile != null) {
        	List challengeList;
        	try {
        		challengeList = ((User) serviceEntity.findById(profile.getUserEntityId(), User.class)).getChallengeRequests();
        	}
        	catch(Exception ex) {
        		ex.printStackTrace();
        		challengeList = new ArrayList<ChallengeInstance>();
        	}
            model.addAttribute("challengeRequests", challengeList);
        }
    }

    public void setModelForMain(HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        
        //TODO: exception handler
        ChallengeDefinition mainChallenge;  
        try {
        	mainChallenge = (ChallengeDefinition)serviceEntity.getAll(ChallengeDefinition.class).get(0);
        }
        catch(Exception e) {
        	e.printStackTrace();
        	mainChallenge = new ChallengeDefinition();
        }
        
        List<ChallengeDefinition> challenges;
        try {
        	challenges = serviceEntity.getAll(ChallengeDefinition.class);
        }
        catch(Exception ex) {        	
        	ex.printStackTrace();
        	challenges = new ArrayList<ChallengeDefinition>();
        }
        
        model.addAttribute("mainChallenge", mainChallenge);
    	model.addAttribute("Challenges", challenges);
    }

    public void setModelForChallengeShow(int id, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        
        ChallengeDefinition challenge;
        try {
        	challenge = (ChallengeDefinition)serviceEntity.findById(id, ChallengeDefinition.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	challenge = new ChallengeDefinition();
        }
        

        model.addAttribute("challenge", challenge);
        
        List<User> listOfAcceptors;
        try {
        	listOfAcceptors = ((ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class)).getAllAcceptors();
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	listOfAcceptors = new ArrayList<User>();
        }
        //
        model.addAttribute("listOfAcceptors", listOfAcceptors);
        model.addAttribute("userProfile", getSignedUpUser(request, currentUser));
        Comment comment = new Comment();
        comment.setDate(new Date());
        comment.setAuthor(getSignedUpUser(request, currentUser));
        model.addAttribute("comment", comment);
        int commentsCount = 0;
        
        
        List<Comment> listComment;
        try {	
        	listComment = (((ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class)).getComments());
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	listComment = new ArrayList<Comment>();
        }
        
        for (Comment comm : listComment) {
            commentsCount++;
            commentsCount += comm.getSubCommentsCount();
        }
        model.addAttribute("commentsCount", commentsCount);
        model.addAttribute("comments", listComment);
    }

    public void setModelForNewOrUpdatedChalShow(ChallengeDefinition challenge, HttpServletRequest request, Principal currentUser, Model model) {

        setModel(request, currentUser, model);
        User curDBUser;
        try {
        	curDBUser = ((User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class));
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	//TODO: change it 
        	curDBUser = new User();
        }
        
        if (challenge.getId() != null) {
        	
            ChallengeDefinition chalToUpdate; 
            try {
            	chalToUpdate = (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class);
            }
            catch(Exception ex) {
            	ex.printStackTrace();
            	chalToUpdate = new ChallengeDefinition();
            }
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
        
       
        try {
        		      //(ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class)
        	challenge = (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class); 
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	challenge = new ChallengeDefinition();
        }        
        model.addAttribute("challenge", challenge);
        
        List<User> listOfAcceptors = challenge.getAllAcceptors();
        model.addAttribute("listOfAcceptors", listOfAcceptors);
    }

    public void setModelForNewComment(int id, HttpServletRequest request, Principal currentUser, Model model, Comment comment) {
        setModel(request, currentUser, model);
        User curDBUser;
        
        try {
        	curDBUser = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	curDBUser = new User();
        }
        ChallengeDefinition currentChallenge; 
        
        try {
        	currentChallenge = (ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	currentChallenge = new ChallengeDefinition();
        }
        
        comment.setDate(new Date());
        comment.setAuthor(curDBUser);
        serviceEntity.save(comment);

        currentChallenge.addComment(comment);
        //currentChallenge.setCreator(curDBUser);
        serviceEntity.update(currentChallenge);
        
        try {
        	Comment c = (Comment)serviceEntity.findById(comment.getId(), Comment.class);
        	User ut = c.getAuthor();
        	String str = ut.getImageRef();
        	System.out.println("dd");
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
        
        //TODO: what wase it? i'm undestand it ???
        //curDBUser.addComment(comment);
        //serviceEntity.update(curDBUser);
    }

    public void setModelForNewReply(int id, HttpServletRequest request, Principal currentUser, Model model, Comment comment) {
        setModel(request, currentUser, model);
        User curDBUser;
        try {
        	curDBUser = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	curDBUser = new User();
        }
        Comment parentComment;
        try {
        	parentComment = (Comment) serviceEntity.findById(id, Comment.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	parentComment = new Comment();
        }
        
        
        comment.setParent(parentComment);
        comment.setDate(new Date());
        comment.setAuthor(curDBUser);
        serviceEntity.save(comment);

        //TODO: change it
        /*
        parentComment.addChild(comment);*/
        //parentComment.addComment(comment);

        serviceEntity.update(parentComment);
        

        //Comment commentT = (Comment)serviceEntity.findById(parentComment.getId(), Comment.class);
        
        
        //curDBUser.addComment(comment);
        //serviceEntity.update(curDBUser);
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
    	User user;
    	
    	try {
    		user = (User) serviceEntity.findById(getUserProfile(request.getSession(),
    				currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    		user = new User();
    	}    	
    	return user;
    }

    public void setProfileShow(int userDBId, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        User userWhichProfileRequested; 
        try {
        	userWhichProfileRequested = (User) serviceEntity.findById(userDBId, User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	userWhichProfileRequested = new User();
        }

        User signedUpUser;
        try {
        	signedUpUser = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	signedUpUser = new User();
        }

        model.addAttribute("userProfile", userWhichProfileRequested);
        model.addAttribute("listOfDefined", userWhichProfileRequested.getChallenges());
        model.addAttribute("listOfAccepted", userWhichProfileRequested.getAcceptedChallenges());
        model.addAttribute("actions", UserActionsProvider.getActionsForProfile(signedUpUser, userWhichProfileRequested));
        model.addAttribute("friends", signedUpUser.getFriends());
    }

    public void setModelForAcceptOrDeclineChallenge(HttpServletRequest request, Principal currentUser, Model model, int chalId, boolean accept) {
        setModel(request, currentUser, model);
        ChallengeInstance chalToAccept;
        
        try {
        	chalToAccept = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	chalToAccept = new ChallengeInstance();
        }
        
        
        User user = getSignedUpUser(request, currentUser);
        if (accept) {
            user.acceptChallenge(chalToAccept);
        } else {
            serviceEntity.delete(chalToAccept);
        }
        serviceEntity.update(user);

        try {
        	User user1 = (User)serviceEntity.findById(user.getId(), User.class);
        	List<ChallengeInstance> list = user1.getAcceptedChallenges();
        	
        	System.out.println(list.size());
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
        
        
        
        List<ChallengeInstance> list = user.getAcceptedChallenges();
        
        
        model.addAttribute("userProfile", user);
        model.addAttribute("listOfDefined", user.getChallenges());
        model.addAttribute("listOfAccepted", user.getAcceptedChallenges());
        model.addAttribute("challengeRequests", user.getChallengeRequests());
        model.addAttribute("actions", UserActionsProvider.getActionsForProfile(user, user));
    }

    public void setModelForAcceptChallengeDefinition(HttpServletRequest request, Principal currentUser, Model model, int chalId) {
        setModel(request, currentUser, model);
        ChallengeDefinition chalToAccept;
        try {
        	chalToAccept = (ChallengeDefinition) serviceEntity.findById(chalId, ChallengeDefinition.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	chalToAccept = new ChallengeDefinition();
        }
        User user;	
        try {
        	user = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	user = new User();
        }
        
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
        List<User> users;
        try {
        	users = serviceEntity.getAll(User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	users = new ArrayList<User>();
        }
        
        model.addAttribute("listSomething", users);
        model.addAttribute("idParent", chalId);
        model.addAttribute("challengeRequests", getSignedUpUser(request, currentUser).getChallengeRequests());
        model.addAttribute("handler", "throwChallengeFromChallengeList");
    }

    public void throwChallenge2User(int userId, int challengeId) {
        ChallengeDefinition chal;
        
        try {
        	chal = (ChallengeDefinition) serviceEntity.findById(challengeId, ChallengeDefinition.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	chal = new ChallengeDefinition();
        }

        User user;
        try {
        	user = (User) serviceEntity.findById(userId, User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	user = new User();
        }
        
        ChallengeInstance chalIns = new ChallengeInstance();
        chalIns.setName(chal.getName());
        chalIns.setStatus(ChallengeStatus.AWAITING);
        serviceEntity.save(chalIns);

        
        chal.addChallengeInstance(chalIns);        
        serviceEntity.update(chal);

        user.addAcceptedChallenge(chalIns);
        serviceEntity.update(user);
    }

    public List<User> filterUsers(String filter, int userId) {
        User user;
        try {
        	user = (User) serviceEntity.findById(userId, User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	user = new User();
        }
        
        List<User> allFriends = user.getFriends();
        List<User> filteredFriends = new ArrayList<>();
        for (int i = 0; i < allFriends.size(); i++) {
            String name = ((User) allFriends.get(i)).getName();
            if (name.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredFriends.add((User) allFriends.get(i));
            }
        }
        return filteredFriends;
    }

    /*
     * Select challenge from list 4 user 
     * click 'throw user' from your profile
     * 
     */
    public void setModelForThrowUser2Challenge(HttpServletRequest request, Principal currentUser, Model model, int userId) {
        setModel(request, currentUser, model);
        //TODO: ignored all include users
        User user;
        try {
        	user = (User) serviceEntity.findById(userId, User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	user = new User();
        }
        
        model.addAttribute("listSomething", user.getChallenges());
        model.addAttribute("idParent", userId);
        model.addAttribute("handler", "throwChallengeFromUserList");
        model.addAttribute("challengeRequests", user.getChallengeRequests());
    }

    public void setModelForShowFriends(HttpServletRequest request, Principal currentUser, Model model, int userId) {
        setModel(request, currentUser, model);

        User user;
        try {
        	user = (User) serviceEntity.findById(userId, User.class);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        	user = new User();
        }

        model.addAttribute("listSomething", user.getFriends());
        model.addAttribute("idParent", userId);
        model.addAttribute("handler", "profile");
        model.addAttribute("challengeRequests", user.getChallengeRequests());
    }
}
