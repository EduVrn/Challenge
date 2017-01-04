package challenge.dbside.models;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.ini.TypeOfAttribute;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "entities")
public class Comment extends BaseEntity {

    public Comment() {
        super(Comment.class.getSimpleName());
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "relationship",
            joinColumns = {
                @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")})
    private User author;

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return (String) this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("message").getId()).getValue();
    }

    public void setMessage(String msg) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("message").getId()).setValue(msg);
    }

    public Date getDate() {
        return (Date) this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("date").getId()).getDateValue();
    }

    public void setDate(Date date) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("date").getId()).setDateValue(date);
    }

    public int getSubCommentsCount() {
        int result = 0;
        for (BaseEntity child : getChildren()) {
            if (child instanceof Comment) {
                result++;
                if (!((Comment) child).getChildren().isEmpty()) {
                    result += ((Comment) child).getSubCommentsCount();
                }
            }
        }
        return result;
    }

    public List<Comment> getComments() {
        List<Comment> comments = new ArrayList<>();
        this.getChildren().forEach((child) -> {
            if (child instanceof Comment) {
                comments.add((Comment) child);
            }
        });
        return comments;
    }
}
