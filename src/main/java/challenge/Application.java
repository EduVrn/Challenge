package challenge;

import challenge.dbside.ini.InitialLoader;
import challenge.dbside.models.BaseEntity;
import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import challenge.dbside.services.ini.MediaService;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends SpringBootServletInitializer {

    private static InitialLoader someThing;

    @Autowired
    public void setSomeThing(InitialLoader someThing) {
        Application.someThing = someThing;
    }

    static void Init(ApplicationContext context) {
        MediaService serviceEntity = (MediaService) context.getBean("storageServiceUser");
        /*Example entity*/
        User user1 = new User();
        serviceEntity.save(user1);

        User user2 = new User();
        user2.setName("name2");
        serviceEntity.save(user2);

        User user3 = new User();
        user3.setName("name3");
        serviceEntity.save(user3);

        User user4 = new User();
        user4.setName("name4");
        serviceEntity.save(user4);

        System.out.println("\n\nUpdate");
        user1.setName("new_name");
        //user1.setParent(user2);
        serviceEntity.update(user1);

        user4.setName("new_name4");
        //user4.setParent(user2);
        serviceEntity.update(user4);

        Set userSet = new HashSet();
        userSet.add(user1);
        userSet.add(user4);
        user2.setChildren(userSet);
        serviceEntity.update(user2);

        /**/
        System.out.println("\n\nDelete");
        serviceEntity.delete(user1);

        System.out.println("\n\nGet all");
        /**
         * *TEMPLAAAAAAAAAAAAAAAAAAAAAAATE !!!!!!***
         */
        List<User> list = serviceEntity.getAll(User.class);
        for (User b : list) {
            System.out.println(b);
        }

        System.out.println("\n\nDelete");
        serviceEntity.delete(user2);

        System.out.println("\n\nGet all");
        /**
         * *TEMPLAAAAAAAAAAAAAAAAAAAAAAATE !!!!!!***
         */
        list = serviceEntity.getAll(User.class);
        for (BaseEntity b : list) {
            System.out.println(b);
        }

        User user6 = new User();
        user6.setName("name6");
        serviceEntity.save(user6);
        ChallengeDefinition m = new ChallengeDefinition();
        serviceEntity.save(m);
        user3.addChallenge(m);
        user3.addFriend(user6);
        serviceEntity.update(user3);

        System.out.println("modified users: \n");
        System.out.println(user3.toString());
        System.out.println(user6.toString());

        ChallengeDefinition challengeRoadMap1 = new ChallengeDefinition();
        challengeRoadMap1.setName("challengeName1");
        serviceEntity.save(challengeRoadMap1);

        /**
         * *TEMPLAAAAAAAAAAAAAAAAAAAAAAATE !!!!!!***
         */
        list = serviceEntity.getAll(User.class);

        for (User b : list) {
            System.out.println(b);
        }
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        //ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/beans.xml");

        //Init();
        SpringApplication.run(Application.class, args);
        someThing.initial();
    }

}

/*@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
          String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }*/
