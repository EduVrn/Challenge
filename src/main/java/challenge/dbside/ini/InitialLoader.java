package challenge.dbside.ini;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Image;
import challenge.dbside.models.User;
import challenge.dbside.models.ini.TypeAttribute;
import challenge.dbside.models.ini.TypeEntity;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;
import challenge.dbside.models.status.ChallengeDefinitionStatus;
import challenge.dbside.models.status.ChallengeStatus;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.imagesstorage.ImageStoreService;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

@Component
public class InitialLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PostgresDatabase.class);

    private static final String CHANGELOG_FILE = "src/main/resources/liquibase/changelog-master.xml";
    private JdbcProperties jdbcProperties;

    @Autowired
    @Qualifier("storageServiceTypeOfAttribute")
    private MediaService serviceAttr;

    @Autowired
    @Qualifier("storageServiceTypeOfEntity")
    private MediaService serviceEntity;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntityInit;

    public InitialLoader() {
        jdbcProperties = loadProperties();
    }

    private JdbcProperties loadProperties() {
        Properties properties = new Properties();
        JdbcProperties result = null;
        InputStream istream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("application.properties");
        try {
            properties.load(istream);

            result = new JdbcProperties();

            String driverClassName = properties
                    .getProperty("spring.datasource.driverClassName");
            result.setDriverClassName(driverClassName);

            String url = properties.getProperty("spring.datasource.url");
            result.setUrl(url);

            String username = properties.getProperty("spring.datasource.username");
            result.setUserName(username);

            String password = properties.getProperty("spring.datasource.password");
            result.setPassword(password);

            istream.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Can't load jdbc properties");
        }

        return result;
    }

    public void initial(String contexts) {
        createContext(contexts);
        init();
    }

    private void createContext(String contexts) {
        try {
            Class.forName(jdbcProperties.getDriverClassName()).newInstance();
            DatabaseConnection connection = new JdbcConnection(DriverManager.getConnection(jdbcProperties.getUrl()));

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


        TypeOfAttribute attrName = new TypeOfAttribute(1, "name", TypeAttribute.STRING.getValue());
        TypeOfAttribute attrSurname = new TypeOfAttribute(2, "surname", TypeAttribute.STRING.getValue());
        TypeOfAttribute attrDate = new TypeOfAttribute(3, "date", TypeAttribute.STRING.getValue());
        TypeOfAttribute attrDescription = new TypeOfAttribute(4, "description", TypeAttribute.STRING.getValue());
        TypeOfAttribute attrImageRef = new TypeOfAttribute(5, "imageref", TypeAttribute.STRING.getValue());
        TypeOfAttribute attrChalStatus = new TypeOfAttribute(6, "chalStatus", TypeAttribute.STRING.getValue());
        TypeOfAttribute attrChalDefStatus = new TypeOfAttribute(7, "chalDefStatus", TypeAttribute.STRING.getValue());
        TypeOfAttribute attrMessage = new TypeOfAttribute(8, "message", TypeAttribute.STRING.getValue());

        TypeOfAttribute refAttrFriends = new TypeOfAttribute(31, "friends", TypeAttribute.REF.getValue());
        TypeOfAttribute refAttrAcceptedChalIns = new TypeOfAttribute(32, "acceptedChalIns", TypeAttribute.REF.getValue());
        TypeOfAttribute refAttrAutorComment = new TypeOfAttribute(33, "autorComment", TypeAttribute.REF.getValue());

        //serviceAttr.save(attrName);
        //serviceAttr.save(attrSurname);
        //serviceAttr.save(attrDate);
        //serviceAttr.save(attrDescription);
        //serviceAttr.save(attrImageRef);
        //serviceAttr.save(attrChalStatus);
        //serviceAttr.save(attrChalDefStatus);
        //serviceAttr.save(attrMessage);

        //serviceAttr.save(refAttrFriends);
        //serviceAttr.save(refAttrAcceptedChalIns);
        //serviceAttr.save(refAttrAutorComment);

        TypeOfEntity entityUser = new TypeOfEntity("User", TypeEntity.USER.getValue());
        entityUser.add(attrName);
        entityUser.add(attrSurname);

        entityUser.add(refAttrFriends);
        entityUser.add(refAttrAcceptedChalIns);
        entityUser.add(refAttrAutorComment);
        //serviceEntity.save(entityUser);

        TypeOfEntity entityChallenge = new TypeOfEntity("ChallengeDefinition", TypeEntity.CHALLENGE_DEFINITION.getValue());
        entityChallenge.add(attrName);
        entityChallenge.add(attrDate);
        entityChallenge.add(attrDescription);
        entityChallenge.add(attrChalDefStatus);
        //serviceEntity.save(entityChallenge);

        TypeOfEntity entityChallengeInstance = new TypeOfEntity("ChallengeInstance", TypeEntity.CHALLENGE_INSTANCE.getValue());
        entityChallengeInstance.add(attrName);
        entityChallengeInstance.add(attrChalStatus);
        entityChallengeInstance.add(attrDate);
        entityChallengeInstance.add(attrDescription);
        entityChallengeInstance.add(refAttrAcceptedChalIns);
        //serviceEntity.save(entityChallengeInstance);

        TypeOfEntity entityComment = new TypeOfEntity("Comment", TypeEntity.COMMENT.getValue());
        entityComment.add(attrDate);
        entityComment.add(attrMessage);

        entityUser.add(refAttrAutorComment);
        //serviceEntity.save(entityComment);

        TypeOfEntity entityImage = new TypeOfEntity("Image", TypeEntity.IMAGE.getValue());
        entityImage.add(attrImageRef);
        //serviceEntity.save(entityImage);

        ContextType contextType = ContextType.getInstance();
        contextType.add(attrName);
        contextType.add(attrSurname);
        contextType.add(attrDate);
        contextType.add(attrDescription);
        contextType.add(attrImageRef);
        contextType.add(attrChalStatus);
        contextType.add(attrChalDefStatus);
        contextType.add(attrMessage);

        contextType.add(refAttrFriends);
        contextType.add(refAttrAcceptedChalIns);
        contextType.add(refAttrAutorComment);

        contextType.add(entityUser);
        contextType.add(entityChallenge);
        contextType.add(entityChallengeInstance);
        contextType.add(entityComment);
        contextType.add(entityImage);
    }
    
    public static String[] generateRandomWords(int numberOfWords) {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for (int i = 0; i < numberOfWords; i++) {
            char[] word = new char[random.nextInt(8) + 3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for (int j = 0; j < word.length; j++) {
                word[j] = (char) ('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
    }

    public void init() {
        List<String> images = new ArrayList<>();
        images.add("src/main/resources/static/images/firstExampleChallenge.jpg");
        images.add("src/main/resources/static/images/secondExampleTask.png");
        images.add("src/main/resources/static/images/wheely.jpg");
        images.add("src/main/resources/static/images/speed.jpg");
        images.add("src/main/resources/static/images/break.png");
        images.add("src/main/resources/static/images/AvaDefault.jpg");
        for (int i = 0; i < 5; i++) {
            User userToCreate = new User();
            userToCreate.setName(generateRandomWords(1)[0] + "-user");
            serviceEntityInit.save(userToCreate);
            Image picForUser = new Image();
            serviceEntityInit.save(picForUser);
            try {
                ImageStoreService.saveImage(new File(images.get(new Random().nextInt(images.size()))), picForUser);
                serviceEntityInit.update(picForUser);
            } catch (Exception ex) {
                LOG.error("Error during database init", ex);
            }
            userToCreate.addImage(picForUser);
            serviceEntityInit.update(userToCreate);
            for (int k = 0; k < 2; k++) {
                ChallengeDefinition chalToCreate = new ChallengeDefinition();
                chalToCreate.setName(generateRandomWords(1)[0] + "-challenge");
                chalToCreate.setStatus(ChallengeDefinitionStatus.CREATED);
                StringBuilder descript = new StringBuilder();
                for (String word : generateRandomWords(new Random().nextInt(20) + 2)) {
                    descript.append(word).append(" ");
                }
                descript.append(".");
                chalToCreate.setDescription(descript.toString());
                chalToCreate.setDate(new Date());
                chalToCreate.setCreator(userToCreate);
                serviceEntityInit.save(chalToCreate);
                Image pic = new Image();
                serviceEntityInit.save(pic);
                try {
                    ImageStoreService.saveImage(new File(images.get(new Random().nextInt(images.size()))), pic);
                    serviceEntityInit.update(pic);

                } catch (Exception ex) {
                    LOG.error("Error during database init", ex);
                }
                chalToCreate.addImage(pic);
                serviceEntityInit.update(chalToCreate);
                userToCreate.addChallenge(chalToCreate);

                for (Object user : serviceEntityInit.getAll(User.class)) {
                    User userToSave = (User) user;
                    userToCreate.addFriend(userToSave);
                };
                serviceEntityInit.update(userToCreate);

                for (int m = 0; m < 2; m++) {
                    ChallengeInstance chalInstance = new ChallengeInstance();
                    chalInstance.setName(generateRandomWords(1)[0] + "-instance");
                    chalInstance.setStatus(ChallengeStatus.AWAITING);

                    chalInstance.setDescription("After (may be)");
                    chalInstance.setDate(new Date());
                    serviceEntityInit.save(chalInstance);
                    Image picForInstance = new Image();
                    serviceEntityInit.save(picForInstance);
                    try {
                        ImageStoreService.saveImage(new File(images.get(new Random().nextInt(images.size()))), picForInstance);
                        serviceEntityInit.update(picForInstance);

                    } catch (Exception ex) {
                        LOG.error("Error during database init", ex);
                    }
                    chalInstance.addImage(picForInstance);
                    serviceEntityInit.update(chalInstance);
                    chalToCreate.addChallengeInstance(chalInstance);
                    serviceEntityInit.update(chalToCreate);
                    User user = (User) serviceEntityInit.getAll(User.class).get(new Random().nextInt(serviceEntityInit.getAll(User.class).size()));
                    chalInstance.setAcceptor(user);
                    serviceEntityInit.update(chalInstance);

                }
            }
        }
    }

    public void fasterInit() {

        ChallengeDefinition chalDef1 = new ChallengeDefinition();
        chalDef1.setName("Make something");
        chalDef1.setDescription("Hi, I'm first. Selected me!");
        Image image = new Image();
        serviceEntityInit.save(image);
        chalDef1.setStatus(ChallengeDefinitionStatus.CREATED);
        chalDef1.setDate(new Date());
        serviceEntityInit.save(chalDef1);
        chalDef1.addImage(image);
        serviceEntityInit.update(chalDef1);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/firstExampleChallenge.jpg"), image);
            serviceEntityInit.update(image);
        } catch (Exception ex) {
            LOG.error("Error during database init", ex);
        }

        User user1 = new User();
        user1.setName("Evgeniy 1");
        Image profilePic = new Image();
        serviceEntityInit.save(profilePic);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic);
            serviceEntityInit.update(profilePic);
        } catch (Exception ex) {
            LOG.error("Error during database init", ex);
        }
        serviceEntityInit.save(user1);
        user1.addImage(profilePic);
        serviceEntityInit.update(user1);

        ChallengeDefinition chalDef2 = new ChallengeDefinition();
        chalDef2.setName("Hi, make your's task 4 Ivan.");
        chalDef2.setDescription("After (may be)");
        Image image2 = new Image();
        serviceEntityInit.save(image2);
        chalDef2.setDate(new Date());
        chalDef2.setStatus(ChallengeDefinitionStatus.CREATED);
        serviceEntityInit.save(chalDef2);
        chalDef2.addImage(image2);
        serviceEntityInit.update(chalDef2);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/secondExampleTask.png"), image2);
            serviceEntityInit.update(image2);
        } catch (Exception ex) {
            LOG.error("Error during database init", ex);
        }

        user1.addChallenge(chalDef1);
        user1.addChallenge(chalDef2);
        serviceEntityInit.update(user1);

        ChallengeInstance chalInstance1 = new ChallengeInstance();
        chalInstance1.setName("I can made it");
        chalInstance1.setStatus(ChallengeStatus.AWAITING);
        chalInstance1.setAcceptor(user1);
        chalInstance1.setDescription("After (may be)");
        Image imageForChalInstance1 = new Image();
        serviceEntityInit.save(imageForChalInstance1);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/secondExampleTask.png"), imageForChalInstance1);
            serviceEntityInit.update(imageForChalInstance1);
        } catch (Exception ex) {
            LOG.error("Error during database init", ex);
        }
        chalInstance1.setDate(new Date());
        serviceEntityInit.save(chalInstance1);
        chalInstance1.addImage(imageForChalInstance1);
        serviceEntityInit.update(chalInstance1);

        ChallengeInstance chalInstance2 = new ChallengeInstance();
        chalInstance2.setName("Ou ");
        chalInstance2.setStatus(ChallengeStatus.AWAITING);
        chalInstance2.setAcceptor(user1);
        chalInstance2.setDescription("After (may be)");
        Image imageForChalInstance2 = new Image();
        serviceEntityInit.save(imageForChalInstance2);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/secondExampleTask.png"), imageForChalInstance2);
            serviceEntityInit.update(imageForChalInstance2);
        } catch (Exception ex) {
            LOG.error("Error during database init", ex);
        }
        chalInstance2.setDate(new Date());
        serviceEntityInit.save(chalInstance2);
        chalInstance2.addImage(imageForChalInstance2);
        serviceEntityInit.update(chalInstance2);

        User user2 = new User();
        user2.setName("Jonnie Fast-Foot");
        Image profilePic2 = new Image();
        serviceEntityInit.save(profilePic2);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic2);
            serviceEntityInit.update(profilePic2);
        } catch (Exception ex) {
            LOG.error("Error during database init", ex);
        }
        serviceEntityInit.save(user2);
        user2.addImage(profilePic2);
        serviceEntityInit.update(user2);

        User user3 = new User();
        user3.setName("Annet Fast-Food");
        Image profilePic3 = new Image();
        serviceEntityInit.save(profilePic3);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic3);
            serviceEntityInit.update(profilePic3);
        } catch (Exception ex) {
            LOG.error("Error during database init", ex);
        }
        serviceEntityInit.save(user3);
        user3.addImage(profilePic3);
        serviceEntityInit.update(user3);
    }
}
