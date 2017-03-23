package challenge.webside.controllers;

import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.authorization.UserActionsProvider;
import challenge.webside.controllers.util.ChallengeDefinitionUtil;
import challenge.webside.controllers.util.ControllerUtil;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;
import challenge.webside.controllers.util.SocialControllerUtil;
import challenge.webside.controllers.util.TagsUtil;
import challenge.webside.controllers.util.UserUtil;
import challenge.webside.dao.UsersDao;
import challenge.webside.model.UserProfile;

import org.apache.commons.lang3.time.DateUtils;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    @Qualifier("EAVStorageServiceUser")
    private MediaService serviceEntity;

    @Autowired
    private UserActionsProvider actionsProvider;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private UserUtil userUtil;

    @Autowired
    private SessionRegistry sessionRegistry;
    
    @Autowired
    private ChallengeDefinitionUtil challengeDefUtil;
    
    @Autowired
    private TagsUtil tagsUtil;

    @RequestMapping("/")
    public String home(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        challengeDefUtil.setModelForMain(request, currentUser, model);
        return "main";
    }
    
    @RequestMapping("tags/find")
    public String findByTag(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int id) {
        util.setModel(request, currentUser, model);
        challengeDefUtil.setModelForMainFilteredByTag(request, currentUser, model, id);
        return "main";
    }

    @RequestMapping(value = "profile/edit", produces = "text/plain;charset=UTF-8")
    public String editProfile(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("id") int userId) {
        util.setModel(request, currentUser, model);
        userUtil.setModelForEditProfile(userId, request, currentUser, model);
        User user = util.getSignedUpUser(request, currentUser);
        User profileOwner = (User) serviceEntity.findById(userId, User.class);
        try {
            actionsProvider.canEditProfile(user, profileOwner);
            return "editProfile";
        } catch (AccessDeniedException ex) {
            model.addAttribute("timestamp", DateUtils.addHours(new Date(), 3));
            model.addAttribute("status", 403);
            model.addAttribute("error", "Access is denied");
            model.addAttribute("message", ex.getMessage());
            return "error";
        }
    }

    @RequestMapping(value = "changelang", produces = "text/plain;charset=UTF-8", method = GET)
    public String changeLang(HttpServletRequest request, Principal currentUser, Model model, @RequestParam("lang") String lang) {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.forLanguageTag(lang));
        return ControllerUtil.getPreviousPageByRequest(request).orElse("/");
    }

    @RequestMapping(value = "/profile", method = GET, produces = "text/plain;charset=UTF-8")
    public String showProfile(HttpServletRequest request, Principal currentUser, Model model,
            @RequestParam("id") int userId) {
        util.setModel(request, currentUser, model);
        UserProfile userProfile = util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName());
        User currentDBUser = util.getSignedUpUser(request, currentUser);
        userUtil.setProfileShow(userId, request, userProfile, currentDBUser, model);
        return "profile";
    }

    @RequestMapping(value = "/myprofile", method = GET, produces = "text/plain;charset=UTF-8")
    public String showSelfProfile(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        UserProfile userProfile = util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName());
        User currentDBUser = util.getSignedUpUser(request, currentUser);
        int userId = usersDao.getUserProfile(currentUser.getName()).getUserEntityId();
        userUtil.setProfileShow(userId, request, userProfile, currentDBUser, model);
        return "profile";
    }

    @RequestMapping(value = "profile", method = POST, produces = "text/plain;charset=UTF-8")
    public String updateProfile(HttpServletRequest request, Principal currentUser,
            Model model, User user,
            RedirectAttributes redirectAttributes,
            @RequestParam("image") String img) {
        util.setModel(request, currentUser, model);
        userUtil.setModelForUpdatedProfile(user, request, currentUser, model, img);
        UserProfile userProfile = util.getUserProfile(request.getSession(), currentUser == null ? null : currentUser.getName());
        User currentDBUser = util.getSignedUpUser(request, currentUser);
        userUtil.setProfileShow(user.getId(), request, userProfile, currentDBUser, model);
        redirectAttributes.addAttribute("id", user.getId());
        return "redirect:profile";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        return "login";
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
    
    @RequestMapping(value="tags", method=GET)
    public String tags(HttpServletRequest request, Principal currentUser, Model model) {
        util.setModel(request, currentUser, model);
        tagsUtil.setModelForTags(request, currentUser, model);
        return "tags";
    }

}
