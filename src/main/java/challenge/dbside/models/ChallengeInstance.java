package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.ini.TypeEntity;
import challenge.dbside.models.status.ChallengeStatus;
import challenge.webside.imagesstorage.ImageStoreService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

public class ChallengeInstance extends BaseEntity implements Commentable {

    public ChallengeInstance() {
        super(ChallengeInstance.class.getSimpleName());
    }

    public ChallengeInstance(DBSource dataSource) {
        super(dataSource);
    }

    public ChallengeInstance(ChallengeDefinition chalDef) {
        super(ChallengeInstance.class.getSimpleName());
        setName(chalDef.getName());
    }

    public ChallengeDefinition getChallengeRoot() {
        return new ChallengeDefinition(getDataSource().getParent());
    }

    public void setChallengeRoot(ChallengeDefinition rootChallenge) {
        getDataSource().setParent(rootChallenge.getDataSource());
    }

    public String getName() {
        return (String) getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
        getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name);
    }

    public User getAcceptor() {
        List list = (List<DBSource>) getDataSource().getBackRel().get(IdAttrGet.refAcChalIns());

        DBSource userDB = (DBSource) (list.get(0));
        return new User(userDB);
    }

    public void setAcceptor(User acceptor) {
        getDataSource().getBackRel().put(IdAttrGet.refAcChalIns(), acceptor.getDataSource());
    }

    public ChallengeStatus getStatus() {
        return ChallengeStatus.valueOf(getDataSource().getAttributes().get(IdAttrGet.IdChalStat()).getValue());
    }

    public void setStatus(ChallengeStatus status) {
        getDataSource().getAttributes().get(IdAttrGet.IdChalStat()).setValue(status.name());
    }

    public String getDescription() {
        return getDataSource().getAttributes().get(IdAttrGet.IdDescr()).getValue();
    }

    public void setDescription(String description) {
        getDataSource().getAttributes().get(IdAttrGet.IdDescr()).setValue(description);
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
            //new Date() == current date,
            //return (new Date());
        }
    }

    public void setDate(Date date) {
        getDataSource().getAttributes().get(IdAttrGet.IdDate()).setValue(date.toString());
    }

    public Image getMainImageEntity() {
        Set<DBSource> children = (Set<DBSource>) getDataSource().getChildren();
        for (DBSource childDB : children) {
            if (childDB.getEntityType() == TypeEntity.IMAGE.getValue()) {
                Image currentImage = new Image(childDB);
                if (currentImage.isMain()) {
                    return currentImage;
                }
            }
        }
        return new Image();
    }

    public List<Image> getImageEntities() {
        List<Image> images = new ArrayList<>();
        Set<DBSource> children = (Set<DBSource>) getDataSource().getChildren();
        children.forEach((childDB) -> {
            if (childDB.getEntityType() == TypeEntity.IMAGE.getValue()) {
                images.add(new Image(childDB));
            }
        });
        return images;
    }

    public void addImage(Image image) {
        getDataSource().addChild(image.getDataSource());
    }
}
