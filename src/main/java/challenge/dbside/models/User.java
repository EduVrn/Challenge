package challenge.dbside.models;

import java.util.*;

import javax.persistence.*;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.Attribute;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.ini.TypeOfAttribute;

import org.hibernate.annotations.Where;

public class User extends BaseEntity {

	public User() {
		super(User.class.getSimpleName());
	}

	public User(DBSource dataSource) {
		super(dataSource);
	}

	/*
    //TODO: EAGER fetch type
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Where(clause = "type_of_entity = 1")
    private List<User> friends;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "type_of_entity = 4")
    private List<Comment> comments;*/

	public List<Comment> getComments() {
		return null;
		//return comments;
	}

	public void setComments(List<Comment> comments) {

		//this.comments = comments;
	}

	public void addComment(Comment comment) {
		//comments.add(comment);
		//comment.setAuthor(this);
	}

	public void setFriends(List<User> users) {
		//this.friends = users;
	}

	public List<User> getFriends() {
		return null;
		//return this.friends;
	}

	public void addFriend(User user) {
		//friends.add(user);
	}

	public String getName() {
		return getDataSourse().getAttributes().get(IdAttrGet.IdName()).getValue();
	}

	public void setName(String name) {
		getDataSourse().getAttributes().get(IdAttrGet.IdName()).setValue(name);
	}

	public void addChallenge(ChallengeDefinition chal) {
		chal.setCreator(this);
		chal.setStatus(ChallengeDefinitionStatus.CREATED);
		getDataSourse().getRelations().put(IdAttrGet.refCreatedChal(), chal.getDataSourse());
	}

	public List<ChallengeDefinition> getChallenges() {
		return (List<ChallengeDefinition>) getDataSourse().getRelations().get(IdAttrGet.refCreatedChal());
	}

	public void addAcceptedChallenge(ChallengeInstance chal) {
		chal.setAcceptor(this);        
		getDataSourse().getRelations()
		.put(IdAttrGet.refAcceptedChalIns(), chal.getDataSourse());
	}


	public List<ChallengeInstance> getAcceptedChallenges() {
		List<ChallengeInstance> accepted = new ArrayList<>();

		((List<DBSource>)getDataSourse().getRelations()
				.get(IdAttrGet.refAcceptedChalIns())).forEach((chalInsDB) ->{
					ChallengeInstance ch = new ChallengeInstance(chalInsDB);
					if(ch.getStatus() == ChallengeStatus.ACCEPTED) {
						accepted.add(ch);
					}
				});
		return accepted;
	}

	public List<ChallengeInstance> getChallengeRequests() {
		List<ChallengeInstance> requests = new ArrayList<>();

		((List<DBSource>)getDataSourse().getRelations()
				.get(IdAttrGet.refAcceptedChalIns())).forEach((chalInsDB) ->{
					ChallengeInstance ch = new ChallengeInstance(chalInsDB);
					if(ch.getStatus() == ChallengeStatus.AWAITING) {
						requests.add(ch);
					}
				});
		return requests;
	}

	public void acceptChallenge(ChallengeInstance chal) {
		List<ChallengeInstance> requests = getChallengeRequests();
		if (requests.contains(chal)) {
			chal.setStatus(ChallengeStatus.ACCEPTED);
			chal.setAcceptor(this);
		}
	}

	public void declineChallenge(ChallengeInstance chal) {
		List<ChallengeInstance> requests = getChallengeRequests();		
		if (requests.contains(chal)) {
			getDataSourse().getRelations().remove(IdAttrGet.refAcceptedChalIns(), chal.getDataSourse());			
            chal.setAcceptor(null);
        }
	}

	@Override
	public String toString() {
		String entityInfo = super.toString();
		StringBuilder info = new StringBuilder();
		info.append(entityInfo);
		info.append("\nFriends: \n");
		
		/*friends.forEach((u) -> {
            info.append("\nid: ").append(u.getId()).append(" name:").append(u.getName());
        });
        info.append("\nChallenges: \n");*/
        /*listOfChallenges.forEach((c) -> {
            info.append("\nid: ").append(c.getId()).append(" name: ").append(c.getName());
        });*/
		return info.toString();
	}

	public void setImageRef(String imageRef) {
		getDataSourse().getAttributes().get(IdAttrGet.IdImgRef()).setValue(imageRef);
	}

	public String getImageRef() {
		return "../images/" + getDataSourse().getAttributes().get(IdAttrGet.IdImgRef()).getValue();
	}

}
