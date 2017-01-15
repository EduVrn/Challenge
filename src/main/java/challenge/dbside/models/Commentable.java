package challenge.dbside.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.ini.TypeEntity;

public interface Commentable {

    public DBSource getDataSource();

    default public List<Comment> getComments() {
        List<Comment> comments = new ArrayList<>();
        ((Set<DBSource>) getDataSource().getChildren()).forEach((comCandidateDB) -> {
            if (comCandidateDB.getEntityType() == TypeEntity.COMMENT.getValue()) {
                comments.add(new Comment(comCandidateDB));
            }
        });
        return comments;
    }

    default public void setComments(Set<Comment> comments) {
        Set<DBSource> children = new HashSet<>();
        comments.forEach((comment) -> {
            children.add(comment.getDataSource());
        });
        getDataSource().setChildren(children);
    }

    default public void addComment(Comment comment) {
        getDataSource().addChild(comment.getDataSource());
    }
}
