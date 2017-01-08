package challenge.dbside.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import challenge.dbside.models.dbentity.DBSource;

public interface Commentable {

	public DBSource getDataSource();

	default public List<Comment> getComments() {
		List<Comment> comments = new ArrayList<>();
		((Set<DBSource>)getDataSource().getChildren()).forEach((commentDB)-> {
			comments.add(new Comment(commentDB));
		});
		return comments;
	}

	default public void setComments(List<Comment> comments) {
		Set<DBSource> setChildren = new HashSet();
		comments.forEach((comment)->{
			setChildren.add(comment.getDataSource());
		});
		getDataSource().setChildren(setChildren);
	}

	default public void addComment(Comment comment) {
		getDataSource().addChidlren(comment.getDataSource());
	}
}
