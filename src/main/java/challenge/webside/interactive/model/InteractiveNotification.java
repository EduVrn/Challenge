package challenge.webside.interactive.model;

public class InteractiveNotification extends BaseMessage {
	String typeNotification;
	String description;
	String body;
	
	public InteractiveNotification(Integer userId) {
		super(userId);
		
	}
	
	public String getTypeNotification() {
		return typeNotification;
	}
	public void setTypeNotification(String typeNotification) {
		this.typeNotification = typeNotification;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
