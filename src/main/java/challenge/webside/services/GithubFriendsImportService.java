package challenge.webside.services;

import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.model.UserConnection;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GithubFriendsImportService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

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
            for (GitHubUser profile : githubFriends) {
                Integer id;
                try {
                    id = jdbcTemplate.queryForObject("SELECT userentityid FROM userprofile p "
                            + "JOIN userconnection c ON p.userid = c.userid WHERE c.providerid = ? "
                            + "AND p.username = ?",
                            new Object[]{connection.getProviderId(), profile.getLogin()}, Integer.class);
                } catch (EmptyResultDataAccessException e) {
                    id = null;
                }
                if (id != null) {
                    User user = (User) serviceEntity.findById(id, User.class);
                    friends.add(user);
                }
            }
        }
        return friends;
    }
}
