package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Comment;
import challenge.dbside.models.User;
import challenge.dbside.models.status.ChallengeInstanceStatus;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.dao.UsersDao;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class VoteUtil {

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;
    
    @Autowired
    private UsersDao usersDao;

    @Autowired
    private ChallengeInstanceUtil challengeInsUtil;

    private static final Logger logger = LoggerFactory.getLogger(VoteUtil.class);

    public void setModelForCommentVote(HttpServletRequest request, User user, Model model, int commentId, boolean voteFor) {
        Comment comment = (Comment) serviceEntity.findById(commentId, Comment.class);

        if (comment.getAuthor().equals(user)) {
            return;
        }
        
        if (voteFor) {
            if (comment.getVotesAgainst().contains(user)) {
                if (comment.rmVoteAgainst(user)) {
                    logger.info("remove comment VoteAgainst for comment " + comment.getId());
                    usersDao.deleteRelation(commentId, user.getId(), 19);
                }
            }
            comment.addVoteFor(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(1);
            serviceEntity.update(author);
        } else {
            if (comment.getVotesFor().contains(user)) {
                if (comment.rmVoteFor(user)) {
                    logger.info("remove comment VoteFor for comment " + comment.getId()
                            + " user: " + user.getId());
                    usersDao.deleteRelation(commentId, user.getId(), 18);
                }
            }
            serviceEntity.update(comment);
            
            comment.addVoteAgainst(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(-1);
            serviceEntity.update(author);
        }
    }

    public void setModelForVote(HttpServletRequest request, User user, Model model, int chalId, boolean voteFor) {
        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        
        challengeInsUtil.checkAndUpdateIfOutdated(challenge);
        if (challenge.getStatus() != ChallengeInstanceStatus.PUT_TO_VOTE) {
            return;
        }
        if (voteFor) {
            challenge.addVoteFor(user);
            serviceEntity.update(challenge);
            user.addVoteFor(challenge);
            serviceEntity.update(user);
        } else {
            challenge.addVoteAgainst(user);
            serviceEntity.update(challenge);
            user.addVoteAgainst(challenge);
            serviceEntity.update(user);
        }
    }

}
