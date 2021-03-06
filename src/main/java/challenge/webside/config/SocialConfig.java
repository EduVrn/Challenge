package challenge.webside.config;

import challenge.webside.config.interceptors.GithubAfterConnectInterceptor;
import challenge.webside.config.interceptors.FacebookAfterConnectInterceptor;
import challenge.webside.config.interceptors.TwitterAfterConnectInterceptor;
import challenge.webside.config.interceptors.VkontakteAfterConnectInterceptor;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import challenge.webside.dao.UsersDao;
import challenge.webside.services.AccountConnectionSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.vkontakte.connect.VKontakteConnectionFactory;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

    @Autowired
    TwitterAfterConnectInterceptor tweetInterceptor;
    @Autowired
    GithubAfterConnectInterceptor gitInterceptor;
    @Autowired
    FacebookAfterConnectInterceptor facebookInterceptor;
    @Autowired
    VkontakteAfterConnectInterceptor vkInterceptor;
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    private UsersDao usersDao;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        /*connectionFactoryConfigurer.addConnectionFactory(new FacebookConnectionFactory(
                environment.getProperty("spring.social.facebook.appId"),
                environment.getProperty("spring.social.facebook.appSecret")));*/
        //connectionFactoryConfigurer.addConnectionFactory(new TwitterConnectionFactory(
                //environment.getProperty("twitter.consumerKey"),
                //environment.getProperty("twitter.consumerSecret")));
        connectionFactoryConfigurer.addConnectionFactory(new GitHubConnectionFactory(
                environment.getProperty("spring.social.github.appId"),
                environment.getProperty("spring.social.github.appSecret")));
        connectionFactoryConfigurer.addConnectionFactory(new VKontakteConnectionFactory(
                environment.getProperty("vkontakte.appKey"),
                environment.getProperty("vkontakte.appSecret")));
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.text("70617373776f72640d0a", "6e657473616c740d0a"));
        repository.setConnectionSignUp(new AccountConnectionSignUpService(usersDao));
        return repository;
    }

    @Bean
    public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
            ConnectionRepository connectionRepository) {
        ConnectController controller = new CustomConnectController(connectionFactoryLocator, connectionRepository, tweetInterceptor, gitInterceptor, facebookInterceptor,vkInterceptor);
        return controller;
    }

}
