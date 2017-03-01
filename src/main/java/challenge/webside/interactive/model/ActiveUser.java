package challenge.webside.interactive.model;

import java.util.Date;

public class ActiveUser {

    private String userId;
    private Date date;
    private Integer entryNumber;
    private Integer lastNumber;

    public ActiveUser() {
    }

    public ActiveUser(String userId, Date date, Integer entryNumber, Integer lastNumber) {
        this.userId = userId;
        this.date = date;
        this.entryNumber = entryNumber;
        this.lastNumber = lastNumber;
    }

    public Integer getCurrentNumber() {
        return entryNumber + lastNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(Integer entryNumber) {
        this.entryNumber = entryNumber;
    }

    public Integer getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(Integer lastNumber) {
        this.lastNumber = lastNumber;
    }

    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }

        ActiveUser otherObj = (ActiveUser) other;
        return this.userId.equals(otherObj.userId);
    }

}
