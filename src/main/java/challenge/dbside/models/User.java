package challenge.dbside.models;

import java.util.*;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.ini.TypeEntity;
import challenge.dbside.models.status.ChallengeStatus;
import challenge.webside.imagesstorage.ImageStoreService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

public class User extends BaseEntity implements Commentable {

    public User() {
        super(User.class.getSimpleName());
    }

    public User(DBSource dataSource) {
        super(dataSource);
    }

    public void setFriends(List<User> users) {
        getDataSource().getRel().remove(IdAttrGet.refFriend());
        users.forEach((user) -> {
            getDataSource().getRel().put(IdAttrGet.refFriend(), user.getDataSource());
        });
    }

    public List<User> getFriends() {
        List<User> friends = new ArrayList<>();
        List<DBSource> list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refFriend());
        if (list != null) {
            list.forEach((userDB) -> {
                friends.add(new User(userDB));
            });
        }
        return friends;
    }

    public void addFriend(User user) {
        getDataSource().getRel().put(IdAttrGet.refFriend(), user.getDataSource());
    }

    public String getName() {
        return getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
        getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name);
    }

    public void addChallenge(ChallengeDefinition chal) {
        getDataSource().addChild(chal.getDataSource());
    }

    public List<ChallengeDefinition> getChallenges() {
        List<ChallengeDefinition> createdChallenges = new ArrayList<>();

        Set<DBSource> set = (Set<DBSource>) getDataSource().getChildren();
        set.forEach((createdChalDB) -> {
            if (createdChalDB.getEntityType() == TypeEntity.CHALLENGE_DEFINITION.getValue()) {
                createdChallenges.add(new ChallengeDefinition(createdChalDB));
            }
        });
        return createdChallenges;
    }

    public void addAcceptedChallenge(ChallengeInstance chal) {
        getDataSource().getRel().put(IdAttrGet.refAcChalIns(), chal.getDataSource());
    }

    public List<ChallengeInstance> getAcceptedChallenges() {
        List<ChallengeInstance> accepted = new ArrayList<>();

        List<DBSource> list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refAcChalIns());
        if (list != null) {
            list.forEach((chalInsDB) -> {
                ChallengeInstance ch = new ChallengeInstance(chalInsDB);
                //TODO: optimize it (checked ChallengeStatus without creation new object)
                if (ch.getStatus() == ChallengeStatus.ACCEPTED) {
                    accepted.add(ch);
                }
            });
        }
        return accepted;
    }

    public List<ChallengeInstance> getChallengeRequests() {
        List<ChallengeInstance> requests = new ArrayList<>();

        List<DBSource> list = (List<DBSource>) getDataSource().getRel().get(IdAttrGet.refAcChalIns());
        if (list != null) {
            list.forEach((chalInsDB) -> {
                ChallengeInstance ch = new ChallengeInstance(chalInsDB);
                //TODO: optimize it (checked ChallengeStatus without creation new object)
                if (ch.getStatus() == ChallengeStatus.AWAITING) {
                    requests.add(ch);
                }
            });
        }
        return requests;
    }

    public void acceptChallenge(ChallengeInstance chal) {
        List<ChallengeInstance> requests = getChallengeRequests();

        if (requests.contains(chal)) {
            chal.setStatus(ChallengeStatus.ACCEPTED);
            chal.setAcceptor(this);
        }
    }

    public void declineChallenge(ChallengeInstance chal) {
        List<ChallengeInstance> requests = getChallengeRequests();
        if (requests.contains(chal)) {
            getDataSource().getRel().remove(IdAttrGet.refAcChalIns());
        }
    }

    @Override
    public String toString() {
        String entityInfo = super.toString();
        StringBuilder info = new StringBuilder();
        info.append(entityInfo);
        return info.toString();
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

    public void addImage(Image image) {
        getDataSource().addChild(image.getDataSource());
    }
}
