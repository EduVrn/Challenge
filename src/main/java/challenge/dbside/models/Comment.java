package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Comment extends BaseEntity implements Commentable { //TODO: add implements Commentable ???

    public Comment() {
        super(Comment.class.getSimpleName());
    }

    public Comment(DBSource dataSource) {
        super(dataSource);
    }

    public User getAuthor() {
        List<DBSource> list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refAutorCom());
        return new User(list.get(0));
    }

    public void setAuthor(User author) {
        getDataSource().getRel().put(IdAttrGet.refAutorCom(), author.getDataSource());
    }

    @NotNull
    @Size(min = 5, max = 250, message = "{error.comment.length}")
    public String getMessage() {
        return getDataSource().getAttributes().get(IdAttrGet.IdMessage()).getValue();
    }

    public void setMessage(String msg) {
        getDataSource().getAttributes().get(IdAttrGet.IdMessage()).setValue(msg);
    }

    public Date getDate() {
        return (Date) getDataSource().getAttributes().get(IdAttrGet.IdDate()).getDateValue();
    }

    public void setDate(Date date) {
        getDataSource().getAttributes().get(IdAttrGet.IdDate()).setDateValue(date);
    }

    public int getSubCommentsCount() {
        int result = 0;
        for (DBSource commentDB : getDataSource().getChildren()) {
            result++;
            //TODO: optimize it, create getAllChildCount in DBSource ???
            result += new Comment(commentDB).getSubCommentsCount();
        }
        return result;
    }

    public void setParentComment(Comment comment) {
        getDataSource().setParent(comment.getDataSource());
    }

    public Comment getParentComment() {
        if (getDataSource().getParent() != null) {
            return getDataSource().getParent().getEntityType() == this.getDataSource().getEntityType() ? new Comment(getDataSource().getParent()) : null;
        }
        return null;
    }

    public List<User> getVotesFor() {
        List<DBSource> list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refVoteForComment());
        List<User> voters = new ArrayList<>();
        if (list != null) {
            list.forEach((ds) -> {
                voters.add(new User(ds));
            });
        }
        return voters;
    }

    public void addVoteFor(User voter) {
        getDataSource().getRel().put(IdAttrGet.refVoteForComment(), voter.getDataSource());
    }

    public List<User> getVotesAgainst() {
        List<DBSource> list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refVoteAgainstComment());
        List<User> voters = new ArrayList<>();
        if (list != null) {
            list.forEach((ds) -> {
                voters.add(new User(ds));
            });
        }
        return voters;
    }

    public void addVoteAgainst(User voter) {
        getDataSource().getRel().put(IdAttrGet.refVoteAgainstComment(), voter.getDataSource());
    }

    public boolean rmVoteFor(User voter) {
        boolean b = getDataSource().getRel().removeMapping(IdAttrGet.refVoteForComment(), voter.getDataSource());
        return b;
    }

    public boolean rmVoteAgainst(User voter) {
        return getDataSource().getRel().removeMapping(IdAttrGet.refVoteAgainstComment(), voter.getDataSource());
    }

    public static final Comparator<Comment> COMPARE_BY_DATE = (Comment leftToCompare, Comment rightToCompare)
            -> rightToCompare.getDate().compareTo(leftToCompare.getDate());
}
