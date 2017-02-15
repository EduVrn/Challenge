package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Comment;
import challenge.dbside.models.User;
import challenge.dbside.models.status.ChallengeStatus;
import challenge.dbside.services.ini.MediaService;
import javax.servlet.http.HttpServletRequest;
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
    private ChallengeInstanceUtil challengeInsUtil;

    public void setModelForCommentVote(HttpServletRequest request, User user, Model model, int commentId, boolean voteFor) {
        Comment comment = (Comment) serviceEntity.findById(commentId, Comment.class);

        if (voteFor) {
            if (comment.getVotesAgainst().contains(user)) {
                comment.removeVoteAgainst(user);
            }
            comment.addVoteFor(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(1);
            serviceEntity.update(author);
        } else {
            if (comment.getVotesFor().contains(user)) {
                comment.removeVoteFor(user);
            }
            comment.addVoteAgainst(user);
            serviceEntity.update(comment);
            User author = comment.getAuthor();
            author.addRating(-1);
            serviceEntity.update(author);
        }
    }

    public void setModelForVote(HttpServletRequest request, User user, Model model, int chalId, boolean voteFor) {
        ChallengeInstance challenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        if (challenge.getVotesFor().contains(user) || challenge.getVotesAgainst().contains(user)) {
            return;
        }
        challengeInsUtil.checkAndUpdateIfOutdated(challenge);
        if (challenge.getStatus() != ChallengeStatus.PUT_TO_VOTE) {
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
