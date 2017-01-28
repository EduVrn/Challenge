package challenge.webside.services;

import challenge.dbside.models.User;
import challenge.webside.model.UserConnection;
import java.util.List;

public interface FriendsImportService {
    public List<User> importFriends(UserConnection connection);
}
