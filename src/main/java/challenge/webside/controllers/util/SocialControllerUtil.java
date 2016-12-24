package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeDefinitionStatus;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStatus;
import challenge.dbside.models.User;
import challenge.webside.dao.UsersDao;
import challenge.webside.model.UserConnection;
import challenge.webside.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import challenge.dbside.services.ini.MediaService;

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
        String data = null;

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

        //TODO: access to context without instaniating
        //ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/beans.xml");        
        //MediaServiceEntity serviceEntity = (MediaServiceEntity) context.getBean("storageServiceUser");
        // Update the model with the information we collected
        model.addAttribute("exception", exception == null ? null : exception.getMessage());
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentUserProfile", profile);
        model.addAttribute("currentUserConnection", connection);
        model.addAttribute("currentUserDisplayName", displayName);
        model.addAttribute("currentData", data);
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

    public void setProfileShow(int userDBId, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        User userWhichProfileRequested = (User) serviceEntity.findById(userDBId, User.class);
        model.addAttribute("listOfDefined", userWhichProfileRequested.getChallenges());
        model.addAttribute("listOfAccepted", userWhichProfileRequested.getAcceptedChallenges());
        model.addAttribute("challengeRequests", userWhichProfileRequested.getChallengeRequests());
    }
    
    public void setModelForAcceptOrDeclineChallenge(HttpServletRequest request, Principal currentUser, Model model, int chalId, boolean accept) {
        setModel(request, currentUser, model); 
        ChallengeInstance chalToAccept = (ChallengeInstance)serviceEntity.findById(chalId, ChallengeInstance.class);
        User user = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        if (accept) 
            user.acceptChallenge(chalToAccept);
        else
            user.declineChallenge(chalToAccept);
        serviceEntity.update(user);
        
        model.addAttribute("listOfDefined", user.getChallenges());
        model.addAttribute("listOfAccepted", user.getAcceptedChallenges());
        model.addAttribute("challengeRequests", user.getChallengeRequests());
    }
    
    public void setModelForAcceptChallengeDefinition(HttpServletRequest request, Principal currentUser, Model model, int chalId) {
        setModel(request, currentUser, model);
        ChallengeDefinition chalToAccept = (ChallengeDefinition)serviceEntity.findById(chalId, ChallengeDefinition.class);
        User user = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        
        ChallengeInstance chalInstance = new ChallengeInstance();
        chalInstance.setName(chalToAccept.getName());       
        chalInstance.setStatus(ChallengeStatus.ACCEPTED);
        serviceEntity.save(chalInstance);
        chalToAccept.setStatus(ChallengeDefinitionStatus.ACCEPTED);
        chalInstance.setAcceptor(user);
        user.addAcceptedChallenge(chalInstance);
        serviceEntity.update(user);
        
        model.addAttribute("listOfDefined", user.getChallenges());
        model.addAttribute("listOfAccepted", user.getAcceptedChallenges());
        model.addAttribute("challengeRequests", user.getChallengeRequests());
    }
}
