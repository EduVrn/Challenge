package challenge.webside.controllers;

import challenge.dbside.models.Comment;
import challenge.webside.controllers.util.CommentUtil;
import challenge.webside.controllers.util.ControllerUtil;
import challenge.webside.controllers.util.SocialControllerUtil;
import challenge.webside.model.UserProfile;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommentController {

    @Autowired
    private SocialControllerUtil util;

    @Autowired
    private CommentUtil commentUtil;

    @RequestMapping(value = "/newcomment", method = POST, produces = "text/plain;charset=UTF-8")
    public String newComment(@RequestParam("id") int id, HttpServletRequest request,
            Principal currentUser, Model model,
            @Valid @ModelAttribute("comment") Comment comment, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasFieldErrors()) {
            util.setModel(request, currentUser, model);
            model.addAttribute(bindingResult.getAllErrors());
            redirectAttributes.addAttribute("id", id);
            return "redirect:challenge/information";
        } else {
            UserProfile userProfile = util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName());
            commentUtil.addNewComment(id, request, userProfile, model, comment);
            redirectAttributes.addAttribute("id", id);
            return "redirect:challenge/information";
        }
    }

    @RequestMapping(value = "/newinscomment", method = POST, produces = "text/plain;charset=UTF-8")
    public String newInstanceComment(@RequestParam("id") int id, HttpServletRequest request,
            Principal currentUser, Model model,
            @Valid @ModelAttribute("comment") Comment comment, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasFieldErrors()) {
            util.setModel(request, currentUser, model);
            model.addAttribute(bindingResult.getAllErrors());
            redirectAttributes.addAttribute("id", id);
            return "redirect:challenge/information";
        } else {
            UserProfile userProfile = util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName());
            commentUtil.addNewInstanceComment(id, request, userProfile, model, comment);
            redirectAttributes.addAttribute("id", id);
            return "redirect:challengeins/information";
        }
    }

    @RequestMapping(value = "/newreply", method = POST, produces = "text/plain;charset=UTF-8")
    public String newReply(@RequestParam("id") int id, HttpServletRequest request,
            Principal currentUser, Model model,
            @ModelAttribute Comment comment
    ) {
        util.setModel(request, currentUser, model);
        UserProfile userProfile = util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName());
        commentUtil.addNewReply(id, request, userProfile, model, comment);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }
}
