package challenge.dbside.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Persister;
import org.hibernate.eav.EAVEntity;
import org.hibernate.eav.EAVGlobalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import challenge.dbside.eav.EAVPersister;
import challenge.dbside.eav.collection.EAVCollectionPersister;

@Entity
@EAVEntity
@Persister(impl = EAVPersister.class)
public class Request extends BaseEntity {

    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    public Request() {
        super(EAVGlobalContext.getTypeOfEntity(Request.class.getSimpleName().toLowerCase()).getId());

        subjects = new ArrayList();
        backSenders = new ArrayList();
        receivers = new ArrayList();
    }

    private String message;
    private String date;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<ChallengeDefinition> subjects;
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<User> backSenders;
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<User> receivers;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        try {
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            String ddt = this.date;
            Date result = df.parse(ddt);
            return result;
        } catch (Exception ex) {
            logger.error("null data" + ex.getMessage());
            return (new Date(0));
        }
    }

    public void setDate(Date date) {
        this.date = date.toString();
    }

    public List<ChallengeDefinition> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<ChallengeDefinition> subjects) {
        this.subjects = subjects;
    }

    public List<User> getBackSenders() {
        return backSenders;
    }

    public void setBackSenders(List<User> backSenders) {
        this.backSenders = backSenders;
    }

    public List<User> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<User> receivers) {
        this.receivers = receivers;
    }

    public void setSubject(ChallengeDefinition chal) {
        subjects = new ArrayList();
        subjects.add(chal);
    }

    public ChallengeDefinition getSubject() {
        return (subjects.size() == 0) ? null : subjects.get(0);
    }

    public void setSender(User sender) {
        backSenders = new ArrayList();
        backSenders.add(sender);
    }

    public User getSender() {
        return backSenders.get(0);
    }

    public void removeSubject(ChallengeDefinition chal) {
        subjects.remove(chal);
    }

    public void removeSender(User user) {
        backSenders.remove(user);
        user.removeFriendRequest(this);
    }

    public void removeReceiver(User receiver) {
        receivers.remove(receiver);
    }

    public void setReceiver(User receiver) {
        receivers = new ArrayList();
        receivers.add(receiver);
    }

    public User getReceiver() {
        return receivers.get(0);
    }

    public static final Comparator<Request> COMPARE_BY_DATE = (Request leftToCompare, Request rightToCompare)
            -> rightToCompare.getDate().compareTo(leftToCompare.getDate());
}
