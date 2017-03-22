package challenge.webside.interactive.model;

public class InteractiveComment extends BaseMessage {

    Integer idParent;
    Integer messageId;
    String userName;
    private String avatarImage64;
    String messageContent;
    private Integer authorId;
    private String toWhom;

    public Integer getIdParent() {
        return idParent;
    }

    public void setIdParent(Integer idParent) {
        this.idParent = idParent;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getToWhom() {
        return toWhom;
    }

    public void setToWhom(String toWhom) {
        this.toWhom = toWhom;
    }

    public String getAvatarImage() {
        return avatarImage64;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage64 = avatarImage;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
}
