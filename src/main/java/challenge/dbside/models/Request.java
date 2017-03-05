package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Request extends BaseEntity {

    public Request() {
        super(Request.class.getSimpleName());
    }

    public Request(DBSource dataSource) {
        super(dataSource);
    }

    public void setSender(User user) {
        getDataSource().getRel().put(IdAttrGet.refRequestSender(), user.getDataSource());
    }

    public User getSender() {
        List list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refRequestSender());
        DBSource userDB = (DBSource) (list.get(0));
        return new User(userDB);
    }

    public void setReceiver(User user) {
        getDataSource().getRel().put(IdAttrGet.refRequestReceiver(), user.getDataSource());
    }

    public User getReceiver() {
        List list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refRequestReceiver());
        DBSource userDB = (DBSource) (list.get(0));
        return new User(userDB);
    }

    public void removeSender(User user) {
        getDataSource().getRel().removeMapping(IdAttrGet.refRequestSender(), user.getDataSource());
        user.getDataSource().getBackRel().removeMapping(IdAttrGet.refRequestSender(), getDataSource());
    }

    public void setSubject(ChallengeDefinition challenge) {
        getDataSource().getRel().put(IdAttrGet.refRequestSubject(), challenge.getDataSource());
    }

    public ChallengeDefinition getSubject() {
        List list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refRequestSubject());
        if (list != null) {
            DBSource challengeDB = (DBSource) (list.get(0));
            return new ChallengeDefinition(challengeDB);
        }
        return null;
    }

    public Date getDate() {
        try {
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            String ddt = getDataSource().getAttributes().get(IdAttrGet.IdDate()).getValue();
            Date result = df.parse(ddt);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return (new Date(0));
        }
    }

    public void setDate(Date date) {
        getDataSource().getAttributes().get(IdAttrGet.IdDate()).setValue(date.toString());
    }

    public String getMessage() {
        return getDataSource().getAttributes().get(IdAttrGet.IdMessage()).getValue();
    }

    public void setMessage(String message) {
        getDataSource().getAttributes().get(IdAttrGet.IdMessage()).setValue(message);
    }
    
    public static final Comparator<Request> COMPARE_BY_DATE = (Request leftToCompare, Request rightToCompare)
            -> rightToCompare.getDate().compareTo(leftToCompare.getDate());

}
