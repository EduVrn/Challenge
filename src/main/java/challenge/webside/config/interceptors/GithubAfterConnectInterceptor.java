package challenge.webside.config.interceptors;

import challenge.webside.dao.UsersDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

@Component
public class GithubAfterConnectInterceptor implements ConnectInterceptor<GitHub> {

    @Autowired
    private UsersDao usersDao;

    @Override
    public void preConnect(ConnectionFactory<GitHub> provider, MultiValueMap<String, String> parameters, WebRequest request) {
    }

    @Override
    public void postConnect(Connection<GitHub> connection, WebRequest request) {
      usersDao.bindUser(connection, request);  }
}
