package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeInstance;
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
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.services.FriendsImportService;

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

    @Autowired
    @Qualifier("twitterFriendsService")
    private FriendsImportService twitter;

    @Autowired
    @Qualifier("githubFriendsService")
    private FriendsImportService github;

    @Autowired
    @Qualifier("facebookFriendsService")
    private FriendsImportService facebook;

    @Autowired
    @Qualifier("vkFriendsService")
    private FriendsImportService vk;

    public void setModel(HttpServletRequest request, Principal currentUser, Model model) {
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
            User user = getSignedUpUser(request, currentUser);
            List<User> friends = null;
            switch (connection.getProviderId().toLowerCase()) {
                case "twitter":
                    friends = twitter.importFriends(connection);
                    break;
                case "github":
                    friends = github.importFriends(connection);
                    break;
                case "facebook":
                    friends = facebook.importFriends(connection);
                    break;
                case "vkontakte":
                    friends = vk.importFriends(connection);
                    break;
            }
            if (friends != null) {
                for (User friend : friends) {
                    user.addFriend(friend);
                }
                serviceEntity.update(user);
            }
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
            List<ChallengeInstance> challengeList = ((User) serviceEntity.findById(profile.getUserEntityId(), User.class)).getChallengeRequests();
            model.addAttribute("challengeRequests", challengeList);
            List<User> friendRequests = ((User) serviceEntity.findById(profile.getUserEntityId(), User.class)).getIncomingFriendRequests();
            model.addAttribute("friendRequests", friendRequests);
        }
    }

    public UserProfile getUserProfile(HttpSession session, String userId) {
        UserProfile profile = (UserProfile) session.getAttribute(USER_PROFILE);

        // Reload from persistence storage if not set or invalid (i.e. no valid userId)
        if (profile == null || !userId.equals(profile.getUserId())) {
            profile = usersDao.getUserProfile(userId);
            session.setAttribute(USER_PROFILE, profile);
        }
        return profile;
    }

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
        if (connection.getDisplayName() != null) {
            return connection.getDisplayName();
        } else {
            return profile.getName();
        }
    }

    public User getSignedUpUser(HttpServletRequest request, Principal currentUser) {
        User user = (User) serviceEntity.findById(getUserProfile(request.getSession(),
                currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        return user;
    }
}
