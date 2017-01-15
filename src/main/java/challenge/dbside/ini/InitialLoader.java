package challenge.dbside.ini;

import challenge.dbside.models.*;
import challenge.dbside.models.ini.TypeAttribute;
import challenge.dbside.models.ini.TypeEntity;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;
import challenge.dbside.models.status.ChallengeDefinitionStatus;
import challenge.dbside.models.status.ChallengeStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import challenge.dbside.services.ini.MediaService;
import challenge.webside.imagesstorage.ImageStoreService;
import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

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

    public void initial() {
        //try load from base

        //else 
        //create
        createContext();
        init();
    }

    private void createContext() {
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

        serviceAttr.save(attrName);
        serviceAttr.save(attrSurname);
        serviceAttr.save(attrDate);
        serviceAttr.save(attrDescription);
        serviceAttr.save(attrImageRef);
        serviceAttr.save(attrChalStatus);
        serviceAttr.save(attrChalDefStatus);
        serviceAttr.save(attrMessage);
        
        serviceAttr.save(refAttrFriends);
        serviceAttr.save(refAttrAcceptedChalIns);
        serviceAttr.save(refAttrAutorComment);

        TypeOfEntity entityUser = new TypeOfEntity("User", TypeEntity.USER.getValue());
        entityUser.add(attrName);
        entityUser.add(attrSurname);

        entityUser.add(refAttrFriends);
        entityUser.add(refAttrAcceptedChalIns);
        entityUser.add(refAttrAutorComment);
        serviceEntity.save(entityUser);

        TypeOfEntity entityChallenge = new TypeOfEntity("ChallengeDefinition", TypeEntity.CHALLENGE_DEFINITION.getValue());
        entityChallenge.add(attrName);
        entityChallenge.add(attrDate);
        entityChallenge.add(attrDescription);
        entityChallenge.add(attrChalDefStatus);
        serviceEntity.save(entityChallenge);

        TypeOfEntity entityChallengeInstance = new TypeOfEntity("ChallengeInstance", TypeEntity.CHALLENGE_INSTANCE.getValue());
        entityChallengeInstance.add(attrName);
        entityChallengeInstance.add(attrChalStatus);
        entityChallengeInstance.add(attrDate);
        entityChallengeInstance.add(attrDescription);
        entityChallengeInstance.add(refAttrAcceptedChalIns);
        serviceEntity.save(entityChallengeInstance);

        TypeOfEntity entityComment = new TypeOfEntity("Comment", TypeEntity.COMMENT.getValue());
        entityComment.add(attrDate);
        entityComment.add(attrMessage);

        entityUser.add(refAttrAutorComment);
        serviceEntity.save(entityComment);

        TypeOfEntity entityImage = new TypeOfEntity("Image", TypeEntity.IMAGE.getValue());
        entityImage.add(attrImageRef);
        serviceEntity.save(entityImage);

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

    public void init() {

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
            Logger.getLogger(InitialLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        User user1 = new User();
        user1.setName("Evgeniy 1");
        Image profilePic = new Image();
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
        chalInstance1.setStatus(ChallengeStatus.AWAITING);
        chalInstance1.setAcceptor(user1);
        chalInstance1.setDescription("After (may be)");
        Image imageForChalInstance1 = new Image();
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
        chalInstance2.setStatus(ChallengeStatus.AWAITING);
        chalInstance2.setAcceptor(user1);
        chalInstance2.setDescription("After (may be)");
        Image imageForChalInstance2 = new Image();
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
