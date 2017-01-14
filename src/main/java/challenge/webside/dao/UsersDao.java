package challenge.webside.dao;

import challenge.dbside.ini.InitialLoader;
import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Image;
import challenge.dbside.models.User;
import challenge.dbside.models.status.ChallengeDefinitionStatus;
import challenge.dbside.models.status.ChallengeStatus;
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

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class UsersDao {

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    @Autowired
    private ImageStoreService imagesStorage;

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

        ChallengeDefinition chalDef1 = new ChallengeDefinition();
        chalDef1.setName("Challenge Of SignUpedUser");
        chalDef1.setDescription("Description");
        Image image1 = new Image();
        serviceEntity.save(image1);
        chalDef1.setDate(new Date());
        chalDef1.setStatus(ChallengeDefinitionStatus.CREATED);
        serviceEntity.save(chalDef1);
        chalDef1.addImage(image1);
        serviceEntity.update(chalDef1);
        try {
            imagesStorage.saveImage(new File("src/main/resources/static/images/race.jpg"), image1);
            serviceEntity.update(image1);
        } catch (Exception ex) {
            Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        ChallengeDefinition chalDef2 = new ChallengeDefinition();
        chalDef2.setName("Challenge Of SignUpedUser");
        chalDef2.setDescription("Description");
        Image image2 = new Image();
        serviceEntity.save(image2);
        chalDef2.setDate(new Date());
        chalDef2.setStatus(ChallengeDefinitionStatus.CREATED);
        serviceEntity.save(chalDef2);
        chalDef2.addImage(image2);
        serviceEntity.update(chalDef2);
        try {
            imagesStorage.saveImage(new File("src/main/resources/static/images/race.jpg"), image2);
            serviceEntity.update(image2);
        } catch (Exception ex) {
            Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        ChallengeDefinition chalDef3 = new ChallengeDefinition();
        chalDef3.setName("Challenge Of SignUpedUser");
        chalDef3.setDescription("Description");
        Image image3 = new Image();
        serviceEntity.save(image3);
        chalDef3.setDate(new Date());
        chalDef3.setStatus(ChallengeDefinitionStatus.CREATED);
        serviceEntity.save(chalDef3);
        chalDef3.addImage(image3);
        serviceEntity.update(chalDef3);
        try {
            imagesStorage.saveImage(new File("src/main/resources/static/images/race.jpg"), image3);
            serviceEntity.update(image3);
        } catch (Exception ex) {
            Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        ChallengeDefinition chalDef4 = new ChallengeDefinition();
        chalDef4.setName("Challenge Of SignUpedUser");
        chalDef4.setDescription("Description");
        Image image4 = new Image();
        serviceEntity.save(image4);
        chalDef4.setDate(new Date());
        chalDef4.setStatus(ChallengeDefinitionStatus.CREATED);
        serviceEntity.save(chalDef4);
        chalDef4.addImage(image4);
        serviceEntity.update(chalDef4);
        try {
            imagesStorage.saveImage(new File("src/main/resources/static/images/race.jpg"), image4);
            serviceEntity.update(image4);
        } catch (Exception ex) {
            Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<User> friendsCandidate = serviceEntity.getAll(User.class);

        User user = new User();
        user.setName(profile.getName());
        Image profilePic = new Image();
        serviceEntity.save(profilePic);
        try {
            imagesStorage.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic);
            serviceEntity.update(profilePic);
        } catch (Exception ex) {
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        serviceEntity.save(user);
        user.addImage(profilePic);
        serviceEntity.update(user);

        user.setFriends(friendsCandidate);
        user.addChallenge(chalDef1);
        user.addChallenge(chalDef2);
        user.addChallenge(chalDef3);
        user.addChallenge(chalDef4);

        serviceEntity.update(user);

        //
        ChallengeInstance chalInstance1 = new ChallengeInstance();
        chalInstance1.setName("Instance of SignUpedUser #1");

        chalInstance1.setChallengeRoot(chalDef1);
        chalInstance1.setStatus(ChallengeStatus.AWAITING);
        chalInstance1.setAcceptor(user);
        chalInstance1.setDescription("Description");
        Image imageForChalInstance1 = new Image();
        serviceEntity.save(imageForChalInstance1);
        try {
            imagesStorage.saveImage(new File("src/main/resources/static/images/race.jpg"), imageForChalInstance1);
            serviceEntity.update(imageForChalInstance1);
        } catch (Exception ex) {
            Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        chalInstance1.setDate(new Date());
        serviceEntity.save(chalInstance1);
        chalInstance1.addImage(imageForChalInstance1);
        serviceEntity.update(chalInstance1);        

        ChallengeInstance chalInstance2 = new ChallengeInstance();
        chalInstance2.setName("Instance of SignUpedUser #");
        chalInstance2.setDescription("Description");
        Image imageForChalInstance2 = new Image();
        serviceEntity.save(imageForChalInstance2);
        try {
            imagesStorage.saveImage(new File("src/main/resources/static/images/race.jpg"), imageForChalInstance2);
            serviceEntity.update(imageForChalInstance2);
        } catch (Exception ex) {
            Logger.getLogger(UsersDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        chalInstance2.setDate(new Date());
        chalInstance2.setAcceptor(user);
        chalInstance2.setChallengeRoot(chalDef1);
        chalInstance2.setStatus(ChallengeStatus.AWAITING);
        serviceEntity.save(chalInstance2);
        chalInstance2.addImage(imageForChalInstance2);
        serviceEntity.update(chalInstance2);
        

        user.addAcceptedChallenge(chalInstance1);
        user.addAcceptedChallenge(chalInstance2);
        serviceEntity.update(user);

        user.setFriends(friendsCandidate);

        serviceEntity.update(user);
        for (User addedUser : friendsCandidate) {
            addedUser.addFriend(user);
            serviceEntity.update(addedUser);
        }

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
