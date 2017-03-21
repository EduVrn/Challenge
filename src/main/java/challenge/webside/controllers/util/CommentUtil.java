package challenge.webside.controllers.util;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Comment;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.model.UserProfile;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class CommentUtil {

    @Autowired
    @Qualifier("EAVStorageServiceUser")
    private MediaService serviceEntity;

    public void addNewReply(int id, HttpServletRequest request, UserProfile currentUser, Model model, Comment comment) {

        User curDBUser = (User) serviceEntity.findById(currentUser.getUserEntityId(), User.class);

        Comment parentComment = (Comment) serviceEntity.findById(id, Comment.class);

        comment.setParentComment(parentComment);
        comment.setDate(new Date());
        comment.setAuthor(curDBUser);
        serviceEntity.save(comment);
    }

    public void addNewComment(int chalId, HttpServletRequest request, UserProfile userProfile, Model model, Comment comment) {
        User curDBUser = (User) serviceEntity.
                findById(userProfile.getUserEntityId(), User.class);

        ChallengeDefinition currentChallenge = (ChallengeDefinition) serviceEntity.findById(chalId, ChallengeDefinition.class);
        comment.setDate(new Date());
        comment.setAuthor(curDBUser);
        serviceEntity.save(comment);

        currentChallenge.addComment(comment);
        serviceEntity.update(currentChallenge);
    }

    public void addNewInstanceComment(int chalId, HttpServletRequest request, UserProfile userProfile, Model model, Comment comment) {
        User curDBUser = (User) serviceEntity.
                findById(userProfile.getUserEntityId(), User.class);

        ChallengeInstance currentChallenge = (ChallengeInstance) serviceEntity.findById(chalId, ChallengeInstance.class);
        comment.setDate(new Date());
        comment.setAuthor(curDBUser);
        serviceEntity.save(comment);
        currentChallenge.addComment(comment);
        serviceEntity.update(currentChallenge);
    }

    private void sortCommentsByDate(List<Comment> comments) {
        Collections.sort(comments, Comment.COMPARE_BY_DATE);
        for (Comment comm : comments) {
            if (!comm.getComments().isEmpty()) {
                sortCommentsByDate(comm.getComments());
            }
        }

    }

    public void setModelForComments(List<Comment> comments, HttpServletRequest request, User currentUser, Model model) {
        if (currentUser != null) {
            Comment comment = new Comment();
            comment.setDate(new Date());
            comment.setAuthor(currentUser);
            model.addAttribute("comment", comment);
            Comment hiddenComment = new Comment();
            hiddenComment.setId(-1);
            hiddenComment.setMessage("hidden Comment");
            hiddenComment.setDate(new Date());
            hiddenComment.setAuthor(comment.getAuthor());

            model.addAttribute("hiddenComment", hiddenComment);
        }
        int commentsCount = 0;

        for (Comment comm : comments) {
            commentsCount++;
            commentsCount += comm.getSubCommentsCount();
        }
        sortCommentsByDate(comments);

        model.addAttribute("commentsCount", commentsCount);
        model.addAttribute("comments", comments);
    }

}
