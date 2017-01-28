package challenge.webside.services;

import challenge.dbside.ini.InitialLoader;
import challenge.dbside.models.Image;
import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.imagesstorage.ImageStoreService;
import challenge.webside.model.UserConnection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;

@Service("facebookFriendsService")
public class FacebookFriendsImportService implements FriendsImportService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    @Override
    public List<User> importFriends(UserConnection connection) {
        Connection<Facebook> conn = connectionRepository.findPrimaryConnection(Facebook.class);
        Facebook facebook = conn != null
                ? conn.getApi()
                : new FacebookTemplate(connection.getAccessToken());
        PagedList<Reference> facebookFriends = facebook.friendOperations().getFriends();
        List<User> friends = new ArrayList<>();
        facebookFriends.forEach((profile) -> {
            User user = new User();
            user.setName(profile.getName());
            Image profilePic = new Image();
            profilePic.setIsMain(Boolean.TRUE);
            serviceEntity.save(profilePic);
            try {
                ImageStoreService.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic);
                serviceEntity.update(profilePic);
            } catch (Exception ex) {
                Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            serviceEntity.save(user);
            user.addImage(profilePic);

            serviceEntity.update(user);
            friends.add(user);
        });
        return friends;
    }
}
