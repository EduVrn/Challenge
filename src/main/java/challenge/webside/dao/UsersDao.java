package challenge.webside.dao;

import challenge.dbside.ini.InitialLoader;
import challenge.dbside.models.Image;
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
import challenge.dbside.services.ini.MediaService;
import challenge.webside.imagesstorage.ImageStoreService;
import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class UsersDao {

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersDao(DataSource dataSource) {
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
                        rs.getString("username"),
                        rs.getInt("userEntityId"));
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

        profile.setUser(user);
        jdbcTemplate.update("INSERT into users(username,password,enabled) values(?,?,true)", userId, RandomStringUtils.randomAlphanumeric(8));
        jdbcTemplate.update("INSERT into authorities(username,authority) values(?,?)", userId, "USER");
        jdbcTemplate.update("INSERT into userprofile(userId, email, firstName, lastName, name, username, userEntityId) values(?,?,?,?,?,?,?)",
                userId,
                profile.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getName(),
                profile.getUsername(),
                profile.getUser().getId());
    }
}
