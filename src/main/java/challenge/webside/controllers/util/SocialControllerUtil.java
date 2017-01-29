package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Comment;
import challenge.dbside.models.Image;
import challenge.dbside.models.User;
import challenge.dbside.models.dbentity.DBSource;
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
import challenge.webside.authorization.thymeleaf.AuthorizationDialect;
import challenge.webside.imagesstorage.ImageStoreService;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

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
    private AuthorizationDialect dialect;

    @Autowired
    private UserActionsProvider actionsProvider;

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
            List<ChallengeInstance> challengeList = ((User) serviceEntity.findById(profile.getUserEntityId(), User.class)).getChallengeRequests();
            model.addAttribute("challengeRequests", challengeList);
        }
    }

    public void setModelForMain(HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);

        ChallengeDefinition mainChallenge = (ChallengeDefinition) serviceEntity.getAll(ChallengeDefinition.class).get(0);
        List<ChallengeDefinition> challenges = serviceEntity.getAll(ChallengeDefinition.class);
        model.addAttribute("mainChallenge", mainChallenge);
        model.addAttribute("challenges", challenges);
    }

    public void setModelForChallengeShow(int id, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);

        ChallengeDefinition challenge = (ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class);
        List<User> listOfAcceptors = ((ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class)).getAllAcceptors();
        User user = getSignedUpUser(request, currentUser);
        dialect.setActions(actionsProvider.getActionsForChallengeDefinition(user, challenge));
        model.addAttribute("challenge", challenge);
        model.addAttribute("listOfAcceptors", listOfAcceptors);
        model.addAttribute("userProfile", user);
        setModelForComments(id, request, currentUser, model);
    }

    public void setModelForNewOrUpdatedChalShow(ChallengeDefinition challenge, HttpServletRequest request, Principal currentUser, Model model, String image) {
        setModel(request, currentUser, model);
        User curDBUser = ((User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class));

        if (challenge.getId() != null) {
            ChallengeDefinition chalToUpdate = (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class);
            chalToUpdate.setDescription(challenge.getDescription());
            chalToUpdate.setName(challenge.getName());
            chalToUpdate.setDate(challenge.getDate());
            //TODO:check if creator
            if (Objects.equals(chalToUpdate.getCreator().getId(), curDBUser.getId())) {
                serviceEntity.update(chalToUpdate);
            }
            if (!image.isEmpty()) {
                Image oldImage = chalToUpdate.getMainImageEntity();
                oldImage.setIsMain(Boolean.FALSE);
                serviceEntity.update(oldImage);
            }
        } else {
            challenge.setStatus(ChallengeDefinitionStatus.CREATED);
            challenge.setCreator(curDBUser);
            serviceEntity.save(challenge);
        }

        challenge = (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class);

        //need to update or create image
        if (!image.isEmpty() && !StringUtils.isNumeric(image)) {
            String base64Image = image.split(",")[1];
            byte[] array = Base64.decodeBase64(base64Image);
            Image imageEntity = new Image();
            imageEntity.setIsMain(Boolean.TRUE);
            serviceEntity.save(imageEntity);
            challenge.addImage(imageEntity);
            serviceEntity.update(challenge);
            try {
                ImageStoreService.saveImage(array, imageEntity);
                serviceEntity.update(imageEntity);
            } catch (Exception ex) {
                Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (StringUtils.isNumeric(image)) {
            Image newMainImage = (Image) serviceEntity.findById(Integer.valueOf(image), Image.class);
            newMainImage.setIsMain(Boolean.TRUE);
            serviceEntity.update(newMainImage);
        }
        model.addAttribute("challenge", challenge);
        model.addAttribute("listOfAcceptors", challenge.getAllAcceptors());
        setModelForComments(challenge.getId(), request, currentUser, model);
    }

    public void setModelForEditProfile(int userId, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);

        User user = (User) serviceEntity.findById(userId, User.class);
        model.addAttribute("userProfile", user);

    }

    public void setModelForUpdatedProfile(User user, HttpServletRequest request, Principal currentUser, Model model, String image) {
        setModel(request, currentUser, model);
        User userToUpdate = (User) serviceEntity.findById(user.getId(), User.class);
        userToUpdate.setName(user.getName());
        //TODO:check if creator
        serviceEntity.update(userToUpdate);

        user = (User) serviceEntity.findById(user.getId(), User.class);

        if (!image.isEmpty() && !StringUtils.isNumeric(image)) {
            String base64Image = image.split(",")[1];
            byte[] array = Base64.decodeBase64(base64Image);
            Image imageEntity = new Image();
            imageEntity.setIsMain(Boolean.TRUE);
            serviceEntity.save(imageEntity);

            Image oldImage = user.getMainImageEntity();
            oldImage.setIsMain(Boolean.FALSE);
            serviceEntity.update(oldImage);

            try {
                ImageStoreService.saveImage(array, imageEntity);
                serviceEntity.update(imageEntity);
            } catch (Exception ex) {
                Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            user.addImage(imageEntity);
            serviceEntity.update(user);
        } else if (StringUtils.isNumeric(image)) {
            Image oldImage = user.getMainImageEntity();
            oldImage.setIsMain(Boolean.FALSE);
            serviceEntity.update(oldImage);

            Image newMainImage = (Image) serviceEntity.findById(Integer.valueOf(image), Image.class);
            newMainImage.setIsMain(Boolean.TRUE);
            serviceEntity.update(newMainImage);
        }

        setProfileShow(user.getId(), request, currentUser, model);
    }

    private void setModelForComments(int chalId, HttpServletRequest request, Principal currentUser, Model model) {
        Comment comment = new Comment();
        comment.setDate(new Date());
        comment.setAuthor(getSignedUpUser(request, currentUser));
        model.addAttribute("comment", comment);
        int commentsCount = 0;

        List<Comment> comments = (((ChallengeDefinition) serviceEntity.findById(chalId, ChallengeDefinition.class)).getComments());
        for (Comment comm : comments) {
            commentsCount++;
            commentsCount += comm.getSubCommentsCount();
        }
        model.addAttribute("commentsCount", commentsCount);
        model.addAttribute("comments", comments);
    }

    public void addNewComment(int chalId, HttpServletRequest request, Principal currentUser, Model model, Comment comment) {
        User curDBUser = (User) serviceEntity.
                findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);

        ChallengeDefinition currentChallenge = (ChallengeDefinition) serviceEntity.findById(chalId, ChallengeDefinition.class);
        comment.setDate(new Date());
        comment.setAuthor(curDBUser);
        serviceEntity.save(comment);

        currentChallenge.addComment(comment);
        serviceEntity.update(currentChallenge);
    }

    public void addNewReply(int id, HttpServletRequest request, Principal currentUser, Model model, Comment comment) {
        setModel(request, currentUser, model);
        User curDBUser = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);

        Comment parentComment = (Comment) serviceEntity.findById(id, Comment.class);

        comment.setParentComment(parentComment);
        comment.setDate(new Date());
        comment.setAuthor(curDBUser);
        serviceEntity.save(comment);
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

    public User getSignedUpUser(HttpServletRequest request, Principal currentUser) {
        User user = (User) serviceEntity.findById(getUserProfile(request.getSession(),
                currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        return user;
    }

    public void setProfileShow(int userDBId, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        User userWhichProfileRequested = (User) serviceEntity.findById(userDBId, User.class);
        User signedUpUser = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);

        model.addAttribute("userProfile", userWhichProfileRequested);
        model.addAttribute("listOfDefined", userWhichProfileRequested.getChallenges());
        model.addAttribute("currentDBUser", getSignedUpUser(request, currentUser));
        model.addAttribute("listOfAccepted", userWhichProfileRequested.getAcceptedChallenges());
        dialect.setActions(actionsProvider.getActionsForProfile(signedUpUser, userWhichProfileRequested));
        model.addAttribute("friends", signedUpUser.getFriends());
    }

    public void setModelForAcceptOrDeclineChallenge(HttpServletRequest request, Principal currentUser, Model model, int chalId, boolean accept) {
        setModel(request, currentUser, model);
        ChallengeInstance chalToAccept = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);

        User user = getSignedUpUser(request, currentUser);
        if (accept) {
            user.acceptChallenge(chalToAccept);
        } else {
            for (DBSource childDB : chalToAccept.getDataSource().getChildren()) {
                serviceEntity.delete(new Image(childDB));
            }
            serviceEntity.delete(chalToAccept);
        }
        serviceEntity.update(user);
        dialect.setActions(actionsProvider.getActionsForProfile(user, user));
    }

    public void setModelForAcceptChallengeDefinition(HttpServletRequest request, Principal currentUser, Model model, int chalId) {
        setModel(request, currentUser, model);
        ChallengeDefinition chalToAccept = (ChallengeDefinition) serviceEntity.findById(chalId, ChallengeDefinition.class);
        User user = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        if (chalToAccept.getStatus() != ChallengeDefinitionStatus.ACCEPTED) {
            ChallengeInstance chalInstance = new ChallengeInstance(chalToAccept);
            chalInstance.setStatus(ChallengeStatus.ACCEPTED);
            serviceEntity.save(chalInstance);
            Image image = chalToAccept.getMainImageEntity();
            serviceEntity.save(image);
            chalInstance.addImage(image);
            serviceEntity.update(chalInstance);
            chalToAccept.setStatus(ChallengeDefinitionStatus.ACCEPTED);
            chalInstance.setAcceptor(user);
            user.addAcceptedChallenge(chalInstance);
            serviceEntity.update(user);
        }
        dialect.setActions(actionsProvider.getActionsForProfile(user, user));
    }

    public void throwChallenge(int userId, int challengeId) {
        ChallengeDefinition chal = (ChallengeDefinition) serviceEntity.findById(challengeId, ChallengeDefinition.class);
        User user = (User) serviceEntity.findById(userId, User.class);

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
        User user = (User) serviceEntity.findById(userId, User.class);

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

    public List<ChallengeDefinition> filterChallenges(String filter, int userId) {
        User user = (User) serviceEntity.findById(userId, User.class);

        List<ChallengeDefinition> challenges = user.getChallenges();
        List<ChallengeDefinition> filteredChallenges = new ArrayList<>();
        for (int i = 0; i < challenges.size(); i++) {
            String name = ((ChallengeDefinition) challenges.get(i)).getName();
            if (name.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredChallenges.add((ChallengeDefinition) challenges.get(i));
            }
        }
        return filteredChallenges;
    }

    public void setModelForShowFriends(HttpServletRequest request, Principal currentUser, Model model, int userId) {
        setModel(request, currentUser, model);

        User user = (User) serviceEntity.findById(userId, User.class);
        model.addAttribute("listSomething", user.getFriends());
        model.addAttribute("idParent", userId);
        model.addAttribute("handler", "profile");
    }
}
