package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.Image;
import challenge.dbside.models.Request;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.authorization.UserActionsProvider;
import challenge.webside.authorization.thymeleaf.AuthorizationDialect;
import challenge.webside.dao.UsersDao;
import challenge.webside.imagesstorage.ImageStoreService;
import challenge.webside.model.UserProfile;
import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class UserUtil {

    @Autowired
    private SocialControllerUtil util;

    @Autowired
    @Qualifier("EAVStorageServiceUser")
    private MediaService serviceEntity;

    @Autowired
    private UserActionsProvider actionsProvider;

    @Autowired
    private AuthorizationDialect dialect;

    @Autowired
    private UsersDao usersDao;

    @Autowired
    private InteractiveUtil interactiveUtil;

    public void setProfileShow(int userDBId, HttpServletRequest request, UserProfile userProfile, User user, Model model) {
        User userWhichProfileRequested = (User) serviceEntity.findById(userDBId, User.class);
        User signedUpUser = (User) serviceEntity.findById(userProfile.getUserEntityId(), User.class);

        model.addAttribute("userProfile", userWhichProfileRequested);
        model.addAttribute("listOfDefined", userWhichProfileRequested.getChallenges());
        model.addAttribute("currentDBUser", user);
        model.addAttribute("listOfAccepted", userWhichProfileRequested.getAcceptedChallenges());
        model.addAttribute("listOfSubscripted", userWhichProfileRequested.getSubscriptions());
        List<Request> requests = userWhichProfileRequested.getIncomingRequests();
        List<ChallengeDefinition> unacceptedChallenges = new ArrayList<>();
        requests.forEach((r) -> {
            if (r.getSubject() != null) {
                unacceptedChallenges.add(r.getSubject());
            }
        });
        model.addAttribute("listOfUnaccepted", unacceptedChallenges);
        List<User> possibleFriends = util.getCurrentProviderPossibleFriends(request, userProfile.getUserId());
        possibleFriends.removeAll(user.getFriends());
        int countOfUsersToDisplay = 6;
        model.addAttribute("possibleFriends", possibleFriends);
        model.addAttribute("possibleFriendsExtendence", possibleFriends != null ? possibleFriends.size() > countOfUsersToDisplay : false);
        dialect.setActions(actionsProvider.getActionsForProfile(signedUpUser, userWhichProfileRequested));
        model.addAttribute("friends", signedUpUser.getFriends());
        model.addAttribute("mapOfNetworks", usersDao.getListOfNetworks(userDBId));
    }

    public List<User> filterUsers(String filter, User currentUser) {
        List<User> allUsers = serviceEntity.getAll(User.class);
        allUsers.remove(currentUser);
        List<User> filteredUsers = new ArrayList<>();
        for (User user : allUsers) {
            String name = user.getName();
            if (name.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    public void setModelForShowNotFriends(HttpServletRequest request, User currentUser, Model model) {
        List<User> allUsers = serviceEntity.getAll(User.class);
        allUsers.remove(currentUser);
        List<User> friends = currentUser.getFriends();
        allUsers.remove(currentUser);
        List<User> filteredUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (!friends.contains(user)) {
                filteredUsers.add(user);
            }
        }
        model.addAttribute("users", filteredUsers);
        model.addAttribute("curUser", currentUser);
        model.addAttribute("showingAllUsers", false);
    }

    public void setModelForShowFriends(HttpServletRequest request, Principal currentUser, Model model, int userId) {
        User user = (User) serviceEntity.findById(userId, User.class);
        model.addAttribute("listSomething", user.getFriends());
        model.addAttribute("idParent", userId);
        model.addAttribute("handler", "profile");
    }

    public List<User> filterFriends(String filter, int userId) {
        User user = (User) serviceEntity.findById(userId, User.class);
        List<User> allFriends = user.getFriends();
        List<User> filteredFriends = new ArrayList<>();
        allFriends.forEach((friend) -> {
            String name = friend.getName();
            if (name.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredFriends.add(friend);
            }
        });
        return filteredFriends;
    }

    public void setModelForUpdatedProfile(User user, HttpServletRequest request, Principal currentUser, Model model, String image) {
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
            imageEntity.setIsForComment(Boolean.FALSE);
            serviceEntity.save(imageEntity);
            Image imageMiniEntity = new Image();
            imageMiniEntity.setIsMain(Boolean.FALSE);
            imageMiniEntity.setIsForComment(Boolean.TRUE);
            serviceEntity.save(imageMiniEntity);

            Image oldImage = user.getMainImageEntity();
            oldImage.setIsMain(Boolean.FALSE);
            serviceEntity.update(oldImage);
            Image oldMiniImage = user.getCommentImageEntity();
            oldMiniImage.setIsForComment(Boolean.FALSE);
            serviceEntity.update(oldMiniImage);

            try {

                File temp = new File("temp");
                FileUtils.writeByteArrayToFile(temp, array);
                ImageStoreService.saveMiniImage(temp, imageMiniEntity);
                serviceEntity.update(imageMiniEntity);
                ImageStoreService.saveImage(array, imageEntity);
                imageEntity.setMinVersionId(imageMiniEntity.getId());
                serviceEntity.update(imageEntity);
                temp.delete();
            } catch (Exception ex) {
                Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            user.addImage(imageEntity);
            user.addImage(imageMiniEntity);
            serviceEntity.update(user);
        } else if (StringUtils.isNumeric(image)) {
            Image oldImage = user.getMainImageEntity();
            oldImage.setIsMain(Boolean.FALSE);
            serviceEntity.update(oldImage);
            Image oldMiniImage = user.getCommentImageEntity();
            oldMiniImage.setIsForComment(Boolean.FALSE);
            serviceEntity.update(oldMiniImage);

            Image newMainImage = (Image) serviceEntity.findById(Integer.valueOf(image), Image.class);
            newMainImage.setIsMain(Boolean.TRUE);
            serviceEntity.update(newMainImage);
            if (newMainImage.getMinVersionId() != null) {
                Image newMiniMainImage = (Image) serviceEntity.findById(Integer.valueOf(newMainImage.getMinVersionId()), Image.class);
                newMiniMainImage.setIsForComment(Boolean.TRUE);
                serviceEntity.update(newMiniMainImage);
            }
        }
    }

    public void setModelForUsers(HttpServletRequest request, User currentUser, Model model) {
        List<User> users = serviceEntity.getAll(User.class);
        users.remove(currentUser);
        Collections.sort(users, User.COMPARE_BY_RATING);
        model.addAttribute("curUser", currentUser);
        model.addAttribute("users", users);
        model.addAttribute("showingAllUsers", true);
    }

    public void setModelForFriendRequest(HttpServletRequest request, User user, Model model, int friendId) {
        User friend = (User) serviceEntity.findById(friendId, User.class);
        if (!Objects.equals(user.getId(), friend.getId())) {
            List<Request> allRequests = serviceEntity.getAll(Request.class);

            for (Request req : allRequests) {
                if (req.getSender().equals(friend) && req.getReceiver().equals(user)) {
                    return;
                }
            }

            Request friendRequest = new Request();
            friendRequest.setDate(DateUtils.addHours(new Date(), 3));
            serviceEntity.save(friendRequest);
            friendRequest.setSender(user);
            friendRequest.setReceiver(friend);
            serviceEntity.update(friendRequest);

            interactiveUtil.interactiveFriendRequest(friend.getId(), friendRequest);
        }
    }

    public void setModelForEditProfile(int userId, HttpServletRequest request, Principal currentUser, Model model) {
        User user = (User) serviceEntity.findById(userId, User.class);
        model.addAttribute("userProfile", user);
        model.addAttribute("mapOfNetworks", usersDao.getListOfNetworks(userId));
    }

    public User removeFriendRequest(int requestId, User currentUser) {
        Request request = (Request) serviceEntity.findById(requestId, Request.class);
        currentUser.removeFriendRequest(request);
        User sender = request.getSender();
        request.removeSender(sender);
        serviceEntity.update(request);
        serviceEntity.delete(request);
        return sender;
    }

    public void addFriend(int requestId, User user) {
        User sender = removeFriendRequest(requestId, user);
        sender.addFriend(user);
        serviceEntity.update(sender);
        user.addFriend(sender);
        serviceEntity.update(user);
    }

}
