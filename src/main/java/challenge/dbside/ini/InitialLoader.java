package challenge.dbside.ini;

import challenge.dbside.models.*;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.ini.TypeAttribute;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;
import challenge.dbside.models.status.ChallengeDefinitionStatus;
import challenge.dbside.models.status.ChallengeStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import challenge.dbside.services.ini.MediaService;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

		
		//TODO: remove bidirectional copies
		TypeOfAttribute refAttrAcceptorChallengeInstance = new TypeOfAttribute(30, "acceptorChalInstance", TypeAttribute.REF_ONE_DIRECTIONAL.getValue());
		TypeOfAttribute refAttrChallengeInstances = new TypeOfAttribute(31, "challengeInstances", TypeAttribute.REF_ONE_DIRECTIONAL.getValue());
		TypeOfAttribute refAttrCreatedChallenges = new TypeOfAttribute(32, "createdChallenges", TypeAttribute.REF_ONE_DIRECTIONAL.getValue());
		TypeOfAttribute refAttrAcceptedChallengeInstances = new TypeOfAttribute(33, "acceptedChalIns", TypeAttribute.REF_ONE_DIRECTIONAL.getValue());
		TypeOfAttribute refAttrFriends = new TypeOfAttribute(34, "friends", TypeAttribute.REF_ONE_DIRECTIONAL.getValue());
		TypeOfAttribute refAttrAutorComment = new TypeOfAttribute(35, "autorComment", TypeAttribute.REF_ONE_DIRECTIONAL.getValue());

		// MediaServiceTypeOfAttribute serviceAttr = (MediaServiceTypeOfAttribute) context.getBean("storageServiceTypeOfAttribute");

		serviceAttr.save(attrName);
		serviceAttr.save(attrSurname);
		serviceAttr.save(attrDate);
		serviceAttr.save(attrDescription);
		serviceAttr.save(attrImageRef);
		serviceAttr.save(attrChalStatus);
		serviceAttr.save(attrChalDefStatus);
		serviceAttr.save(attrMessage);
		serviceAttr.save(refAttrAcceptorChallengeInstance);
		serviceAttr.save(refAttrChallengeInstances);
		serviceAttr.save(refAttrCreatedChallenges);
		serviceAttr.save(refAttrAcceptedChallengeInstances);
		serviceAttr.save(refAttrFriends);
		serviceAttr.save(refAttrAutorComment);

		// MediaServiceTypeOfEntity serviceEntity = (MediaServiceTypeOfEntity) context.getBean("storageServiceTypeOfEntity");
		TypeOfEntity entity = new TypeOfEntity("User");
		entity.add(attrName);
		entity.add(attrSurname);
		entity.add(attrImageRef);

		entity.add(refAttrCreatedChallenges);
		entity.add(refAttrAcceptedChallengeInstances);
		entity.add(refAttrFriends);
		serviceEntity.save(entity);

		TypeOfEntity entityChallenge = new TypeOfEntity("ChallengeDefinition");
		entityChallenge.add(attrName);
		entityChallenge.add(attrDate);
		entityChallenge.add(attrDescription);
		entityChallenge.add(attrImageRef);
		entityChallenge.add(attrChalDefStatus);

		entityChallenge.add(refAttrChallengeInstances);
		serviceEntity.save(entityChallenge);

		TypeOfEntity entityChallengeInstance = new TypeOfEntity("ChallengeInstance");
		entityChallengeInstance.add(attrName);
		entityChallengeInstance.add(attrChalStatus);

		entityChallengeInstance.add(refAttrAcceptorChallengeInstance);
		serviceEntity.save(entityChallengeInstance);

		TypeOfEntity entityComment = new TypeOfEntity("Comment");
		entityComment.add(attrDate);
		entityComment.add(attrMessage);

		entityComment.add(refAttrAutorComment);
		//
		serviceEntity.save(entityComment);


		ContextType contextType = ContextType.getInstance();

		contextType.add(attrName);
		contextType.add(attrSurname);
		contextType.add(attrDate);
		contextType.add(attrDescription);//!!!!
		contextType.add(attrImageRef);
		contextType.add(attrChalStatus);
		contextType.add(attrChalDefStatus);
		contextType.add(attrMessage);

		contextType.add(refAttrAcceptorChallengeInstance);
		contextType.add(refAttrChallengeInstances);
		contextType.add(refAttrCreatedChallenges);
		contextType.add(refAttrAcceptedChallengeInstances);
		contextType.add(refAttrFriends);
		contextType.add(refAttrAutorComment);



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
		chalDef1.setStatus(ChallengeDefinitionStatus.CREATED);
		chalDef1.setDate(new Date());

		serviceEntityInit.save(chalDef1);


		User user1 = new User();
		user1.setName("Evgeniy 1");
		user1.setImageRef("AvaDefault.jpg");
		serviceEntityInit.save(user1);

		ChallengeDefinition chalDef2 = new ChallengeDefinition();
		chalDef2.setName("Hi, make your's task 4 Ivan.");
		chalDef2.setDescription("After (may be)");
		chalDef2.setImageRef("secondExampleTask.png");
		chalDef2.setDate(new Date());
		chalDef2.setStatus(ChallengeDefinitionStatus.CREATED);
		serviceEntityInit.save(chalDef2);
		user1.addChallenge(chalDef1);
		user1.addChallenge(chalDef2);

		Comment com = new Comment();
		com.setMessage("asg");
		com.setDate(new Date());
		serviceEntityInit.save(com);
		

		serviceEntityInit.update(user1);
		
		
		
		try {
			User usertest = (User)serviceEntityInit.findById(user1.getId(), User.class);
			
			List list = usertest.getChallenges();
			Integer count = list.size();
			
			System.out.println(count);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		

		try {
			ChallengeDefinition chalDef3 = (ChallengeDefinition)serviceEntityInit.findById(chalDef1.getId(), ChallengeDefinition.class);
			DBSource userC = chalDef3.getDataSource().getParent();
			
			User usertt = chalDef3.getCreator();
			
			String n = usertt.getName();
			System.out.println("");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		
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



		try {
			User usertest = (User)serviceEntityInit.findById(user1.getId(), User.class);
			
			List list = usertest.getChallenges();
			Integer count = list.size();
			
			System.out.println(count);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		

		try {
			ChallengeDefinition chalDef3 = (ChallengeDefinition)serviceEntityInit.findById(chalDef1.getId(), ChallengeDefinition.class);
			DBSource userC = chalDef3.getDataSource().getParent();
			
			User usertt = chalDef3.getCreator();
			
			String n = usertt.getName();
			System.out.println("");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		
	}
}
