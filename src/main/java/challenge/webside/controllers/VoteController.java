package challenge.webside.controllers;

import challenge.dbside.models.User;
import challenge.webside.controllers.util.ControllerUtil;
import challenge.webside.controllers.util.SocialControllerUtil;
import challenge.webside.controllers.util.VoteUtil;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VoteController {

    @Autowired
    private SocialControllerUtil util;

    @Autowired
    private VoteUtil voteUtil;

    @RequestMapping(value = "challengeins/voteFor", method = GET, produces = "text/plain;charset=UTF-8")
    public String voteFor(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId, RedirectAttributes redirectAttributes) {
        User user = util.getSignedUpUser(request, currentUser);
        voteUtil.setModelForVote(request, user, model, chalId, true);
        redirectAttributes.addAttribute("id", chalId);
        return "redirect:information";
    }

    @RequestMapping(value = "challengeins/voteAgainst", method = GET, produces = "text/plain;charset=UTF-8")
    public String voteAgainst(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int chalId, RedirectAttributes redirectAttributes) {
        User user = util.getSignedUpUser(request, currentUser);
        voteUtil.setModelForVote(request, user, model, chalId, false);
        redirectAttributes.addAttribute("id", chalId);
        return "redirect:information";
    }

    @RequestMapping(value = "comment/voteFor", method = GET, produces = "text/plain;charset=UTF-8")
    public String voteForComment(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int commId, RedirectAttributes redirectAttributes) {
        User user = util.getSignedUpUser(request, currentUser);
        voteUtil.setModelForCommentVote(request, user, model, commId, true);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "comment/voteAgainst", method = GET, produces = "text/plain;charset=UTF-8")
    public String voteAgainstComment(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int commId, RedirectAttributes redirectAttributes) {
        User user = util.getSignedUpUser(request, currentUser);
        voteUtil.setModelForCommentVote(request, user, model, commId, false);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }
}
