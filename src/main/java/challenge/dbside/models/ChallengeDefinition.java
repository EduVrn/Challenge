package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.ini.TypeEntity;
import challenge.dbside.models.status.ChallengeDefinitionStatus;
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

public class ChallengeDefinition extends BaseEntity implements Commentable {

    public ChallengeDefinition() {
        super(ChallengeDefinition.class.getSimpleName());
    }

    public ChallengeDefinition(DBSource dataSource) {
        super(dataSource);
    }

    public List<User> getAllAcceptors() {
        List<User> acceptors = new ArrayList<>();

        Set<DBSource> children = (Set<DBSource>) getDataSource().getChildren();
        children.forEach((chalInsDB) -> {
            if (chalInsDB.getEntityType() == TypeEntity.CHALLENGE_INSTANCE.getValue()) {
                acceptors.add(new ChallengeInstance(chalInsDB).getAcceptor());
            }
        });
        return acceptors;
    }

    public String getName() {
        return getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
        getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name);
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

    public User getCreator() {
        return new User(getDataSource().getParent());
    }

    public void setCreator(User creator) {
        getDataSource().setParent(creator.getDataSource());
    }

    public ChallengeDefinitionStatus getStatus() {
        return ChallengeDefinitionStatus.valueOf((getDataSource().getAttributes().get(IdAttrGet.IdChalDefStat())).getValue());
    }

    public void setStatus(ChallengeDefinitionStatus status) {
        getDataSource().getAttributes().get(IdAttrGet.IdChalDefStat()).setValue(status.name());
    }

    public void addChallengeInstance(ChallengeInstance chalIns) {
        getDataSource().getChildren().add(chalIns.getDataSource());
    }
    
    public Image getMainImageEntity() {
        Set<DBSource> children = (Set<DBSource>) getDataSource().getChildren();
        for (DBSource childDB : children) {
            if (childDB.getEntityType() == TypeEntity.IMAGE.getValue()) {
                Image currentImage = new Image(childDB);
                if (currentImage.isMain())
                    return currentImage;
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
        getDataSource().getChildren().add(image.getDataSource());
    }
}
