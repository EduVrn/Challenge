package challenge.webside.controllers;

import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStep;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.controllers.util.ChallengeInstanceUtil;
import challenge.webside.controllers.util.ControllerUtil;
import challenge.webside.controllers.util.SocialControllerUtil;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
public class ChallengeInstanceController {

    @Autowired
    private SocialControllerUtil util;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;
    @Autowired
    private ChallengeInstanceUtil challengeInsUtil;

    @RequestMapping(value = "challengeins/information", method = GET, produces = "text/plain;charset=UTF-8")
    public String showInstance(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int id) {
        util.setModel(request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        challengeInsUtil.setModelForChallengeInstanceShow(id, request, user, model);
        return "chalShow";
    }

    @RequestMapping(value = "challengeins/newstep", method = POST, produces = "text/plain;charset=UTF-8")
    public String newStep(HttpServletRequest request, Principal currentUser, Model model,
            @Valid @ModelAttribute("step") ChallengeStep step, BindingResult bindingResult, @RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(id, ChallengeInstance.class);
        if (bindingResult.hasFieldErrors()
                || challenge.getDate().before(step.getDate())
                || step.getDate().before(new Date())) {
            util.setModel(request, currentUser, model);
            User user = util.getSignedUpUser(request, currentUser);
            challengeInsUtil.setModelForBadStepChal(id, step, request, user, model);
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
            challengeInsUtil.setModelForNewStepForChallenge(request, currentUser, model, step, id);
            redirectAttributes.addAttribute("id", id);
            return "redirect:information";
        }
    }

    @RequestMapping(value = "challengeins/subscribe", method = GET, produces = "text/plain;charset=UTF-8")
    public String subscribeOnChallengeInstance(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId, RedirectAttributes redirectAttributes) {
        User user = (User) serviceEntity.findById(util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName()).getUserEntityId(), User.class);
        challengeInsUtil.setModelForInstanceSubscribe(request, user, model, chalId);
        redirectAttributes.addAttribute("id", chalId);
        return "redirect:information";
    }

    @RequestMapping(value = "challengeins/close", method = GET, produces = "text/plain;charset=UTF-8")
    public String closeChallenge(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId, RedirectAttributes redirectAttributes) {
        challengeInsUtil.setModelForCloseChallenge(request, currentUser, model, chalId);
        redirectAttributes.addAttribute("id", chalId);
        return "redirect:information";
    }

    @RequestMapping(value = "/accept", method = GET, produces = "text/plain;charset=UTF-8")
    public String accept(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId) {
        util.setModel(request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        challengeInsUtil.setModelForAcceptOrDeclineChallenge(request, user, model, chalId, true);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/decline", method = GET, produces = "text/plain;charset=UTF-8")
    public String decline(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id") int chalId) {
        util.setModel(request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        challengeInsUtil.setModelForAcceptOrDeclineChallenge(request, user, model, chalId, false);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy HH:mm"), true);
        binder.registerCustomEditor(Date.class, editor);
    }
}
