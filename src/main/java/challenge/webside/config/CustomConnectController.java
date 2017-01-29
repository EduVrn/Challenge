package challenge.webside.config;

import challenge.webside.config.interceptors.GithubAfterConnectInterceptor;
import challenge.webside.config.interceptors.FacebookAfterConnectInterceptor;
import challenge.webside.config.interceptors.TwitterAfterConnectInterceptor;
import challenge.webside.config.interceptors.VkontakteAfterConnectInterceptor;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;

public class CustomConnectController extends org.springframework.social.connect.web.ConnectController {

    private TwitterAfterConnectInterceptor twitterConnInterceptor;
    private GithubAfterConnectInterceptor gitConnInterceptor;
    private FacebookAfterConnectInterceptor fbConnInterceptor;
    private VkontakteAfterConnectInterceptor vkConnInterceptor;

    @Inject
    public CustomConnectController(ConnectionFactoryLocator connectionFactoryLocator,
            ConnectionRepository connectionRepository,
            TwitterAfterConnectInterceptor twitterConnInterceptor,
            GithubAfterConnectInterceptor gitConnInterceptor,
            FacebookAfterConnectInterceptor fbConnInterceptor,
            VkontakteAfterConnectInterceptor vkConnInterceptor) {
        super(connectionFactoryLocator, connectionRepository);
        this.twitterConnInterceptor = twitterConnInterceptor;
        this.gitConnInterceptor = gitConnInterceptor;
        this.fbConnInterceptor = fbConnInterceptor;
        this.vkConnInterceptor = vkConnInterceptor;
    }

    @PostConstruct
    public void addInterceptor() {
        this.addInterceptor(twitterConnInterceptor);
        this.addInterceptor(gitConnInterceptor);
        this.addInterceptor(fbConnInterceptor);
        this.addInterceptor(vkConnInterceptor);
    }

    @Override
    protected String connectedView(String providerId) {
        return "redirect:/myprofile";
    }

    @Override
    protected String connectView(String providerId) {
        return "redirect:/myprofile";
    }
}
