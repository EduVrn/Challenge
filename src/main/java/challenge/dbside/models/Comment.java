package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void removeVoteFor(User voter) {
        getDataSource().getRel().remove(IdAttrGet.refVoteForComment(), voter.getDataSource());
    }

    public void removeVoteAgainst(User voter) {
        getDataSource().getRel().remove(IdAttrGet.refVoteAgainstComment(), voter.getDataSource());
    }
}
