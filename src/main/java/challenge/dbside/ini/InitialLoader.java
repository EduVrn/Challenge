package challenge.dbside.ini;

import challenge.dbside.models.*;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import challenge.dbside.services.ini.MediaService;
import java.util.Date;
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

        TypeOfAttribute attrName = new TypeOfAttribute(1, "name", 1);
        TypeOfAttribute attrSurname = new TypeOfAttribute(2, "surname", 1);
        TypeOfAttribute attrDate = new TypeOfAttribute(3, "date", 2);
        TypeOfAttribute attrDescription = new TypeOfAttribute(4, "description", 1);
        TypeOfAttribute attrImageRef = new TypeOfAttribute(5, "imageref", 1);
        TypeOfAttribute attrChalStatus = new TypeOfAttribute(6, "chalStatus", 1);
        TypeOfAttribute attrChalDefStatus = new TypeOfAttribute(7, "chalDefStatus", 1);
        TypeOfAttribute attrMessage = new TypeOfAttribute(8, "message", 1);

        serviceAttr.save(attrName);
        serviceAttr.save(attrSurname);
        serviceAttr.save(attrDate);
        serviceAttr.save(attrDescription);
        serviceAttr.save(attrImageRef);
        serviceAttr.save(attrChalStatus);
        serviceAttr.save(attrChalDefStatus);
        serviceAttr.save(attrMessage);

        TypeOfEntity entity = new TypeOfEntity("User");
        entity.add(attrName);
        entity.add(attrSurname);
        entity.add(attrImageRef);
        serviceEntity.save(entity);

        TypeOfEntity entityChallenge = new TypeOfEntity("ChallengeDefinition");
        entityChallenge.add(attrName);
        entityChallenge.add(attrDate);
        entityChallenge.add(attrDescription);
        entityChallenge.add(attrImageRef);
        entityChallenge.add(attrChalDefStatus);
        serviceEntity.save(entityChallenge);

        TypeOfEntity entityChallengeInstance = new TypeOfEntity("ChallengeInstance");
        entityChallengeInstance.add(attrName);
        entityChallengeInstance.add(attrChalStatus);
        serviceEntity.save(entityChallengeInstance);

        TypeOfEntity entityComment = new TypeOfEntity("Comment");
        entityComment.add(attrDate);
        entityComment.add(attrMessage);
        serviceEntity.save(entityComment);

        ContextType contextType = ContextType.getInstance();

        contextType.add(attrName);
        contextType.add(attrSurname);
        contextType.add(attrDate);
        contextType.add(attrDescription);
        contextType.add(attrImageRef);
        contextType.add(attrChalStatus);
        contextType.add(attrChalDefStatus);
        contextType.add(attrMessage);

        contextType.add(entity);
        contextType.add(entityChallenge);
        contextType.add(entityChallengeInstance);
        contextType.add(entityComment);

    }

    public void init() {
        ChallengeDefinition chalDef1 = new ChallengeDefinition();
        chalDef1.setName("Make something");
        chalDef1.setDescription("Hi, I'm first. Selected me!");
        chalDef1.setImageRef("firstExampleChallenge.jpg");

        chalDef1.setDate(new Date());

        serviceEntityInit.save(chalDef1);

        User user1 = new User();
        user1.setName("Evgeniy 1");
        user1.setImageRef("AvaDefault.jpg");
        serviceEntityInit.save(user1);

        ChallengeDefinition chalDef2 = new ChallengeDefinition();
        chalDef2.setName("Make your task 4 Ivan.");
        chalDef2.setDescription("After (may be)");
        chalDef2.setImageRef("secondExampleTask.png");
        chalDef2.setDate(new Date());
        serviceEntityInit.save(chalDef2);
        user1.addChallenge(chalDef1);
        user1.addChallenge(chalDef2);
        serviceEntityInit.update(user1);

        ChallengeInstance chalInstance1 = new ChallengeInstance();
        chalInstance1.setName("I can made it");
        chalInstance1.setStatus(ChallengeStatus.AWAITING);
        serviceEntityInit.save(chalInstance1);
        ChallengeInstance chalUnstance2 = new ChallengeInstance();
        chalUnstance2.setName("Ou ");
        chalUnstance2.setStatus(ChallengeStatus.AWAITING);
        serviceEntityInit.save(chalUnstance2);

        User user2 = new User();
        user2.setName("Jonnie Fast-Foot");
        user2.setImageRef("AvaDefault.jpg");
        serviceEntityInit.save(user2);

        User user3 = new User();
        user3.setName("Annet Fast-Food");
        user3.setImageRef("AvaDefault.jpg");
        serviceEntityInit.save(user3);
    }
}
