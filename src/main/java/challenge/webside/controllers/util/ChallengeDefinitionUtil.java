package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStep;
import challenge.dbside.models.Image;
import challenge.dbside.models.Request;
import challenge.dbside.models.Tag;
import challenge.dbside.models.User;
import challenge.dbside.models.status.ChallengeDefinitionStatus;
import challenge.dbside.models.status.ChallengeInstanceStatus;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.authorization.UserActionsProvider;
import challenge.webside.authorization.thymeleaf.AuthorizationDialect;
import challenge.webside.dao.UsersDao;
import challenge.webside.imagesstorage.ImageStoreService;
import challenge.webside.model.UserProfile;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class ChallengeDefinitionUtil {

    @Autowired
    @Qualifier("EAVStorageServiceUser")
    private MediaService serviceEntity;

    @Autowired
    private AuthorizationDialect dialect;

    @Autowired
    private UserActionsProvider actionsProvider;

    @Autowired
    private CommentUtil commentUtil;

    @Autowired
    private InteractiveUtil interactiveUtil;

    @Autowired
    private UsersDao usersDao;

    public List<ChallengeDefinition> filterChallenges(String filter, int userId) {
        User user = (User) serviceEntity.findById(userId, User.class);

        List<ChallengeDefinition> challenges = user.getChallenges();
        List<ChallengeDefinition> filteredChallenges = new ArrayList<>();
        for (ChallengeDefinition challenge : challenges) {
            String name = challenge.getName();
            if (name.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredChallenges.add(challenge);
            }
        }
        return filteredChallenges;
    }

    public void throwChallenge(int userId, User currentUser, int challengeId, String message) {
        ChallengeDefinition chal = (ChallengeDefinition) serviceEntity.findById(challengeId, ChallengeDefinition.class);
        User user = (User) serviceEntity.findById(userId, User.class);

        Request challengeRequest = new Request();
        challengeRequest.setDate(DateUtils.addHours(new Date(), 3));
        if (message != null) {
            challengeRequest.setMessage(message);
        }
        serviceEntity.save(challengeRequest);
        challengeRequest.setSender(currentUser);
        challengeRequest.setReceiver(user);
        challengeRequest.setSubject(chal);
        serviceEntity.update(challengeRequest);

        interactiveUtil.interactiveThrowChallenge(userId, challengeRequest);
    }

    public void setModelForAcceptChallengeDefinition(HttpServletRequest request, User user, Model model, int chalId) {

        ChallengeDefinition chalToAccept = (ChallengeDefinition) serviceEntity.findById(chalId, ChallengeDefinition.class);
        Image img = new Image();
        img.setIsMain(true);
        img.setImageRef(chalToAccept.getMainImageEntity().getImageRef());
        serviceEntity.save(img);

        ChallengeInstance chalIns = new ChallengeInstance();
        chalIns.setName(chalToAccept.getName());
        chalIns.setDate(chalToAccept.getDate());
        chalIns.setDescription(chalToAccept.getDescription());
        chalIns.setChallengeRoot(chalToAccept);
        serviceEntity.save(chalIns);

        chalToAccept.addChallengeInstance(chalIns);
        serviceEntity.update(chalToAccept);

        chalIns.addImage(img);
        chalIns.setStatus(ChallengeInstanceStatus.ACCEPTED);
        chalIns.setDescription(chalToAccept.getDescription());
        chalIns.setAcceptor(user);

        ChallengeStep step = new ChallengeStep();
        step.setDate(chalIns.getDate());
        step.setMessage(chalIns.getDescription());
        step.setName(chalIns.getName());
        serviceEntity.save(step);

        chalIns.addStep(step);
        serviceEntity.update(chalIns);

        Image stepImg = new Image();
        stepImg.setIsMain(true);
        stepImg.setImageRef(chalToAccept.getMainImageEntity().getImageRef());
        serviceEntity.save(stepImg);
        step.addImage(stepImg);
        serviceEntity.update(step);
        dialect.setActions(actionsProvider.getActionsForProfile(user, user));
    }

    public void setModelForNewChallenge(HttpServletRequest request, Principal currentUser, Model model) {
        ChallengeDefinition chalDefNew = new ChallengeDefinition();
        chalDefNew.setRating(0);
        chalDefNew.setDate(DateUtils.addHours(new Date(), 3));
        model.addAttribute("challenge", chalDefNew);
        List<Tag> tags = serviceEntity.getAll(Tag.class);
        model.addAttribute("tags", tags);
        model.addAttribute("selectedTags", new ArrayList<>());
    }

    public void setModelForNewOrUpdatedChalShow(ChallengeDefinition challenge, HttpServletRequest request,
            User currentUser, UserProfile userProfile, Model model, String image, List<Integer> selectedTags) {

        User curDBUser = ((User) serviceEntity.findById(userProfile.getUserEntityId(), User.class));

        if (challenge.getId() != null) {
            ChallengeDefinition chalToUpdate = (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class);
            chalToUpdate.setDescription(challenge.getDescription());
            chalToUpdate.setName(challenge.getName());
            chalToUpdate.setDate(challenge.getDate());
            //TODO:check if creator
            if (Objects.equals(chalToUpdate.getCreator().getId(), curDBUser.getId())) {
                serviceEntity.update(chalToUpdate);
            }
            if (!image.isEmpty()) {
                Image oldImage = chalToUpdate.getMainImageEntity();
                if (oldImage.getId() != null) {
                    oldImage.setIsMain(Boolean.FALSE);
                    serviceEntity.update(oldImage);
                }
            }
            List<Tag> tags = chalToUpdate.getTags();
            chalToUpdate.removeAllTags();
            serviceEntity.update(chalToUpdate);
            for (Tag t : tags) {
                //usersDao.deleteRelation(chalToUpdate.getId(), t.getId(), 20);
                t.removeChallenge(chalToUpdate);
                serviceEntity.update(t);
            }
        } else {
            challenge.setStatus(ChallengeDefinitionStatus.CREATED);
            challenge.setCreator(curDBUser);
            challenge.setRating(0);
            serviceEntity.save(challenge);
        }

        challenge = (ChallengeDefinition) serviceEntity.findById(challenge.getId(), ChallengeDefinition.class);

        if (selectedTags != null) {
            for (Integer tagId : selectedTags) {
                Tag tag = (Tag) serviceEntity.findById(tagId, Tag.class);
                challenge.addTag(tag);
                serviceEntity.update(challenge);
            }
        }
        //need to update or create image
        if (image.isEmpty()) {
            if (challenge.getMainImageEntity().getId() == null) {
                Image img = new Image();
                img.setIsMain(Boolean.TRUE);
                img.setImageRef(ImageStoreService.getDEFAULT_IMAGE_ROUTE());
                serviceEntity.save(img);
                challenge.addImage(img);
                serviceEntity.update(challenge);
            }
        } else if (!image.isEmpty() && !StringUtils.isNumeric(image)) {
            String base64Image = image.split(",")[1];
            byte[] array = Base64.decodeBase64(base64Image);
            Image imageEntity = new Image();
            imageEntity.setIsMain(Boolean.TRUE);
            serviceEntity.save(imageEntity);
            try {
                ImageStoreService.saveImage(array, imageEntity);
                serviceEntity.update(imageEntity);
            } catch (Exception ex) {
                Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            challenge.addImage(imageEntity);
            serviceEntity.update(challenge);
        } else if (StringUtils.isNumeric(image)) {
            Image newMainImage = (Image) serviceEntity.findById(Integer.valueOf(image), Image.class);
            newMainImage.setIsMain(Boolean.TRUE);
            serviceEntity.update(newMainImage);
        }
        model.addAttribute("challenge", challenge);
        setModelForAcceptors(challenge, model);
        commentUtil.setModelForComments(challenge.getComments(), request, currentUser, model);
    }

    private void setModelForAcceptors(ChallengeDefinition challenge, Model model) {
        List acceptors = new ArrayList<>(new HashSet<>(challenge.getAllAcceptors()));
        int acceptorsOnPage = 6;
        model.addAttribute("listOfAcceptors", acceptors.size() > acceptorsOnPage ? acceptors.subList(0, acceptorsOnPage) : acceptors.subList(0, acceptors.size()));
        model.addAttribute("showAcceptorsExtendenceButton", acceptors.size() > acceptorsOnPage);
    }

    public void setModelForChallengeShow(int id, HttpServletRequest request, User currentUser, Model model) {
        ChallengeDefinition challenge = (ChallengeDefinition) serviceEntity.findById(id, ChallengeDefinition.class);
        dialect.setActions(actionsProvider.getActionsForChallengeDefinition(currentUser, challenge));
        model.addAttribute("challenge", challenge);
        setModelForAcceptors(challenge, model);
        if (currentUser != null) {
            model.addAttribute("friends", currentUser.getFriends());
        }
        model.addAttribute("userProfile", currentUser);
        model.addAttribute("tags", serviceEntity.getAll(Tag.class));
        model.addAttribute("selectedTags", challenge.getTags());
        commentUtil.setModelForComments(challenge.getComments(), request, currentUser, model);
    }

    public void setModelForShowAcceptors(HttpServletRequest request, Principal currentUser, Model model, int challengeId) {
        ChallengeDefinition challenge = (ChallengeDefinition) serviceEntity.findById(challengeId, ChallengeDefinition.class);
        model.addAttribute("listSomething", new ArrayList<>(new HashSet<>(challenge.getAllAcceptors())));
        model.addAttribute("idParent", challengeId);
        model.addAttribute("handler", "profile");
        model.addAttribute("type", "acceptors");
    }

    public void setModelForBadDateNewChal(ChallengeDefinition challenge, HttpServletRequest request,
            Principal currentUser, Model model, String image, String imageName, List<Integer> selectedTags) {
        model.addAttribute("challenge", challenge);
        model.addAttribute("image64", image);
        model.addAttribute("imageName", imageName);
        model.addAttribute("tags", serviceEntity.getAll(Tag.class));
        List<Tag> tags = new ArrayList<>();
        if (selectedTags != null) {
            for (Integer tagId : selectedTags) {
                tags.add((Tag) serviceEntity.findById(tagId, Tag.class));
            }
        }
        model.addAttribute("selectedTags", tags);
    }

    public void setModelForMain(HttpServletRequest request, Principal currentUser, Model model) {
        List<ChallengeDefinition> challenges = serviceEntity.getAll(ChallengeDefinition.class);
        Collections.sort(challenges, ChallengeDefinition.COMPARE_BY_RATING);
        ChallengeDefinition mainChallenge = challenges.remove(0);
        model.addAttribute("mainChallenge", mainChallenge);
        model.addAttribute("challenges", challenges);
        model.addAttribute("tag", "");
    }

    public void setModelForMainFilteredByTag(HttpServletRequest request, Principal currentUser, Model model, int tagId) {
        Tag tag = (Tag) serviceEntity.findById(tagId, Tag.class);
        List<ChallengeDefinition> challenges = tag.getChallenges();
        ChallengeDefinition mainChallenge = null;
        if (!challenges.isEmpty()) {
            Collections.sort(challenges, ChallengeDefinition.COMPARE_BY_RATING);
            mainChallenge = challenges.remove(0);
        }
        model.addAttribute("mainChallenge", mainChallenge);
        model.addAttribute("challenges", challenges);
        model.addAttribute("tag", tag.getName());
    }
}
