package challenge.webside.interactive.model;

import java.util.Date;

public class BaseMessage {

    Integer mainObjectId;
    Integer userId;

    String typeMain;
    String typeAnswer;
    String status;
    Date date;

    public BaseMessage() {
    }

    public BaseMessage(Integer userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getMainObjectId() {
        return mainObjectId;
    }

    public void setMainObjectId(Integer mainObjectId) {
        this.mainObjectId = mainObjectId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTypeAnswer() {
        return typeAnswer;
    }

    public void setTypeAnswer(String typeAnswer) {
        this.typeAnswer = typeAnswer;
    }

    public String getTypeMain() {
        return typeMain;
    }

    public void setTypeMain(String typeMain) {
        this.typeMain = typeMain;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
