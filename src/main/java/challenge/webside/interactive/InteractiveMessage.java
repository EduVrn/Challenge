package challenge.webside.interactive;

import java.util.Date;

public class InteractiveMessage {
    //private String name;
    Integer idParent; 
	Integer mainObjectId;
	Integer userId;
	Integer messageId;
	String userName;
	String type;
	String messageContent;
	Date date;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getIdParent() {
		return idParent;
	}
	public void setIdParent(Integer idParent) {
		this.idParent = idParent;
	}
	
	public Integer getMainObjectId() {
		return mainObjectId;
	}
	public void setMainObjectId(Integer mainObjectId) {
		this.mainObjectId = mainObjectId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getMessageId() {
		return messageId;
	}
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

}
