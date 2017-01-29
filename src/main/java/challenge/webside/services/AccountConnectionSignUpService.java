package challenge.webside.services;

import challenge.webside.dao.UsersDao;
import challenge.webside.model.UserProfile;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import java.util.UUID;

public class AccountConnectionSignUpService implements ConnectionSignUp {

  
    private final UsersDao usersDao;

    public AccountConnectionSignUpService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    @Override
    public String execute(Connection<?> connection) {
        org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();
        String userId = UUID.randomUUID().toString();
        usersDao.createUser(userId, new UserProfile(userId, profile));
        return userId;
    }
}