package challenge.webside.services;

import challenge.dbside.models.User;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.model.UserConnection;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public TwitterFriendsImportService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<User> importFriends(UserConnection connection) {
        List<User> friends = new ArrayList<>();
        Connection<Twitter> conn = connectionRepository.findPrimaryConnection(Twitter.class);
        Twitter twitter = conn != null
                ? conn.getApi()
                : new TwitterTemplate(
                        environment.getProperty("twitter.consumerKey"),
                        environment.getProperty("twitter.consumerSecret"));
        CursoredList<TwitterProfile> twitterFriends = twitter.friendOperations().getFriends();
        for (TwitterProfile profile : twitterFriends) {
            Integer id;
            try {
                id = jdbcTemplate.queryForObject("SELECT userentityid FROM userprofile p "
                        + "JOIN userconnection c ON p.userid = c.userid WHERE c.providerid = ? "
                        + "AND p.username = ?",
                        new Object[]{connection.getProviderId(), profile.getScreenName()}, Integer.class);
            } catch (EmptyResultDataAccessException e) {
                id = null;
            }
            if (id != null) {
                User user = (User) serviceEntity.findById(id, User.class);
                friends.add(user);
            }
        }
        return friends;
    }
}
