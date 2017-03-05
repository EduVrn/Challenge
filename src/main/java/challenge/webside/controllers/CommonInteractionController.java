package challenge.webside.controllers;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Comment;
import challenge.dbside.models.Request;
import challenge.dbside.models.Tag;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.controllers.util.ChallengeDefinitionUtil;
import challenge.webside.controllers.util.TagsUtil;
import challenge.webside.controllers.util.UserUtil;
import challenge.webside.dao.UsersDao;
import challenge.webside.interactive.InteractiveRepository;
import challenge.webside.interactive.model.InteractiveComment;
import challenge.webside.interactive.model.InteractiveVote;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CommonInteractionController {

    @Autowired
    private InteractiveRepository commonInteractiveHandler;

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
    
    @Autowired
    private TagsUtil tagsUtil;

	private static final Logger logger =
			LoggerFactory.getLogger(CommonInteractionController.class);
    
    
    @MessageMapping("/interactive.like.{username}")
    public void like(@Payload InteractiveVote message, @DestinationVariable("username") String username, Principal principal) {
        UserProfile userProf = usersDao.getUserProfile(principal.getName());
        User user = (User) serviceEntity.findById(userProf.getUserEntityId(), User.class);
        
        //typeMain
        //ChallengeDefinition chal = (ChallengeDefinition) serviceEntity.findById(message.getMainObjectId(), ChallengeDefinition.class);
        
        Comment comment = (Comment) serviceEntity.findById(message.getIdOwner(), Comment.class);
        
        boolean voteFor = message.getChangeVote() == 1 ? true : false;
        List<User> lVUp   = comment.getVotesFor();
        List<User> lVDown = comment.getVotesAgainst();
        
        if (voteFor) {
            if (comment.getVotesAgainst().contains(user)) {
                comment.rmVoteAgainst(user);
                //remove in web
                message.setChangeVote(2);
            } else {
                //don't remove in web
                message.setChangeVote(1);
            }
            comment.addVoteFor(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(1);
            serviceEntity.update(author);
        } else {
            if (comment.getVotesFor().contains(user)) {
                comment.rmVoteFor(user);
                message.setChangeVote(-2);
            } else {
                message.setChangeVote(-1);
            }
            comment.addVoteAgainst(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(-1);
            serviceEntity.update(author);
        }
        Set<String> candidates = commonInteractiveHandler.getCommonConnection4Object(message.getMainObjectId());
        for (String resp : candidates) {
            template.convertAndSend("/user/" + resp + "/exchange/like", message);
        }
    }

    @MessageMapping("/interactive.comment.{username}")
    public void interactiveComment(@Payload InteractiveComment message, @DestinationVariable("username") String username, Principal principal) {
    	Integer mainObjectId = message.getMainObjectId();
		UserProfile userProf = usersDao.getUserProfile(principal.getName());
		User user = (User) serviceEntity.findById(userProf.getUserEntityId(), User.class);
		
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
			if(message.getTypeMain().equals("ChallengeDefinitionType")) {
				ChallengeDefinition chal = (ChallengeDefinition) serviceEntity.findById(mainObjectId, ChallengeDefinition.class);
				chal.addComment(newComment);
				serviceEntity.update(chal);
			}
			else if(message.getTypeMain().equals("ChallengeInstanceType")) {
				ChallengeInstance chal = (ChallengeInstance) serviceEntity.findById(mainObjectId, ChallengeInstance.class);
				chal.addComment(newComment);
				serviceEntity.update(chal);
			}
			else {
				logger.error("unknown typeMain: " + message.getTypeMain());
				return;
			}
		}
		
		message.setStatus("SUCCESS");
		message.setUserName(user.getName());
		message.setMessageId(newComment.getId());
		message.setDate(newComment.getDate());
		message.setUserId(user.getId());
				
		Set<String> candidates = commonInteractiveHandler.getCommonConnection4Object(mainObjectId);
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
                    nameAndImage.setIsSubscriber(currentUser.getIncomingFriendRequestSenders().contains(user));
                    nameAndImage.setIsSubscripted(user.getIncomingFriendRequestSenders().contains(currentUser));
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
    
    @RequestMapping(value = "/getTags", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    AjaxResponseBody searchTags(@RequestBody SearchCriteria search) {

        AjaxResponseBody result = new AjaxResponseBody();

        if (search != null) {
            List<Tag> filteredTags = tagsUtil.filterTags(search.getFilter());

            if (filteredTags.size() > 0) {
                Map<Integer, NameAndImage> tagsAjax = new HashMap<>();
                for (Tag tag : filteredTags) {
                    NameAndImage nameAndImage = new NameAndImage();
                    nameAndImage.setName(tag.getName());
                    nameAndImage.setImage(String.valueOf(tag.getChallenges().size()));
                    tagsAjax.put(tag.getId(), nameAndImage);
                }
                result.setCode("200");
                result.setMsg("");
                result.setResult(tagsAjax);
            } else {
                result.setCode("204");
                result.setMsg("No tags");
            }
        } else {
            result.setCode("400");
            result.setMsg("Search criteria is empty");
        }
        return result;
    }
}
