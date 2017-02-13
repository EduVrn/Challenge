package challenge.webside.controllers;


import challenge.webside.model.UserProfile;
import challenge.webside.model.ajax.AjaxResponseBody;
import challenge.webside.model.ajax.SearchCriteria;
import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStep;
import challenge.dbside.models.Comment;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.authorization.UserActionsProvider;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;
import challenge.webside.controllers.util.SocialControllerUtil;
import challenge.webside.dao.UsersDao;
import challenge.webside.interactive.InteractiveHandler;
import challenge.webside.interactive.InteractiveMessage;
import challenge.webside.interactive.InteractiveVote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class MainController {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private SocialControllerUtil util;

    @Autowired
    private UsersDao usersDao;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    @Autowired
    private UserActionsProvider actionsProvider;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private SessionRegistry sessionRegistry;
    
    /*@RequestMapping(value = "/")
    public String home(){
        return "home";
    }*/
    
    @Autowired
    private InteractiveHandler interactiveHandler;  
    
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
		User user = (User)serviceEntity.findById(userProf.getUserEntityId(), User.class);
		ChallengeDefinition chal = (ChallengeDefinition)serviceEntity.findById(message.getMainObjectId(), ChallengeDefinition.class);
		
		Comment comment = (Comment) serviceEntity.findById(message.getIdMessage(), Comment.class);
		boolean voteFor = message.isDown();
		if (voteFor) {
            if (comment.getVotesAgainst().contains(user)) {
                comment.removeVoteAgainst(user);
                //remove in web
                message.setStatus(1);
            }
            else {
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
            }
            else {
            	message.setStatus(-2);
            }
            comment.addVoteAgainst(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(-1);
            serviceEntity.update(author);
        }
		Set<String> candidates = interactiveHandler.getConnection4Object(message.getMainObjectId());
		for(String resp : candidates) {
			template.convertAndSend("/user/" + resp + "/exchange/like", message);
		}		
	}
    
    
    @MessageMapping("/interactive.comment.{username}")
	public void interactiveComment(@Payload InteractiveMessage message, @DestinationVariable("username") String username, Principal principal) {
		UserProfile userProf = usersDao.getUserProfile(principal.getName());
		User user = (User)serviceEntity.findById(userProf.getUserEntityId(), User.class); 
		ChallengeDefinition chal = (ChallengeDefinition)serviceEntity.findById(message.getMainObjectId(), ChallengeDefinition.class);
		
		Comment newComment = new Comment();
		newComment.setDate(new Date());
		newComment.setMessage(message.getMessageContent());
		newComment.setAuthor(user);
		serviceEntity.save(newComment);
		Integer id = message.getIdParent();
		if(id != null) {
			Comment parentComment = (Comment)serviceEntity.findById(id, Comment.class);
			parentComment.addComment(newComment);
			serviceEntity.update(parentComment);
		}	
		else {
			chal.addComment(newComment);
			serviceEntity.update(chal);
		}
		
		message.setUserName(user.getName());
		message.setMessageId(newComment.getId());
		message.setDate(newComment.getDate());
		message.setUserId(user.getId());
		
		Set<String> candidates = interactiveHandler.getConnection4Object(message.getMainObjectId());
		for(String resp : candidates) {
			template.convertAndSend("/user/" + resp + "/exchange/comment", message);
		}
	}
    
    
    
    @RequestMapping(value = "/newcomment", method = POST, produces = "text/plain;charset=UTF-8")
    public String newComment(@RequestParam("id") int id, HttpServletRequest request,
            Principal currentUser, Model model,
            @ModelAttribute Comment comment, RedirectAttributes redirectAttributes) {
    	
        util.addNewComment(id, request, currentUser, model, comment);
        redirectAttributes.addAttribute("id", id);
        return "redirect:challenge/information";
    }
    
    
    @RequestMapping("/")
    public String home(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModelForMain(request, currentUser, model);
        return "main";
    }

    @RequestMapping(value = "challenge/information", method = GET, produces = "text/plain;charset=UTF-8")
    public String show(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int id) {
        util.setModelForChallengeShow(id, request, currentUser, model);
        return "chalShow";
    }

    @RequestMapping(value = "challengeins/information", method = GET, produces = "text/plain;charset=UTF-8")
    public String showInstance(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int id) {
        util.setModelForChallengeInstanceShow(id, request, currentUser, model);
        return "chalShow";
    }

    @RequestMapping(value = "challenge/update", method = GET, produces = "text/plain;charset=UTF-8")
    public String updateChal(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int id) {
        util.setModelForChallengeShow(id, request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        ChallengeDefinition challengeToUpdate = (ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class);
        try {
            actionsProvider.canUpdateChallenge(user, challengeToUpdate);
            return "chalNewOrUpdate";
        } catch (AccessDeniedException ex) {
            model.addAttribute("timestamp", new Date());
            model.addAttribute("status", 403);
            model.addAttribute("error", "Access is denied");
            model.addAttribute("message", ex.getMessage());
            return "error";
        }
    }

    @RequestMapping(value = "challenge/new", produces = "text/plain;charset=UTF-8")
    public String newChal(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModelForNewChallenge(request, currentUser, model);
        return "chalNewOrUpdate";
    }

    @RequestMapping(value = "challengeins/newstep", method = POST, produces = "text/plain;charset=UTF-8")
    public String newStep(HttpServletRequest request, Principal currentUser, Model model,
            @Valid @ModelAttribute("step") ChallengeStep step, BindingResult bindingResult, @RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(id, ChallengeInstance.class);
        if (bindingResult.hasFieldErrors()
                || challenge.getDate().before(step.getDate())
                || step.getDate().before(new Date())) {
            util.setModelForBadStepChal(id, step, request, currentUser, model);
            model.addAttribute(bindingResult.getAllErrors());
            if (challenge.getDate().before(step.getDate())
                    || step.getDate().before(new Date())) {
                model.addAttribute("dateError", true);
            } else {
                model.addAttribute("dateError", false);
            }
            //#stepform
            return "chalShow";
        } else {
            util.setModelForNewStepForChallenge(request, currentUser, model, step, id);
            redirectAttributes.addAttribute("id", id);
            return "redirect:information";
        }
    }

    @RequestMapping(value = "profile/edit", produces = "text/plain;charset=UTF-8")
    public String editProfile(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int userId) {
        util.setModelForEditProfile(userId, request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        User profileOwner = (User) serviceEntity.findById(userId, User.class);
        try {
            actionsProvider.canEditProfile(user, profileOwner);
            return "editProfile";
        } catch (AccessDeniedException ex) {
            model.addAttribute("timestamp", new Date());
            model.addAttribute("status", 403);
            model.addAttribute("error", "Access is denied");
            model.addAttribute("message", ex.getMessage());
            return "error";
        }
    }

    protected Optional<String> getPreviousPageByRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> "redirect:" + requestUrl);
    }

    @RequestMapping(value = "changelang", produces = "text/plain;charset=UTF-8", method = GET)
    public String changeLang(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("lang") String lang) {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.forLanguageTag(lang));
        return getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "challenge/information", method = POST, produces = "text/plain;charset=UTF-8")
    public String saveOrUpdateChallenge(HttpServletRequest request, Principal currentUser, Model model, @Valid @ModelAttribute("challenge") ChallengeDefinition challenge, BindingResult bindingResult, RedirectAttributes redirectAttributes, @RequestParam("image") String img, @RequestParam(required = false, value = "image-name") String imgName) {
        if (bindingResult.hasFieldErrors()) {
            util.setModelForBadDateNewChal(challenge, request, currentUser, model, img, imgName);
            model.addAttribute(bindingResult.getAllErrors());
            return "chalNewOrUpdate";
        } else {
            util.setModelForNewOrUpdatedChalShow(challenge, request, currentUser, model, img);
            redirectAttributes.addAttribute("id", challenge.getId());
            return "redirect:information";
        }
    }

    @RequestMapping(value = "/profile", method = GET, produces = "text/plain;charset=UTF-8")
    public String showProfile(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int userId) {
        util.setProfileShow(userId, request, currentUser, model);
        return "profile";
    }

    @RequestMapping(value = "/myprofile", method = GET, produces = "text/plain;charset=UTF-8")
    public String showSelfProfile(HttpServletRequest request, Principal currentUser, Model model) {
        util.setProfileShow((usersDao.getUserProfile(currentUser.getName())).getUserEntityId(), request, currentUser, model);
        return "profile";
    }

    @RequestMapping(value = "profile", method = POST, produces = "text/plain;charset=UTF-8")
    public String updateProfile(HttpServletRequest request, Principal currentUser,
            Model model, User user,
            RedirectAttributes redirectAttributes,
            @RequestParam("image") String img) {
        util.setModelForUpdatedProfile(user, request, currentUser, model, img);
        redirectAttributes.addAttribute("id", user.getId());
        return "redirect:profile";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        return "login";
    }

    @RequestMapping(value = "/accept", method = GET, produces = "text/plain;charset=UTF-8")
    public String accept(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId) {
        util.setModelForAcceptOrDeclineChallenge(request, currentUser, model, chalId, true);
        return getPreviousPageByRequest(request).orElse("/");
    }



    @RequestMapping(value = "/newinscomment", method = POST, produces = "text/plain;charset=UTF-8")
    public String newInstanceComment(@RequestParam("id") int id, HttpServletRequest request,
            Principal currentUser, Model model,
            @ModelAttribute Comment comment, RedirectAttributes redirectAttributes) {
    	
        util.addNewInstanceComment(id, request, currentUser, model, comment);
        redirectAttributes.addAttribute("id", id);
        return "redirect:challengeins/information";
    }

    @RequestMapping(value = "/newreply", method = POST, produces = "text/plain;charset=UTF-8")
    public String newReply(@RequestParam("id") int id, HttpServletRequest request,
            Principal currentUser, Model model,
            @ModelAttribute Comment comment) {
        util.addNewReply(id, request, currentUser, model, comment);
        return getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/decline", method = GET, produces = "text/plain;charset=UTF-8")
    public String decline(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id") int chalId) {
        util.setModelForAcceptOrDeclineChallenge(request, currentUser, model, chalId, false);
        return getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/acceptDefinition", method = GET, produces = "text/plain;charset=UTF-8")
    public String acceptChallengeDefinition(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId) {
        util.setModelForAcceptChallengeDefinition(request, currentUser, model, chalId);
        return getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "challengeins/subscribe", method = GET, produces = "text/plain;charset=UTF-8")
    public String subscribeOnChallengeInstance(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId, RedirectAttributes redirectAttributes) {
        util.setModelForInstanceSubscribe(request, currentUser, model, chalId);
        redirectAttributes.addAttribute("id", chalId);
        return "redirect:information";
    }

    @RequestMapping(value = "challengeins/close", method = GET, produces = "text/plain;charset=UTF-8")
    public String closeChallenge(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId, RedirectAttributes redirectAttributes) {
        util.setModelForCloseChallenge(request, currentUser, model, chalId);
        redirectAttributes.addAttribute("id", chalId);
        return "redirect:information";
    }

    @RequestMapping(value = "challengeins/voteFor", method = GET, produces = "text/plain;charset=UTF-8")
    public String voteFor(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId, RedirectAttributes redirectAttributes) {
        util.setModelForVote(request, currentUser, model, chalId, true);
        redirectAttributes.addAttribute("id", chalId);
        return "redirect:information";
    }

    @RequestMapping(value = "challengeins/voteAgainst", method = GET, produces = "text/plain;charset=UTF-8")
    public String voteAgainst(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId, RedirectAttributes redirectAttributes) {
        util.setModelForVote(request, currentUser, model, chalId, false);
        redirectAttributes.addAttribute("id", chalId);
        return "redirect:information";
    }

    @RequestMapping(value = "comment/voteFor", method = GET, produces = "text/plain;charset=UTF-8")
    public String voteForComment(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int commId, RedirectAttributes redirectAttributes) {
        util.setModelForCommentVote(request, currentUser, model, commId, true);
        return getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "comment/voteAgainst", method = GET, produces = "text/plain;charset=UTF-8")
    public String voteAgainstComment(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int commId, RedirectAttributes redirectAttributes) {
        util.setModelForCommentVote(request, currentUser, model, commId, false);
        return getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/friends", method = GET, produces = "text/plain;charset=UTF-8")
    public String selectUserFriends(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id") int userId) {
        util.setModelForShowFriends(request, currentUser, model, userId);
        return "listSomething";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy HH:mm"), true);
        binder.registerCustomEditor(Date.class, editor);
    }

    @RequestMapping(value = "/updateStatus", method = POST)
    public String updateStatus(
            WebRequest webRequest,
            HttpServletRequest request,
            Principal currentUser,
            Model model,
            @RequestParam(value = "status", required = true) String status) {
        MultiValueMap<String, Connection<?>> cmap = connectionRepository.findAllConnections();
        Set<Map.Entry<String, List<Connection<?>>>> entries = cmap.entrySet();
        for (Map.Entry<String, List<Connection<?>>> entry : entries) {
            for (Connection<?> c : entry.getValue()) {
                c.updateStatus(status);
            }
        }

        return "index";
    }

    @RequestMapping(value = "/friendsForChallenge", method = GET)
    public String friendForChallenge(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id-checked") List<Integer> selectedFriendsIds,
            @RequestParam("chal-id") int chalId,
            @RequestParam("challenge-info") String chalInfo) {
        for (Integer id : selectedFriendsIds) {
            util.throwChallenge(id, chalId, chalInfo);
        }
        return getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/challengesForFriend", method = GET)
    public String challengeForFriend(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id-checked") List<Integer> selectedChallengesIds,
            @RequestParam("user-id") int friendId,
            @RequestParam("challenge-info") List<String> messages) {
        for (int i = 0; i < selectedChallengesIds.size(); i++) {
            util.throwChallenge(friendId, selectedChallengesIds.get(i), messages.get(i));
        }
        return getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/getFriends", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    AjaxResponseBody searchFriendsAjax(@RequestBody SearchCriteria search) {
        AjaxResponseBody result = new AjaxResponseBody();
        if (search != null) {
            List<User> users = util.filterUsers(search.getFilter(), search.getUserId());

            if (users.size() > 0) {
                Map<Integer, String> usersNames = new HashMap<>();
                users.forEach((user) -> {
                    usersNames.put(user.getId(), user.getName());
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
            List<ChallengeDefinition> challenges = util.filterChallenges(search.getFilter(), search.getUserId());

            if (challenges.size() > 0) {
                Map<Integer, String> chalNames = new HashMap<>();
                for (ChallengeDefinition challenge : challenges) {
                    chalNames.put(challenge.getId(), challenge.getName());
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
}
