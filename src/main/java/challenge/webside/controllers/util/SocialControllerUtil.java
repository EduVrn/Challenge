package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStep;
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
import challenge.webside.services.FriendsImportService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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

    public void setModelForBadDateNewChal(ChallengeDefinition challenge, HttpServletRequest request, Principal currentUser, Model model, String image, String imageName) {
        setModel(request, currentUser, model);
        if (challenge.getId() != null) {
            //   ChallengeDefinition challengeToSend = (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class);
            // challengeToSend.setDate(new Date());
            model.addAttribute("challenge", challenge);
            model.addAttribute("image64", image);
            model.addAttribute("imageName", imageName);
        } else {
            // challenge.setDate(new Date());
            model.addAttribute("challenge", challenge);
            model.addAttribute("image64", image);
            model.addAttribute("imageName", imageName);
        }
    }

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
        setModelForComments(challenge.getComments(), request, currentUser, model);
    }

    public void setModelForChallengeInstanceShow(int id, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(id, ChallengeInstance.class);
        User user = getSignedUpUser(request, currentUser);

        Date closingDate = challenge.getClosingDate();
        Date currentDate = new Date();
        long diffInMillies = currentDate.getTime() - closingDate.getTime();
        long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diff >= 5) {
            int votesFor = challenge.getVotesFor().size();
            int votesAgainst = challenge.getVotesAgainst().size();
            challenge.setStatus(votesFor > votesAgainst ? ChallengeStatus.COMPLETED : ChallengeStatus.FAILED);
        }

        dialect.setActions(actionsProvider.getActionsForChallengeInstance(user, challenge));
        model.addAttribute("challenge", challenge);
        ChallengeStep step = new ChallengeStep();
        step.setDate(new Date());
        model.addAttribute("step", step);
        model.addAttribute("userProfile", user);
        List<ChallengeStep> listOfSteps = challenge.getSteps();
        Collections.sort(listOfSteps, ChallengeStep.COMPARE_BY_DATE);
        model.addAttribute("listOfSteps", listOfSteps);
        setModelForComments(challenge.getComments(), request, currentUser, model);
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
                if (oldImage.getId() != null) {
                    oldImage.setIsMain(Boolean.FALSE);
                    serviceEntity.update(oldImage);
                }
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
            try {
                ImageStoreService.saveImage(array, imageEntity);
                serviceEntity.update(imageEntity);
            } catch (Exception ex) {
                Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            challenge.addImage(imageEntity);
            serviceEntity.update(challenge);
        } else if (StringUtils.isNumeric(image)) {
            Image newMainImage = (Image) serviceEntity.findById(Integer.valueOf(image), Image.class);
            newMainImage.setIsMain(Boolean.TRUE);
            serviceEntity.update(newMainImage);
        }
        model.addAttribute("challenge", challenge);
        model.addAttribute("listOfAcceptors", challenge.getAllAcceptors());
        setModelForComments(challenge.getComments(), request, currentUser, model);
    }

    public void setModelForEditProfile(int userId, HttpServletRequest request, Principal currentUser, Model model) {
        setModel(request, currentUser, model);
        User user = (User) serviceEntity.findById(userId, User.class);
        model.addAttribute("userProfile", user);
        model.addAttribute("mapOfNetworks", usersDao.getListOfNetworks(userId));
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

    private void setModelForComments(List<Comment> comments, HttpServletRequest request, Principal currentUser, Model model) {
        Comment comment = new Comment();
        comment.setDate(new Date());
        comment.setAuthor(getSignedUpUser(request, currentUser));
        model.addAttribute("comment", comment);
        int commentsCount = 0;

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

    public void setModelForNewStepForChallenge(HttpServletRequest request, Principal currentUser, Model model, ChallengeStep step, int chalId) {
        ChallengeInstance currentChallenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        serviceEntity.save(step);
        currentChallenge.addStep(step);
        serviceEntity.update(currentChallenge);
    }

    public void addNewInstanceComment(int chalId, HttpServletRequest request, Principal currentUser, Model model, Comment comment) {
        User curDBUser = (User) serviceEntity.
                findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);

        ChallengeInstance currentChallenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
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
        model.addAttribute("listOfSubscripted", userWhichProfileRequested.getSubscriptions());
        dialect.setActions(actionsProvider.getActionsForProfile(signedUpUser, userWhichProfileRequested));
        model.addAttribute("friends", signedUpUser.getFriends());
        model.addAttribute("mapOfNetworks", usersDao.getListOfNetworks(userDBId));
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
            Image image = new Image();
            image.setIsMain(true);
            image.setImageRef(chalToAccept.getMainImageEntity().getImageRef());
            serviceEntity.save(image);

            ChallengeInstance chalInstance = new ChallengeInstance(chalToAccept);
            chalInstance.setStatus(ChallengeStatus.ACCEPTED);
            chalInstance.addImage(image);
            chalInstance.setAcceptor(user);
            serviceEntity.save(chalInstance);

            chalToAccept.setStatus(ChallengeDefinitionStatus.ACCEPTED);
            serviceEntity.update(chalToAccept);
        }
        dialect.setActions(actionsProvider.getActionsForProfile(user, user));
    }

    public void setModelForInstanceSubscribe(HttpServletRequest request, Principal currentUser, Model model, int chalId) {
        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        User user = (User) serviceEntity.findById(getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        user.addSubscription(challenge);
        serviceEntity.update(user);
        challenge.addSubscriber(user);
        serviceEntity.update(challenge);
    }

    public void setModelForCloseChallenge(HttpServletRequest request, Principal currentUser, Model model, int chalId) {
        ChallengeInstance challengeToClose = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        challengeToClose.setStatus(ChallengeStatus.PUT_TO_VOTE);
        challengeToClose.setClosingDate(new Date());
        serviceEntity.update(challengeToClose);
    }

    public void setModelForVote(HttpServletRequest request, Principal currentUser, Model model, int chalId, boolean voteFor) {
        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        User user = getSignedUpUser(request, currentUser);
//        if (challenge.getAcceptor().getId().equals(user.getId())) {
//            return;
//        }
        if (challenge.getVotesFor().contains(user) || challenge.getVotesAgainst().contains(user)) {
            return;
        }
        if (challenge.getStatus() != ChallengeStatus.PUT_TO_VOTE) {
            return;
        }
        if (voteFor) {
            challenge.addVoteFor(user);
            serviceEntity.update(challenge);
            user.addVoteFor(challenge);
            serviceEntity.update(user);
        } else {
            challenge.addVoteAgainst(user);
            serviceEntity.update(challenge);
            user.addVoteAgainst(challenge);
            serviceEntity.update(user);
        }
    }

    public void throwChallenge(int userId, int challengeId, String message) {
        ChallengeDefinition chal = (ChallengeDefinition) serviceEntity.findById(challengeId, ChallengeDefinition.class);
        User user = (User) serviceEntity.findById(userId, User.class);

        Image img = new Image();
        img.setIsMain(true);
        img.setImageRef(chal.getMainImageEntity().getImageRef());
        serviceEntity.save(img);

        ChallengeInstance chalIns = new ChallengeInstance();
        chalIns.setName(chal.getName());
        chalIns.setDate(chal.getDate());
        chalIns.addImage(img);
        chalIns.setStatus(ChallengeStatus.AWAITING);
        chalIns.setMessage(message);
        chalIns.setDescription(chal.getDescription());
        chalIns.setAcceptor(user);

        ChallengeStep step = new ChallengeStep();
        step.setDate(chalIns.getDate());
        step.setMessage(chalIns.getDescription());
        step.setName(chalIns.getName());
        serviceEntity.save(step);

        chalIns.addStep(step);
        serviceEntity.save(chalIns);

        chal.addChallengeInstance(chalIns);
        serviceEntity.update(chal);
    }

    public List<User> filterUsers(String filter, int userId) {
        User user = (User) serviceEntity.findById(userId, User.class);

        List<User> allFriends = user.getFriends();
        List<User> filteredFriends = new ArrayList<>();
        for (User friend : allFriends) {
            String name = friend.getName();
            if (name.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredFriends.add(friend);
            }
        }
        return filteredFriends;
    }

    public List<ChallengeDefinition> filterChallenges(String filter, int userId) {
        User user = (User) serviceEntity.findById(userId, User.class);

        List<ChallengeDefinition> challenges = user.getChallenges();
        List<ChallengeDefinition> filteredChallenges = new ArrayList<>();
        for (ChallengeDefinition challenge : challenges) {
            String name = challenge.getName();
            if (name.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredChallenges.add(challenge);
            }
        }
        return filteredChallenges;
    }

    public void setModelForShowFriends(HttpServletRequest request, Principal currentUser, Model model, int userId) {
        setModel(request, currentUser, model);

        User user = (User) serviceEntity.findById(userId, User.class);
        List<User> fr = user.getFriends();
        model.addAttribute("listSomething", user.getFriends());
        model.addAttribute("idParent", userId);
        model.addAttribute("handler", "profile");
    }
}
