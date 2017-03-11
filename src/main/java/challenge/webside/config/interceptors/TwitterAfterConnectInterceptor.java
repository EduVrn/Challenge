package challenge.webside.config.interceptors;

import challenge.webside.dao.UsersDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

@Component
public class TwitterAfterConnectInterceptor implements ConnectInterceptor<Twitter> {

    @Autowired
    private UsersDao usersDao;

    @Override
    public void preConnect(ConnectionFactory<Twitter> provider, MultiValueMap<String, String> parameters, WebRequest request) {
    }

    @Override
    public void postConnect(Connection<Twitter> connection, WebRequest request) {
        usersDao.bindUser(connection, request);
    }
}
