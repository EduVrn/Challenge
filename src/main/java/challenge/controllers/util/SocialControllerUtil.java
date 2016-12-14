package challenge.controllers.util;

import challenge.dao.ChallengesDao;
import challenge.dao.DataDao;
import challenge.dao.UsersDao;
import challenge.model.UserConnection;
import challenge.model.UserProfile;
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

@Component
public class SocialControllerUtil {

    private static final String USER_CONNECTION = "MY_USER_CONNECTION";
    private static final String USER_PROFILE = "MY_USER_PROFILE";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataDao dataDao;

    @Autowired
    private UsersDao usersDao;

    @Autowired
    private ChallengesDao chalsDao;

    public void dumpDbInfo() {
        try {
            Connection c = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData md = c.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                if (rs.getString(4).equalsIgnoreCase("TABLE")) {

                    String tableName = rs.getString(3);
                    List<String> sl = jdbcTemplate.query("select * from " + tableName,
                            new RowMapper<String>() {
                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                                sb.append(rs.getString(i)).append(' ');
                            }
                            return sb.toString();
                        }
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

            // Get user data from persistence storage
            data = dataDao.getData(userId);
        }

        Throwable exception = (Throwable) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        // Update the model with the information we collected
        model.addAttribute("exception", exception == null ? null : exception.getMessage());
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentUserProfile", profile);
        model.addAttribute("currentUserConnection", connection);
        model.addAttribute("currentUserDisplayName", displayName);
        model.addAttribute("currentData", data);
        model.addAttribute("mainChallenge", chalsDao.getMainChallenge());
        model.addAttribute("Challenges", chalsDao.getChallenges());

    }

    public void setChallengeShow(int id, HttpServletRequest request, Principal currentUser, Model model) {

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

            // Get user data from persistence storage
            data = dataDao.getData(userId);
        }

        Throwable exception = (Throwable) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        // Update the model with the information we collected
        model.addAttribute("exception", exception == null ? null : exception.getMessage());
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentUserProfile", profile);
        model.addAttribute("currentUserConnection", connection);
        model.addAttribute("currentUserDisplayName", displayName);
        model.addAttribute("currentData", data);
        model.addAttribute("email", profile.getEmail());
        model.addAttribute("challenge", chalsDao.getChallengeById(id));

    }

    /**
     * Get the current UserProfile from the http session
     *
     * @param session
     * @param userId
     * @return
     */
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

    /**
     * Compile the best display name from the connection and the profile
     *
     * @param connection
     * @param profile
     * @return
     */
    protected String getDisplayName(UserConnection connection, UserProfile profile) {

        // The name is set differently in different providers so we better look in both places...
        if (connection.getDisplayName() != null) {
            return connection.getDisplayName();
        } else {
            return profile.getName();
        }
    }
}
