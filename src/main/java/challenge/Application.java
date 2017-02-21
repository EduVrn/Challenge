package challenge;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import challenge.dbside.ini.InitialLoader;
import challenge.dbside.ini.JdbcProperties;
import challenge.webside.imagesstorage.ImageStoreService;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends SpringBootServletInitializer {

    //private static final Logger LOG = LoggerFactory.getLogger(PostgresDatabase.class);

    private static final String CHANGELOG_FILE = "src/main/resources/liquibase/changelog-master.xml";
    private static JdbcProperties jdbcProperties;
	
	
	
	
	
	
	
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
    	
    	String contexts = "schemedata";
    	
    	
    	Properties properties = new Properties();
        
        InputStream istream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("application.properties");
        try {
            properties.load(istream);

            jdbcProperties = new JdbcProperties();

            String driverClassName = properties
                    .getProperty("spring.datasource.driverClassName");
            jdbcProperties.setDriverClassName(driverClassName);

            String url = properties.getProperty("spring.datasource.url");
            jdbcProperties.setUrl(url);

            String username = properties.getProperty("spring.datasource.username");
            jdbcProperties.setUserName(username);

            String password = properties.getProperty("spring.datasource.password");
            jdbcProperties.setPassword(password);

            istream.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Can't load jdbc properties");
        }

    	
    	
    	//initiator.createTables("schemedata,initdata");
    	
    	try {
            Class.forName(jdbcProperties.getDriverClassName()).newInstance();
            String str = jdbcProperties.getUrl();
            DatabaseConnection connection = new JdbcConnection(DriverManager.getConnection(str, 
            		jdbcProperties.getUserName(), jdbcProperties.getPassword()));

            PostgresDatabase postgresDatabase = new PostgresDatabase();
            postgresDatabase.setConnection(connection);
            Liquibase liquibase = new Liquibase(CHANGELOG_FILE, new FileSystemResourceAccessor(), connection);
            liquibase.update(contexts);
            connection.close();
            postgresDatabase.getConnection().close();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (LiquibaseException e) {
            e.printStackTrace();
        }
    	
    	
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        try {
            ImageStoreService.login();
        } catch (Exception ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        initiator.initial();
    }

}

