package challenge;

import challenge.dbside.ini.InitialLoader;
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
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import challenge.dbside.services.ini.MediaService;
import java.util.Arrays;

@Configuration
@EnableAutoConfiguration
@ComponentScan
/*@ImportResource({
	//"/home/serg/eclipse/workspace_challenge/Challenge/src/main/resources/Some.hgm.xml"
	"classpath:/BaseEntity.hbm.xml"
})*/
public class Application extends SpringBootServletInitializer {

    private static InitialLoader initiator;

    @Autowired
    public void setSomeThing(InitialLoader someThing) {
        Application.initiator = someThing;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(Application.class, args);               
        
        System.out.println("All created beans:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
        initiator.initial();
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
