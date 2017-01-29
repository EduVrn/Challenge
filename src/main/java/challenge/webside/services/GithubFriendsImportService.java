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
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Service;

@Service("githubFriendsService")
public class GithubFriendsImportService implements FriendsImportService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    private List<User> friends;

    @Override
    public List<User> importFriends(UserConnection connection) {
        if (friends == null) {
            Connection<GitHub> conn = connectionRepository.findPrimaryConnection(GitHub.class);
            GitHub github = conn != null
                    ? conn.getApi()
                    : new GitHubTemplate(connection.getAccessToken());
            GitHubUserProfile userProfile = github.userOperations().getUserProfile();
            List<GitHubUser> githubFriends = github.userOperations().getFollowing(userProfile.getUsername());
            friends = new ArrayList<>();
            for (GitHubUser profile: githubFriends) {
                User user = new User();
                user.setName(profile.getLogin());
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
            }
        }
        return friends;
    }
}
