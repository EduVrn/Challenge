package challenge.controllers;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;
import challenge.controllers.util.SocialControllerUtil;
import challenge.dao.DataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class MainController {

  
    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private DataDao dataDao;

    @Autowired
    private SocialControllerUtil util;

    @RequestMapping("/")
    public String home(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        return "main";
    }
     @RequestMapping(value="/main1", produces = "text/plain;charset=UTF-8")
    public String hom(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        return "main";
    }
    
    @RequestMapping(value = "/information", method = GET, produces = "text/plain;charset=UTF-8")
    public String show(HttpServletRequest request, Principal currentUser, Model model,@RequestParam("id") int id) {
        util.setChallengeShow(id,request, currentUser, model);
        return "chalShow";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        return "login";
    }

    @RequestMapping(value= "/update", method = POST)
    public String update(
        HttpServletRequest request,
        Principal currentUser,
        Model model,
        @RequestParam(value = "data", required = true) String data) {

         String userId = currentUser.getName();
        dataDao.setDate(userId, data);

        util.setModel(request, currentUser, model);
        return "index";
    }

    @RequestMapping(value= "/updateStatus", method = POST)
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
