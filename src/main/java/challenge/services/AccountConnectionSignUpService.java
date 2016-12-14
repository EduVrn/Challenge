package challenge.services;

import challenge.dao.UsersDao;
import challenge.model.UserProfile;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import java.util.UUID;

public class AccountConnectionSignUpService implements ConnectionSignUp {

  
    private final UsersDao usersDao;

    public AccountConnectionSignUpService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public String execute(Connection<?> connection) {
        org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();
        String userId = UUID.randomUUID().toString();
        // TODO: Or simply use: r = new Random(); r.nextInt(); ???
        usersDao.createUser(userId, new UserProfile(userId, profile));
        return userId;
    }
}