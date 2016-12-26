package challenge.webside.dao;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStatus;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    	
        ChallengeDefinition chalDef1 = new ChallengeDefinition();
        chalDef1.setName("Challenge Of SignUpedUser");
        chalDef1.setDescription("Description");
        chalDef1.setImageRef("race.jpg");
        chalDef1.setDate(new Date());
        serviceEntity.save(chalDef1);
         ChallengeDefinition chalDef2 = new ChallengeDefinition();
        chalDef2.setName("Challenge Of SignUpedUser");
        chalDef2.setDescription("Description");
        chalDef2.setImageRef("race.jpg");
        chalDef2.setDate(new Date());
        serviceEntity.save(chalDef2);
                 ChallengeDefinition chalDef3 = new ChallengeDefinition();
        chalDef3.setName("Challenge Of SignUpedUser");
        chalDef3.setDescription("Description");
        chalDef3.setImageRef("race.jpg");
        chalDef3.setDate(new Date());
        serviceEntity.save(chalDef3);
        ChallengeDefinition chalDef4 = new ChallengeDefinition();
        chalDef4.setName("Challenge Of SignUpedUser");
        chalDef4.setDescription("Description");
        chalDef4.setImageRef("race.jpg");
        chalDef4.setDate(new Date());
        serviceEntity.save(chalDef4);
        
        List<User> friends = serviceEntity.getAll(User.class);
        
        User user = new User();
        user.setName(profile.getName());
        user.setImageRef("AvaDefault.JPG");
        serviceEntity.save(user);

        user.addChallenge(chalDef1);
        user.addChallenge(chalDef2);
        user.addChallenge(chalDef3);
        user.addChallenge(chalDef4);
        serviceEntity.update(user);

        
        //
        
        ChallengeInstance chalInstance1 = new ChallengeInstance();
        chalInstance1.setName("Instance of SignUpedUser #1");
        chalInstance1.setParent(chalDef1);
        chalInstance1.setStatus(ChallengeStatus.AWAITING);
        serviceEntity.save(chalInstance1);
        ChallengeInstance chalUnstance2 = new ChallengeInstance();
        chalUnstance2.setName("Instance of SignUpedUser #");
        chalUnstance2.setParent(chalDef1);
        chalUnstance2.setStatus(ChallengeStatus.AWAITING);
        serviceEntity.save(chalUnstance2);

        Set set = new HashSet();
        set.add(chalInstance1);
        set.add(chalUnstance2);

        chalDef1.setChildren(set);
        serviceEntity.update(chalDef1);
        //  chalInstance1.setParent(chalDef1);
        //chalUnstance2.setParent(chalDef1);
        //serviceEntity.update(chalInstance1);
        //serviceEntity.update(chalUnstance2);
        user.addAcceptedChallenge(chalInstance1);
        user.addAcceptedChallenge(chalUnstance2);
        serviceEntity.update(user);

        user.setFriends(friends);
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
