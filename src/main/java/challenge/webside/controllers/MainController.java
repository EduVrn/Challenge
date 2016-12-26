package challenge.webside.controllers;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;
import challenge.webside.controllers.util.SocialControllerUtil;
import challenge.webside.model.UserConnection;
import challenge.webside.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class MainController {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private SocialControllerUtil util;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

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

    @RequestMapping(value = "challenge/update", method = GET, produces = "text/plain;charset=UTF-8")
    public String updateChal(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int id) {
        util.setModelForChallengeShow(id, request, currentUser, model);
        return "chalNewOrUpdate";
    }

    @RequestMapping(value = "challenge/new", produces = "text/plain;charset=UTF-8")
    public String newChal(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModelForNewChallenge(request, currentUser, model);
        return "chalNewOrUpdate";
    }

    @RequestMapping(value = "challenge/information", method = POST, produces = "text/plain;charset=UTF-8")
    public String saveOrUpdateChallenge(HttpServletRequest request, Principal currentUser, Model model, ChallengeDefinition challenge) {
        util.setModelForNewOrUpdatedChalShow(challenge, request, currentUser, model);
        return "chalShow";
    }

    @RequestMapping(value = "/profile", method = GET, produces = "text/plain;charset=UTF-8")
    public String showProfile(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int userId) {
        util.setProfileShow(userId, request, currentUser, model);
        return "profile";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        return "login";
    }
    
    @RequestMapping(value="/accept", method=GET, produces = "text/plain;charset=UTF-8")
    public String accept(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int chalId) {
        util.setModelForAcceptOrDeclineChallenge(request, currentUser, model, chalId, true);
        
        
        
        return "profile";
    }
    
    @RequestMapping(value="/decline", method=GET, produces = "text/plain;charset=UTF-8")
    public String decline(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int chalId) {
        util.setModelForAcceptOrDeclineChallenge(request, currentUser, model, chalId, false);
        return "profile";
    }
    
    @RequestMapping(value="/acceptDefinition", method=GET, produces = "text/plain;charset=UTF-8")
    public String acceptChallengeDefinition(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int chalId) {
        util.setModelForAcceptChallengeDefinition(request, currentUser, model, chalId);
        return "profile";
    }
   
    
    @RequestMapping(value="/user4Challenge", method=GET, produces = "text/plain;charset=UTF-8")
    public String selectUserCandidate4Challenge(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int chalId) {
    	util.setModelForThrowChallenge2User(request, currentUser, model, chalId);
    	
    	return "listSomething";
    }
    
    
    @RequestMapping(value="/challenge4User", method=GET, produces = "text/plain;charset=UTF-8")
    public String selectChallengeCandidate4User(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int userId) {
    	util.setModelForThrowUser2Challenge(request, currentUser, model, userId);    	
    	return "listSomething";
    }
    
    @RequestMapping(value="/throwChallengeFromChallengeList", method=GET, produces = "text/plain;charset=UTF-8")
    public String throwChallenge2UserFromChallengeList(HttpServletRequest request, Principal currentUser, Model model, 
    		@RequestParam("idParent") int challengeId, @RequestParam("id") int userId) {
    	
    	//TODO: need make any class with handler creation relation (friends, challenge2User, ...)
    	util.throwChallenge2User(userId, challengeId);
    	//TODO: get user id current seanse, this current id - is id user, which throw challenge
    	util.setProfileShow(userId, request, currentUser, model);
    	return "profile";
    }
    
    @RequestMapping(value="/throwChallengeFromUserList", method=GET, produces = "text/plain;charset=UTF-8")
    public String throwChallenge2UserFromUserList(HttpServletRequest request, Principal currentUser, Model model, 
    		@RequestParam("idParent") int userId, @RequestParam("id") int challengeId) {
    	
    	//TODO: need make any class with handler creation relation (friends, challenge2User, ...)
    	util.throwChallenge2User(userId, challengeId);
    	//TODO: get user id current seanse, this current id - is id user, which throw challenge
    	util.setProfileShow(userId, request, currentUser, model);
    	return "profile";
    }
    
    
    @RequestMapping(value="/friends", method=GET, produces = "text/plain;charset=UTF-8")
    public String selectUserFriends(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int userId) {    	
    	util.setModelForShowFriends(request, currentUser, model, userId);
    	
    	return "listSomething";
    }
    
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("yyyy/MM/dd HH:mm"), true);
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
    
    
    
    
}
