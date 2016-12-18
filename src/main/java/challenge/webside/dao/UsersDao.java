package challenge.webside.dao;

import challenge.dbside.models.User;
import challenge.webside.model.UserConnection;
import challenge.webside.model.UserProfile;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import challenge.dbside.services.ini.MediaService;


@Repository
public class UsersDao {

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersDao(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public UserProfile getUserProfile(final String userId) {
        return jdbcTemplate.queryForObject("select * from UserProfile where userId = ?",
            new RowMapper<UserProfile>() {
                public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new UserProfile(
                    userId,
                    rs.getString("name"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("username"));
                }
            }, userId);
    }

    public UserConnection getUserConnection(final String userId) {
        return jdbcTemplate.queryForObject("select * from UserConnection where userId = ?",
            new RowMapper<UserConnection>() {
                public UserConnection mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new UserConnection(
                    userId,
                    rs.getString("providerId"),
                    rs.getString("providerUserId"),
                    rs.getInt("rank"),
                    rs.getString("displayName"),
                    rs.getString("profileUrl"),
                    rs.getString("imageUrl"),
                    rs.getString("accessToken"),
                    rs.getString("secret"),
                    rs.getString("refreshToken"),
                    rs.getLong("expireTime"));
                }
            }, userId);
    }

    public void createUser(String userId, UserProfile profile) {
        User user = new User();
        serviceEntity.save(user);
        profile.setUser(user);
        jdbcTemplate.update("INSERT into users(username,password,enabled) values(?,?,true)",userId, RandomStringUtils.randomAlphanumeric(8));
        jdbcTemplate.update("INSERT into authorities(username,authority) values(?,?)",userId,"USER");
        jdbcTemplate.update("INSERT into userprofile(userId, email, firstName, lastName, name, username, user_entity_id) values(?,?,?,?,?,?,?)",
            userId,
            profile.getEmail(),
            profile.getFirstName(),
            profile.getLastName(),
            profile.getName(),
            profile.getUsername(),
            profile.getUser().getId());
    }
}
