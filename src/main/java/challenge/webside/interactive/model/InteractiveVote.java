package challenge.webside.interactive.model;

public class InteractiveVote extends BaseMessage {

    Integer idOwner;
    Integer changeVote;

    public Integer getChangeVote() {
        return changeVote;
    }

    public void setChangeVote(Integer changeVote) {
        this.changeVote = changeVote;
    }

    public Integer getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(Integer idOwner) {
        this.idOwner = idOwner;
    }
}
