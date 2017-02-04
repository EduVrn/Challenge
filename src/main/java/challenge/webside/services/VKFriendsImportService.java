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
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.api.VKontakteProfile;
import org.springframework.social.vkontakte.api.impl.VKontakteTemplate;
import org.springframework.stereotype.Service;

@Service("vkFriendsService")
public class VKFriendsImportService implements FriendsImportService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    private List<User> friends;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public VKFriendsImportService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<User> importFriends(UserConnection connection) {
        if (friends == null) {
            Connection<VKontakte> conn = connectionRepository.findPrimaryConnection(VKontakte.class);
            VKontakte vk = conn != null ? conn.getApi() : new VKontakteTemplate(connection.getAccessToken(), connection.getUserId());
            List<VKontakteProfile> vkFriends = vk.friendsOperations().get();
            friends = new ArrayList<>();
            for (VKontakteProfile profile : vkFriends) {
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
        }
        return friends;
    }
}
