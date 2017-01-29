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
import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

@Service("twitterFriendsService")
public class TwitterFriendsImportService implements FriendsImportService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private Environment environment;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    private List<User> friends;

    @Override
    public List<User> importFriends(UserConnection connection) {
        if (friends == null) {
            Connection<Twitter> conn = connectionRepository.findPrimaryConnection(Twitter.class);
            Twitter twitter = conn != null
                    ? conn.getApi()
                    : new TwitterTemplate(
                            environment.getProperty("twitter.consumerKey"),
                            environment.getProperty("twitter.consumerSecret"));
            CursoredList<TwitterProfile> twitterFriends = twitter.friendOperations().getFriends();
            friends = new ArrayList<>();
            for (TwitterProfile profile : twitterFriends) {
                User user = new User();
                user.setName(profile.getName());
                serviceEntity.save(user);
                Image profilePic = new Image();
                profilePic.setIsMain(Boolean.TRUE);
                serviceEntity.save(profilePic);
                try {
                    ImageStoreService.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic);
                    serviceEntity.update(profilePic);
                } catch (Exception ex) {
                    Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
                user.addImage(profilePic);
                serviceEntity.update(user);
                friends.add(user);
            }
        }
        return friends;
    }
}
