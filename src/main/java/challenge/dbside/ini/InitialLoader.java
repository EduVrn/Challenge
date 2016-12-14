package challenge.dbside.ini;

import challenge.dbside.models.User;
import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;
import challenge.dbside.services.ini.MediaServiceTypeOfAttribute;
import challenge.dbside.services.ini.MediaServiceTypeOfEntity;
import challenge.dbside.services.ini.MediaServiceEntity;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Component;

@Component
public class InitialLoader {

    @Autowired
    @Qualifier("storageServiceTypeOfAttribute")
    private MediaServiceTypeOfAttribute serviceAttr;

    @Autowired
    @Qualifier("storageServiceTypeOfEntity")
    private MediaServiceTypeOfEntity serviceEntity;

    @Autowired
    @Qualifier("storageServiceUser")
    private MediaServiceEntity serviceEntityInit;

    public void initial() {
        //try load from base

        //else 
        //create
        createContext();
        init();
        //return null;
    }

    private void createContext() {

        TypeOfAttribute attrName = new TypeOfAttribute(1, "name", 1);
        TypeOfAttribute attrSurname = new TypeOfAttribute(2, "surname", 1);
        TypeOfAttribute attrDate = new TypeOfAttribute(3, "date", 2);
        TypeOfAttribute attrDescription = new TypeOfAttribute(4, "description", 1);
        TypeOfAttribute attrImageRef = new TypeOfAttribute(5, "imageref", 1);
        // MediaServiceTypeOfAttribute serviceAttr = (MediaServiceTypeOfAttribute) context.getBean("storageServiceTypeOfAttribute");

        serviceAttr.save(attrName);
        serviceAttr.save(attrSurname);
        serviceAttr.save(attrDate);
        serviceAttr.save(attrDescription);
        serviceAttr.save(attrImageRef);

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
        serviceEntity.save(entityChallenge);

        ContextType contextType = ContextType.getInstance();

        contextType.add(attrName);
        contextType.add(attrSurname);
        contextType.add(attrDate);
        contextType.add(attrDescription);//!!!!
        contextType.add(attrImageRef);
        
        
        contextType.add(entity);
        contextType.add(entityChallenge);

    }

    public void init() {

        // MediaServiceEntity serviceEntityInit = (MediaServiceEntity) context.getBean("storageServiceUser");
        /*Example entity*/
        ChallengeDefinition chal1 = new ChallengeDefinition();
        chal1.setName("TestChallenge");
        chal1.setDescription("hella awesome");
        chal1.setImageRef("images/race.jpg");
        serviceEntityInit.save(chal1);
        User user1 = new User();
        serviceEntityInit.save(user1);

        User user2 = new User();
        user2.setName("name2");
        serviceEntityInit.save(user2);

        User user3 = new User();
        user3.setName("name3");
        serviceEntityInit.save(user3);

        User user4 = new User();
        user4.setName("name4");
        serviceEntityInit.save(user4);

        System.out.println("\n\nUpdate");
        user1.setName("new_name");
        //user1.setParent(user2);
        serviceEntityInit.update(user1);

        user4.setName("new_name4");
        //user4.setParent(user2);
        serviceEntityInit.update(user4);

        Set userSet = new HashSet();
        userSet.add(user1);
        userSet.add(user4);
        user2.setChildren(userSet);
        serviceEntityInit.update(user2);

    }

}
