package challenge.webside.controllers;

import challenge.dbside.models.User;
import challenge.webside.controllers.util.ControllerUtil;
import challenge.webside.controllers.util.SocialControllerUtil;
import challenge.webside.controllers.util.UserUtil;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private SocialControllerUtil util;

    @Autowired
    private UserUtil userUtil;

    @RequestMapping("users")
    public String users(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        userUtil.setModelForUsers(request, user, model);
        return "users";
    }

    @RequestMapping("sendFriendRequest")
    public String sendFriendRequest(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int friendId) {
        util.setModel(request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        userUtil.setModelForFriendRequest(request, user, model, friendId);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }
    
    @RequestMapping("addFriend")
    public String addFriend(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int friendId) {
        User user = util.getSignedUpUser(request, currentUser);
        userUtil.addFriend(friendId, user);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }
    
    @RequestMapping("removeRequest")
    public String removeFriendRequest(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int friendId) {
        User user = util.getSignedUpUser(request, currentUser);
        userUtil.removeFriendRequest(friendId, user);
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "users/notFriends", method = GET)
    public String getNotFriends(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        userUtil.setModelForShowNotFriends(request, user, model);
        return "users";
    }

    @RequestMapping(value = "/friends", method = GET, produces = "text/plain;charset=UTF-8")
    public String selectUserFriends(HttpServletRequest request, Principal currentUser,
            Model model,
            @RequestParam("id") int userId) {
        util.setModel(request, currentUser, model);
        userUtil.setModelForShowFriends(request, currentUser, model, userId);
        return "listSomething";
    }
}
