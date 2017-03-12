package challenge.dbside.ini;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import challenge.common.AccessProp;
import challenge.dbside.dbconfig.ParserDBConfiguration;
import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.Comment;
import challenge.dbside.models.Image;
import challenge.dbside.models.Tag;
import challenge.dbside.models.User;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;
import challenge.dbside.models.status.ChallengeDefinitionStatus;
import challenge.dbside.models.status.ChallengeInstanceStatus;
import challenge.dbside.property.PropertyDB;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.imagesstorage.ImageStoreService;

import java.util.logging.Level;

@Component
public class InitialLoader {

    @Autowired
    @Qualifier("storageServiceTypeOfAttribute")
    private MediaService serviceAttr;

    @Autowired
    @Qualifier("storageServiceTypeOfEntity")
    private MediaService serviceEntity;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntityInit;

    @Autowired
    @Qualifier("storageServiceProperty")
    private MediaService serviceProperty;

    public void initial() {
        boolean iniFlag = false;
        PropertyDB version = (PropertyDB) serviceProperty.findById("version", PropertyDB.class);

        if (version == null) {
            iniFlag = true;
            version = new PropertyDB("version", "0");
        }
        Integer nVersion = Integer.valueOf(version.getValue());

        try {
            nVersion = createContext(nVersion);
            if (version.getValue().equals("0")) {
                version.setValue(nVersion.toString());
                serviceProperty.save(version);
            } else {
                version.setValue(nVersion.toString());
                serviceProperty.update(version);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // usersCount, user's chaldefsCount,instancesCount, CommentsCount, Comment'sEmbedenceCount
        //init(5, 2,2, 4, 3);
        if (iniFlag == true) {
            init(1, 2, 2, 4, 7);
        }
    }

    private Integer createContext(Integer versionDB) throws Exception {
        Integer versionApp = Integer.valueOf(AccessProp.getProperties().getCurrentVersionDB());

        ParserDBConfiguration p = new ParserDBConfiguration();
        if (versionDB > 0) {
            ContextType contextType = ContextType.getInstance();

            for (TypeOfAttribute t : (List<TypeOfAttribute>) serviceAttr.getAll(TypeOfAttribute.class)) {
                p.getAllAttribute().put(t.getName(), t);

                //p.getAddCandiateAttributes().put(t.getName(), t);
                //contextType.add(t);
            }

            for (TypeOfEntity t : (List<TypeOfEntity>) serviceEntity.getAll(TypeOfEntity.class)) {
                p.getAllEntities().put(t.getNameTypeEntity(), t);
                //p.getAddCandidateEntities().put(t.getNameTypeEntity(), t);
                //contextType.add(t);
            }
        }

        for (Integer i = versionDB + 1; i <= versionApp; i++) {
            String filePath = AccessProp.getProperties().getStructureDBPath()
                    + "v" + i.toString() + ".xml";

            InputStream input = new ClassPathResource(filePath).getInputStream();
            //p = new ParserDBConfiguration();    		
            p.applyConfiguration(input);

            for (TypeOfAttribute t : p.getAddCandiateAttributes()) {
                serviceAttr.save(t);
            }
            for (TypeOfAttribute t : p.getRmCandiateAttributes()) {
                serviceAttr.delete(t);
            }

            for (TypeOfEntity t : p.getAddCandidateEntities()) {
                serviceEntity.save(t);
            }
            for (TypeOfEntity t : p.getUpdateCandidateEntities()) {
                serviceEntity.update(t);
            }
            for (TypeOfEntity t : p.getRmCandidateEntities()) {
                serviceEntity.delete(t);
            }

        }

        for (TypeOfAttribute t : (List<TypeOfAttribute>) serviceAttr.getAll(TypeOfAttribute.class)) {
            ContextType.getInstance().add(t);
        }

        for (TypeOfEntity t : (List<TypeOfEntity>) serviceEntity.getAll(TypeOfEntity.class)) {
            ContextType.getInstance().add(t);
        }

        /*for(TypeOfAttribute t : ContextType.getInstance().getAvailableAttributes()) {
    		
    		serviceAttr.save(t);
    	}
    	
    	for(TypeOfEntity t : ContextType.getInstance().getAvailableEntities()) {
    		serviceEntity.save(t);
    	}*/
        return versionApp;
    }

    private static String[] generateRandomWords(int numberOfWords) {
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

    private Comment createNewEmbeddedComments(int countOfEmbedence, User userToCreate) {
        Comment comment = createNewComment(userToCreate);
        if (countOfEmbedence > 0) {
            comment.addComment(createNewEmbeddedComments(countOfEmbedence - 1, userToCreate));
        }
        return comment;
    }

    private Comment createNewComment(User userToCreate) {
        StringBuilder text = new StringBuilder();
        Comment comment = new Comment();
        for (String word : generateRandomWords(new Random().nextInt(20) + 2)) {
            text.append(word).append(" ");
        }
        text.append(".");
        comment.setMessage(text.toString());
        comment.setDate(new Date());
        comment.setAuthor(userToCreate);
        serviceEntityInit.save(comment);
        return comment;
    }

    public void createTags() {
        Tag tag1 = new Tag();
        tag1.setName("кот");
        serviceEntityInit.save(tag1);
        Tag tag2 = new Tag();
        tag2.setName("безысходность");
        serviceEntityInit.save(tag2);
        Tag tag4 = new Tag();
        tag4.setName("ааа");
        serviceEntityInit.save(tag4);
        Tag tag5 = new Tag();
        tag5.setName("ббб");
        serviceEntityInit.save(tag5);
        Tag tag6 = new Tag();
        tag6.setName("другое");
        serviceEntityInit.save(tag6);
    }

    public void init(int countOfUsers, int countOfChalDefs, int countOfInstanses, int countOfComments, int countOfEmbedence) {
        try {
            ImageStoreService.saveDefaultImage(new File("src/main/resources/static/images/photo_not_available.jpg"));
            ImageStoreService.saveDefaultUserImage(new File("src/main/resources/static/images/user_photo_not_available.jpg"));
        } catch (Exception ex) {
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<String> images = new ArrayList<>();
        images.add("src/main/resources/static/images/firstExampleChallenge.jpg");
        images.add("src/main/resources/static/images/secondExampleTask.png");
        images.add("src/main/resources/static/images/wheely.jpg");
        images.add("src/main/resources/static/images/speed.jpg");
        images.add("src/main/resources/static/images/break.png");
        images.add("src/main/resources/static/images/AvaDefault.jpg");
        createTags();
        for (int i = 0; i < countOfUsers; i++) {
            User userToCreate = new User();
            userToCreate.setRating(0);
            userToCreate.setName(generateRandomWords(1)[0] + "-user");
            serviceEntityInit.save(userToCreate);
            Image picForUser = new Image();
            picForUser.setIsMain(Boolean.TRUE);
            serviceEntityInit.save(picForUser);
            try {
                ImageStoreService.saveImage(new File(images.get(new Random().nextInt(images.size()))), picForUser);
                serviceEntityInit.update(picForUser);
            } catch (Exception ex) {
                Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            userToCreate.addImage(picForUser);
            serviceEntityInit.update(userToCreate);
            for (int k = 0; k < countOfChalDefs; k++) {
                ChallengeDefinition chalToCreate = new ChallengeDefinition();
                chalToCreate.setRating(0);
                chalToCreate.setName(generateRandomWords(1)[0] + "-challenge");
                chalToCreate.setStatus(ChallengeDefinitionStatus.CREATED);
                StringBuilder text = new StringBuilder();
                for (String word : generateRandomWords(new Random().nextInt(20) + 2)) {
                    text.append(word).append(" ");
                }
                text.append(".");
                chalToCreate.setDescription(text.toString());
                chalToCreate.setDate(new Date());
                chalToCreate.setCreator(userToCreate);
                serviceEntityInit.save(chalToCreate);
                for (int m = 0; m < countOfComments; m++) {
                    Comment comment = createNewEmbeddedComments(countOfEmbedence, userToCreate);
                    chalToCreate.addComment(comment);
                }
                serviceEntityInit.update(chalToCreate);
                Image pic = new Image();
                pic.setIsMain(Boolean.TRUE);
                serviceEntityInit.save(pic);
                try {
                    ImageStoreService.saveImage(new File(images.get(new Random().nextInt(images.size()))), pic);
                    serviceEntityInit.update(pic);

                } catch (Exception ex) {
                    Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
                chalToCreate.addImage(pic);
                serviceEntityInit.update(chalToCreate);
                userToCreate.addChallenge(chalToCreate);
                serviceEntityInit.update(userToCreate);
                for (int m = 0; m < countOfInstanses; m++) {
                    ChallengeInstance chalInstance = new ChallengeInstance();
                    chalInstance.setName(generateRandomWords(1)[0] + "-instance");
                    chalInstance.setStatus(ChallengeInstanceStatus.AWAITING);

                    chalInstance.setDescription("After (may be)");
                    chalInstance.setDate(new Date());
                    serviceEntityInit.save(chalInstance);

                    Image picForInstance = new Image();
                    picForInstance.setIsMain(Boolean.TRUE);
                    serviceEntityInit.save(picForInstance);
                    try {
                        ImageStoreService.saveImage(new File(images.get(new Random().nextInt(images.size()))), picForInstance);
                        serviceEntityInit.update(picForInstance);

                    } catch (Exception ex) {
                        Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
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
        image.setIsMain(Boolean.TRUE);
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
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        User user1 = new User();
        user1.setName("Evgeniy 1");
        Image profilePic = new Image();
        profilePic.setIsMain(Boolean.TRUE);
        serviceEntityInit.save(profilePic);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic);
            serviceEntityInit.update(profilePic);
        } catch (Exception ex) {
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        serviceEntityInit.save(user1);
        user1.addImage(profilePic);
        serviceEntityInit.update(user1);

        ChallengeDefinition chalDef2 = new ChallengeDefinition();
        chalDef2.setName("Hi, make your's task 4 Ivan.");
        chalDef2.setDescription("After (may be)");
        Image image2 = new Image();
        image2.setIsMain(Boolean.TRUE);
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
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        user1.addChallenge(chalDef1);
        user1.addChallenge(chalDef2);
        serviceEntityInit.update(user1);

        ChallengeInstance chalInstance1 = new ChallengeInstance();
        chalInstance1.setName("I can made it");
        chalInstance1.setStatus(ChallengeInstanceStatus.AWAITING);
        chalInstance1.setAcceptor(user1);
        chalInstance1.setDescription("After (may be)");
        Image imageForChalInstance1 = new Image();
        imageForChalInstance1.setIsMain(Boolean.TRUE);
        serviceEntityInit.save(imageForChalInstance1);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/secondExampleTask.png"), imageForChalInstance1);
            serviceEntityInit.update(imageForChalInstance1);
        } catch (Exception ex) {
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        chalInstance1.setDate(new Date());
        serviceEntityInit.save(chalInstance1);
        chalInstance1.addImage(imageForChalInstance1);
        serviceEntityInit.update(chalInstance1);

        ChallengeInstance chalInstance2 = new ChallengeInstance();
        chalInstance2.setName("Ou ");
        chalInstance2.setStatus(ChallengeInstanceStatus.AWAITING);
        chalInstance2.setAcceptor(user1);
        chalInstance2.setDescription("After (may be)");
        Image imageForChalInstance2 = new Image();
        imageForChalInstance2.setIsMain(Boolean.TRUE);
        serviceEntityInit.save(imageForChalInstance2);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/secondExampleTask.png"), imageForChalInstance2);
            serviceEntityInit.update(imageForChalInstance2);
        } catch (Exception ex) {
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        chalInstance2.setDate(new Date());
        serviceEntityInit.save(chalInstance2);
        chalInstance2.addImage(imageForChalInstance2);
        serviceEntityInit.update(chalInstance2);

        User user2 = new User();
        user2.setName("Jonnie Fast-Foot");
        Image profilePic2 = new Image();
        profilePic2.setIsMain(Boolean.TRUE);
        serviceEntityInit.save(profilePic2);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic2);
            serviceEntityInit.update(profilePic2);
        } catch (Exception ex) {
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        serviceEntityInit.save(user2);
        user2.addImage(profilePic2);
        serviceEntityInit.update(user2);

        User user3 = new User();
        user3.setName("Annet Fast-Food");
        Image profilePic3 = new Image();
        profilePic3.setIsMain(Boolean.TRUE);
        serviceEntityInit.save(profilePic3);
        try {
            ImageStoreService.saveImage(new File("src/main/resources/static/images/AvaDefault.jpg"), profilePic3);
            serviceEntityInit.update(profilePic3);
        } catch (Exception ex) {
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        serviceEntityInit.save(user3);
        user3.addImage(profilePic3);
        serviceEntityInit.update(user3);
    }
}
