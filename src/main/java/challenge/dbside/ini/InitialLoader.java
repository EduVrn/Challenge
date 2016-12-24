package challenge.dbside.ini;

import challenge.dbside.models.BaseEntity;
import challenge.dbside.models.User;
import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.ChallengeStatus;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import challenge.dbside.services.ini.MediaService;
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
        // MediaServiceTypeOfAttribute serviceAttr = (MediaServiceTypeOfAttribute) context.getBean("storageServiceTypeOfAttribute");

        serviceAttr.save(attrName);
        serviceAttr.save(attrSurname);
        serviceAttr.save(attrDate);
        serviceAttr.save(attrDescription);
        serviceAttr.save(attrImageRef);
        serviceAttr.save(attrChalStatus);
        serviceAttr.save(attrChalDefStatus);

        // MediaServiceTypeOfEntity serviceEntity = (MediaServiceTypeOfEntity) context.getBean("storageServiceTypeOfEntity");
        TypeOfEntity entity = new TypeOfEntity("User");
        entity.add(attrName);
        entity.add(attrSurname);
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

        ContextType contextType = ContextType.getInstance();

        contextType.add(attrName);
        contextType.add(attrSurname);
        contextType.add(attrDate);
        contextType.add(attrDescription);//!!!!
        contextType.add(attrImageRef);
        contextType.add(attrChalStatus);
        contextType.add(attrChalDefStatus);

        contextType.add(entity);
        contextType.add(entityChallenge);
        contextType.add(entityChallengeInstance);

    }

    public void init() {
        ChallengeDefinition chalDef1 = new ChallengeDefinition();
        chalDef1.setName("TestChallenge");
        chalDef1.setDescription("hella awesome");
        chalDef1.setImageRef("race.jpg");
        
        serviceEntityInit.save(chalDef1);
        User user1 = new User();
        user1.setName("User 1");
        serviceEntityInit.save(user1);

        ChallengeDefinition chalDef2 = new ChallengeDefinition();
        chalDef2.setName("TestofChalleng");
        chalDef2.setDescription("It's ok");
        chalDef2.setImageRef("onTheEdge.jpeg");
        serviceEntityInit.save(chalDef2);
        user1.addChallenge(chalDef1);
        user1.addChallenge(chalDef2);
        serviceEntityInit.update(user1);
        ChallengeInstance chalInstance1 = new ChallengeInstance();
        chalInstance1.setName("Instance N1");
        chalInstance1.setStatus(ChallengeStatus.AWAITING);
        serviceEntityInit.save(chalInstance1);
        ChallengeInstance chalUnstance2 = new ChallengeInstance();
        chalUnstance2.setName("Instance N2");
        chalUnstance2.setStatus(ChallengeStatus.AWAITING);
        serviceEntityInit.save(chalUnstance2);
        /*  Set set = new HashSet();
        set.add(chalInstance1);
        set.add(chalUnstance2);
        chalDef2.setChildren(set);
        chalInstance1.setParent(chalDef2);
        chalUnstance2.setParent(chalDef2);
        serviceEntityInit.update(chalDef2);

        user1.addAcceptedChallenge(chalInstance1);
        user1.addAcceptedChallenge(chalUnstance2);
       serviceEntityInit.update(user1);

        User user2 = new User();
        // serviceEntityInit.save(user2);
        user2.setName("user2");
//        user2.addAcceptedChallenge(chal1);
        //      user2.addAcceptedChallenge(chal2);
        user2.addFriend(user1);
        //   user2.addAcceptedChallenge(chal2);
        // user2.addChallenge(chal2);
//        serviceEntityInit.save(user2);

        User user3 = new User();
        user3.setName("user3");
        serviceEntityInit.save(user3);

        User user4 = new User();
        user4.setName("name4");
        serviceEntityInit.save(user4);

        user1.setName("User1");
       // serviceEntityInit.update(user1);


       /* Set userSet = new HashSet();
        userSet.add(user1);
        userSet.add(user4);
        user2.setChildren(userSet);
        user1.setParent(user2);
        user4.setParent(user2); 
        serviceEntityInit.update(user2);*/
 /*    for (BaseEntity obj : chalDef2.getChildren()) {
            System.out.println(((ChallengeInstance) obj).getAcceptor().getName());
        }

        user1.getAcceptedChallenges().forEach((c) -> {
            System.out.println(((ChallengeInstance) c).getName());
        });
         */
    }
}
