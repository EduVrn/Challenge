package challenge.webside.controllers;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.authorization.UserActionsProvider;
import challenge.webside.controllers.util.ChallengeDefinitionUtil;
import challenge.webside.controllers.util.ControllerUtil;
import challenge.webside.controllers.util.SocialControllerUtil;
import challenge.webside.model.UserProfile;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChallengeDefinitionController {

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    @Autowired
    private UserActionsProvider actionsProvider;

    @Autowired
    private SocialControllerUtil util;

    @Autowired
    private ChallengeDefinitionUtil challengeDefUtil;

    @RequestMapping(value = "challenge/information", method = GET, produces = "text/plain;charset=UTF-8")
    public String show(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int id) {
        util.setModel(request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        challengeDefUtil.setModelForChallengeShow(id, request, user, model);
        return "chalShow";
    }

    @RequestMapping(value = "challenge/update", method = GET, produces = "text/plain;charset=UTF-8")
    public String updateChal(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int id) {
        util.setModel(request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        challengeDefUtil.setModelForChallengeShow(id, request, user, model);
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
        util.setModel(request, currentUser, model);
        challengeDefUtil.setModelForNewChallenge(request, currentUser, model);
        return "chalNewOrUpdate";
    }

    @RequestMapping(value = "challenge/information", method = POST, produces = "text/plain;charset=UTF-8")
    public String saveOrUpdateChallenge(HttpServletRequest request, Principal currentUser, Model model,
            @Valid @ModelAttribute("challenge") ChallengeDefinition challenge, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, @RequestParam("image") String img,
            @RequestParam(required = false, value = "image-name") String imgName,
            @RequestParam(value = "tags", required = false) List<Integer> selectedTags) {
        UserProfile userProfile = util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName());
        User user = util.getSignedUpUser(request, currentUser);
        if (bindingResult.hasFieldErrors()) {
            util.setModel(request, currentUser, model);
            challengeDefUtil.setModelForBadDateNewChal(challenge, request, currentUser, model, img, imgName, selectedTags);
            model.addAttribute(bindingResult.getAllErrors());
            return "chalNewOrUpdate";
        } else {
            util.setModel(request, currentUser, model);

            challengeDefUtil.setModelForNewOrUpdatedChalShow(challenge, request, user, userProfile, model, img, selectedTags);
            redirectAttributes.addAttribute("id", challenge.getId());
            return "redirect:information";
        }
    }

    @RequestMapping(value = "/acceptDefinition", method = GET, produces = "text/plain;charset=UTF-8")
    public String acceptChallengeDefinition(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId) {
        util.setModel(request, currentUser, model);
        User user = (User) serviceEntity.findById(util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        challengeDefUtil.setModelForAcceptChallengeDefinition(request, user, model, chalId);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/friendsForChallenge", method = GET)
    public String friendForChallenge(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id-checked") List<Integer> selectedFriendsIds,
            @RequestParam("chal-id") int chalId,
            @RequestParam("challenge-info") String chalInfo) {
        User currentDBUser = util.getSignedUpUser(request, currentUser);
        for (Integer id : selectedFriendsIds) {
            challengeDefUtil.throwChallenge(id, currentDBUser, chalId, chalInfo);
        }
        return ControllerUtil.getPreviousPageByRequest(request).orElse("main");
    }

    @RequestMapping(value = "/challengesForFriend", method = GET)
    public String challengeForFriend(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id-checked") List<Integer> selectedChallengesIds,
            @RequestParam("user-id") int friendId,
            @RequestParam("challenge-info") List<String> messages) {

        User currentDBUser = util.getSignedUpUser(request, currentUser);
        for (int i = 0; i < selectedChallengesIds.size(); i++) {
            String message = messages.get(i).trim().isEmpty() ? null : messages.get(i);
            challengeDefUtil.throwChallenge(friendId, currentDBUser, selectedChallengesIds.get(i), message);
        }
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/acceptors", method = GET, produces = "text/plain;charset=UTF-8")
    public String selectAcceptors(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id") int challengeId) {
        util.setModel(request, currentUser, model);
        challengeDefUtil.setModelForShowAcceptors(request, currentUser, model, challengeId);
        return "listSomething";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy HH:mm"), true);
        binder.registerCustomEditor(Date.class, editor);
    }
}
