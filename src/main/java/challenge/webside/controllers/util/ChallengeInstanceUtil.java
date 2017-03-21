package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStep;
import challenge.dbside.models.Image;
import challenge.dbside.models.Request;
import challenge.dbside.models.User;
import challenge.dbside.models.status.ChallengeInstanceStatus;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.authorization.UserActionsProvider;
import challenge.webside.authorization.thymeleaf.AuthorizationDialect;
import challenge.webside.dao.UsersDao;
import challenge.webside.imagesstorage.ImageStoreService;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class ChallengeInstanceUtil {

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    @Autowired
    private AuthorizationDialect dialect;

    @Autowired
    private UserActionsProvider actionsProvider;

    @Autowired
    private CommentUtil commentUtil;

    public void setModelForNewStepForChallenge(HttpServletRequest request, Principal currentUser, Model model, ChallengeStep step, String image, int chalId) {
        ChallengeInstance currentChallenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        //need to update or create image
        Image img;
        if (image.isEmpty()) {
            img = new Image();
            img.setIsMain(Boolean.TRUE);
            img.setImageRef(ImageStoreService.getDEFAULT_IMAGE_ROUTE());
            serviceEntity.save(img);
        } else {
            String base64Image = image.split(",")[1];
            byte[] array = Base64.decodeBase64(base64Image);
            img = new Image();
            img.setIsMain(Boolean.TRUE);
            serviceEntity.save(img);
            try {
                ImageStoreService.saveImage(array, img);
                serviceEntity.update(img);
            } catch (Exception ex) {
                Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        step.addImage(img);
        serviceEntity.save(step);
        currentChallenge.addStep(step);
        serviceEntity.update(currentChallenge);
    }

    public void setModelForChallengeInstanceShow(int id, HttpServletRequest request, User user, Model model) {

        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(id, ChallengeInstance.class);
        checkAndUpdateIfOutdated(challenge);
        dialect.setActions(actionsProvider.getActionsForChallengeInstance(user, challenge));
        model.addAttribute("challenge", challenge);
        ChallengeStep step = new ChallengeStep();
        step.setDate(new Date());
        model.addAttribute("step", step);
        model.addAttribute("showStepForm", false);
        model.addAttribute("dateError", false);
        model.addAttribute("userProfile", user);
        List<ChallengeStep> listOfSteps = challenge.getSteps();
        Collections.sort(listOfSteps, ChallengeStep.COMPARE_BY_DATE);
        model.addAttribute("listOfSteps", listOfSteps);
        commentUtil.setModelForComments(challenge.getComments(), request, user, model);
    }

    public void setModelForInstanceSubscribe(HttpServletRequest request, User user, Model model, int chalId) {
        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        user.addSubscription(challenge);
        serviceEntity.update(user);
        challenge.addSubscriber(user);
        serviceEntity.update(challenge);
    }

    public void setModelForCloseChallenge(HttpServletRequest request, Principal currentUser, Model model, int chalId) {
        ChallengeInstance challengeToClose = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        challengeToClose.setStatus(ChallengeInstanceStatus.PUT_TO_VOTE);
        challengeToClose.setClosingDate(new Date());
        serviceEntity.update(challengeToClose);
    }

    public void setModelForAcceptOrDeclineChallenge(HttpServletRequest request, User user, Model model, int requestId, boolean accept) {
        Request challengeRequest = (Request) serviceEntity.findById(requestId, Request.class);
        if (accept) {

            ChallengeDefinition chal = challengeRequest.getSubject();

            Image img = new Image();
            img.setIsMain(true);
            img.setImageRef(chal.getMainImageEntity().getImageRef());
            serviceEntity.save(img);

            ChallengeInstance chalIns = new ChallengeInstance();
            chalIns.setName(chal.getName());
            chalIns.setDate(chal.getDate());
            chalIns.setChallengeRoot(chal);
            chalIns.addImage(img);
            chalIns.setStatus(ChallengeInstanceStatus.ACCEPTED);
            chalIns.setDescription(chal.getDescription());
            chalIns.setAcceptor(user);

            ChallengeStep step = new ChallengeStep();
            step.setDate(chalIns.getDate());
            step.setMessage(chalIns.getDescription());
            step.setName(chalIns.getName());
            Image stepImg = new Image();
            stepImg.setIsMain(true);
            stepImg.setImageRef(chal.getMainImageEntity().getImageRef());
            serviceEntity.save(stepImg);
            step.addImage(stepImg);
            serviceEntity.save(step);

            chalIns.addStep(step);
            serviceEntity.save(chalIns);

            chal.addChallengeInstance(chalIns);
            serviceEntity.update(chal);
            user.acceptChallenge(chalIns);
            serviceEntity.update(user);
        }
        serviceEntity.delete(challengeRequest);
        dialect.setActions(actionsProvider.getActionsForProfile(user, user));
    }

    public void setModelForBadStepChal(int chalId, ChallengeStep step, HttpServletRequest request, User user, Model model, String image, String imageName) {

        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        checkAndUpdateIfOutdated(challenge);
        dialect.setActions(actionsProvider.getActionsForChallengeInstance(user, challenge));
        model.addAttribute("challenge", challenge);
        model.addAttribute("userProfile", user);
        List<ChallengeStep> listOfSteps = challenge.getSteps();
        Collections.sort(listOfSteps, ChallengeStep.COMPARE_BY_DATE);
        model.addAttribute("listOfSteps", listOfSteps);
        commentUtil.setModelForComments(challenge.getComments(), request, user, model);
        model.addAttribute("step", step);
        model.addAttribute("showStepForm", true);
        model.addAttribute("image64", image);
        model.addAttribute("imageName", imageName);
    }

    public void checkAndUpdateIfOutdated(ChallengeInstance challenge) {
        Date closingDate = challenge.getClosingDate();
        Date currentDate = new Date();
        long diffInMillies = currentDate.getTime() - closingDate.getTime();
        long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
        synchronized (challenge) {
            if (diff >= 5 && challenge.getStatus() == ChallengeInstanceStatus.PUT_TO_VOTE) {
                int votesFor = challenge.getVotesFor().size();
                int votesAgainst = challenge.getVotesAgainst().size();
                challenge.setStatus(votesFor > votesAgainst ? ChallengeInstanceStatus.COMPLETED : ChallengeInstanceStatus.FAILED);
                serviceEntity.update(challenge);
                User authorUser = challenge.getAcceptor();
                authorUser.addRating(votesFor - votesAgainst);
                serviceEntity.update(authorUser);
                ChallengeDefinition challengeDef = challenge.getChallengeRoot();
                challengeDef.addRating(votesFor - votesAgainst);
                serviceEntity.update(challengeDef);
            } else if (currentDate.compareTo(challenge.getDate()) >= 0 && challenge.getStatus() == ChallengeInstanceStatus.ACCEPTED) {
                challenge.setStatus(ChallengeInstanceStatus.PUT_TO_VOTE);
                challenge.setClosingDate(new Date());
                serviceEntity.update(challenge);
            }
        }
    }

}
