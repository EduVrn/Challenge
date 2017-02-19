package challenge.webside.controllers;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.Comment;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.controllers.util.ChallengeDefinitionUtil;
import challenge.webside.controllers.util.UserUtil;
import challenge.webside.dao.UsersDao;
import challenge.webside.interactive.InteractiveHandler;
import challenge.webside.interactive.InteractiveMessage;
import challenge.webside.interactive.InteractiveVote;
import challenge.webside.model.UserProfile;
import challenge.webside.model.ajax.AjaxResponseBody;
import challenge.webside.model.ajax.SearchCriteria;
import challenge.webside.model.ajax.NameAndImage;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class InteractionController {

    @Autowired
    private InteractiveHandler interactiveHandler;

    @Autowired
    private UsersDao usersDao;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private ChallengeDefinitionUtil challengeDefUtil;

    @Autowired
    private UserUtil userUtil;

    @MessageMapping("/interactive.open.{username}")
    public void onopen(@Payload InteractiveMessage message, @DestinationVariable("username") String username, Principal principal) {

        interactiveHandler.addConnection(message.getMainObjectId(), username);
    }

    @MessageMapping("/interactive.close.{username}")
    public void onclose(@Payload InteractiveMessage message, @DestinationVariable("username") String username, Principal principal) {
        interactiveHandler.rmConnection(message.getMainObjectId(), username);

    }

    @MessageMapping("/interactive.like.{username}")
    public void like(@Payload InteractiveVote message, @DestinationVariable("username") String username, Principal principal) {

        UserProfile userProf = usersDao.getUserProfile(principal.getName());
        User user = (User) serviceEntity.findById(userProf.getUserEntityId(), User.class);
        ChallengeDefinition chal = (ChallengeDefinition) serviceEntity.findById(message.getMainObjectId(), ChallengeDefinition.class);

        Comment comment = (Comment) serviceEntity.findById(message.getIdMessage(), Comment.class);
        boolean voteFor = message.isDown();
        if (voteFor) {
            if (comment.getVotesAgainst().contains(user)) {
                comment.removeVoteAgainst(user);
                //remove in web
                message.setStatus(1);
            } else {
                //don't remove in web
                message.setStatus(2);
            }
            comment.addVoteFor(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(1);
            serviceEntity.update(author);
        } else {
            if (comment.getVotesFor().contains(user)) {
                comment.removeVoteFor(user);
                message.setStatus(-1);
            } else {
                message.setStatus(-2);
            }
            comment.addVoteAgainst(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(-1);
            serviceEntity.update(author);
        }
        Set<String> candidates = interactiveHandler.getConnection4Object(message.getMainObjectId());
        for (String resp : candidates) {
            template.convertAndSend("/user/" + resp + "/exchange/like", message);
        }
    }

    @MessageMapping("/interactive.comment.{username}")
    public void interactiveComment(@Payload InteractiveMessage message, @DestinationVariable("username") String username, Principal principal) {
        UserProfile userProf = usersDao.getUserProfile(principal.getName());
        User user = (User) serviceEntity.findById(userProf.getUserEntityId(), User.class);
        ChallengeDefinition chal = (ChallengeDefinition) serviceEntity.findById(message.getMainObjectId(), ChallengeDefinition.class);

        Comment newComment = new Comment();
        newComment.setDate(new Date());
        newComment.setMessage(message.getMessageContent());
        newComment.setAuthor(user);
        serviceEntity.save(newComment);
        Integer id = message.getIdParent();
        if (id != null) {
            Comment parentComment = (Comment) serviceEntity.findById(id, Comment.class);
            parentComment.addComment(newComment);
            serviceEntity.update(parentComment);
        } else {
            chal.addComment(newComment);
            serviceEntity.update(chal);
        }

        message.setUserName(user.getName());
        message.setMessageId(newComment.getId());
        message.setDate(newComment.getDate());
        message.setUserId(user.getId());

        Set<String> candidates = interactiveHandler.getConnection4Object(message.getMainObjectId());
        for (String resp : candidates) {
            template.convertAndSend("/user/" + resp + "/exchange/comment", message);
        }
    }

    @RequestMapping(value = "/getFriends", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    AjaxResponseBody searchFriendsAjax(@RequestBody SearchCriteria search) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (search != null) {
            List<User> users = userUtil.filterFriends(search.getFilter(), search.getUserId());

            if (users.size() > 0) {
                Map<Integer, NameAndImage> usersNames = new HashMap<>();
                users.forEach((user) -> {
                    NameAndImage nameAndImage = new NameAndImage();
                    nameAndImage.setName(user.getName());
                    usersNames.put(user.getId(), nameAndImage);
                });
                result.setCode("200");
                result.setMsg("");
                result.setResult(usersNames);
            } else {
                result.setCode("204");
                result.setMsg("No users");
            }
        } else {
            result.setCode("400");
            result.setMsg("Search criteria is empty");
        }
        return result;
    }

    @RequestMapping(value = "/getChallenges", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    AjaxResponseBody searchChallengesViaAjax(@RequestBody SearchCriteria search) {

        AjaxResponseBody result = new AjaxResponseBody();

        if (search != null) {
            List<ChallengeDefinition> challenges = challengeDefUtil.filterChallenges(search.getFilter(), search.getUserId());

            if (challenges.size() > 0) {
                Map<Integer, NameAndImage> chalNames = new HashMap<>();
                for (ChallengeDefinition challenge : challenges) {
                    NameAndImage nameAndImage = new NameAndImage();
                    nameAndImage.setName(challenge.getName());
                    chalNames.put(challenge.getId(), nameAndImage);
                }
                result.setCode("200");
                result.setMsg("");
                result.setResult(chalNames);
            } else {
                result.setCode("204");
                result.setMsg("No challenges");
            }
        } else {
            result.setCode("400");
            result.setMsg("Search criteria is empty");
        }
        return result;
    }

    @RequestMapping(value = "/getUsers", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    AjaxResponseBody searchUsers(@RequestBody SearchCriteria search) {

        AjaxResponseBody result = new AjaxResponseBody();

        if (search != null) {
            List<User> filteredUsers = userUtil.filterUsers(search.getFilter());

            User currentUser = (User) serviceEntity.findById(search.getUserId(), User.class);
            if (filteredUsers.size() > 0) {
                Map<Integer, NameAndImage> usersAjax = new HashMap<>();
                for (User user : filteredUsers) {
                    NameAndImage nameAndImage = new NameAndImage();
                    nameAndImage.setName(user.getName());
                    nameAndImage.setImage(user.getMainImageEntity().getBase64());
                    nameAndImage.setIsFriend(currentUser.getFriends().contains(user));
                    usersAjax.put(user.getId(), nameAndImage);
                }
                result.setCode("200");
                result.setMsg("");
                result.setResult(usersAjax);
            } else {
                result.setCode("204");
                result.setMsg("No users");
            }
        } else {
            result.setCode("400");
            result.setMsg("Search criteria is empty");
        }
        return result;
    }
}
