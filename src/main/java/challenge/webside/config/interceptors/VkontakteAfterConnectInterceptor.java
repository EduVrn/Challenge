package challenge.webside.config.interceptors;

import challenge.webside.dao.UsersDao;
import challenge.webside.model.UserProfile;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

@Component
public class VkontakteAfterConnectInterceptor implements ConnectInterceptor<VKontakte> {

    @Autowired
    private UsersDao usersDao;

    @Override
    public void preConnect(ConnectionFactory<VKontakte> provider, MultiValueMap<String, String> parameters, WebRequest request) {
    }

    @Override
    public void postConnect(Connection<VKontakte> connection, WebRequest request) {
        usersDao.bindUser(connection, request);
    }
}
