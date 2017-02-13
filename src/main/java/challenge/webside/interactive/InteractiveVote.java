package challenge.webside.interactive;

public class InteractiveVote {
	
	Integer idMessage;
	Integer mainObjectId;
	String type;
	boolean isDown;
	Integer status;
	
	public InteractiveVote() {
		status = 0;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getIdMessage() {
		return idMessage;
	}
	public void setIdMessage(Integer idMessage) {
		this.idMessage = idMessage;
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
	public boolean isDown() {
		return isDown;
	}
	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}
}
