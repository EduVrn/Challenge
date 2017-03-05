package challenge.dbside.models.common;

import challenge.dbside.ini.ContextType;

public class IdAttrGet {

    static public Integer IdName() {
        return ContextType.getInstance().getTypeAttribute("name").getId();
    }

    static public Integer IdDescr() {
        return ContextType.getInstance().getTypeAttribute("description").getId();
    }

    static public Integer IdChalStat() {
        return ContextType.getInstance().getTypeAttribute("chalStatus").getId();
    }

    static public Integer IdChalDefStat() {
        return ContextType.getInstance().getTypeAttribute("chalDefStatus").getId();
    }

    static public Integer IdImgRef() {
        return ContextType.getInstance().getTypeAttribute("imageref").getId();
    }

    static public Integer IdDate() {
        return ContextType.getInstance().getTypeAttribute("date").getId();
    }

    static public Integer IdMessage() {
        return ContextType.getInstance().getTypeAttribute("message").getId();
    }

    static public Integer IdIsMain() {
        return ContextType.getInstance().getTypeAttribute("isMain").getId();
    }

    static public Integer IdClosingDate() {
        return ContextType.getInstance().getTypeAttribute("closingDate").getId();
    }

    static public Integer IdRating() {
        return ContextType.getInstance().getTypeAttribute("rating").getId();
    }

    //Ref \/
    static public Integer refAcChalIns() {
        return ContextType.getInstance().getTypeAttribute("acceptedChalIns").getId();
    }

    static public Integer refFriend() {
        return ContextType.getInstance().getTypeAttribute("friends").getId();
    }

    static public Integer refSubscriber() {
        return ContextType.getInstance().getTypeAttribute("subscriber").getId();
    }

    static public Integer refVoteFor() {
        return ContextType.getInstance().getTypeAttribute("voteFor").getId();
    }

    static public Integer refVoteAgainst() {
        return ContextType.getInstance().getTypeAttribute("voteAgainst").getId();
    }

    static public Integer refVoteForComment() {
        return ContextType.getInstance().getTypeAttribute("voteForComment").getId();
    }

    static public Integer refVoteAgainstComment() {
        return ContextType.getInstance().getTypeAttribute("voteAgainstComment").getId();
    }

    static public Integer refAutorCom() {
        return ContextType.getInstance().getTypeAttribute("autorComment").getId();
    }

    static public Integer refChallengeDefTag() {
        return ContextType.getInstance().getTypeAttribute("challengeDefinitionTag").getId();
    }

    static public Integer refRequestSender() {
        return ContextType.getInstance().getTypeAttribute("requestSender").getId();
    }

    static public Integer refRequestReceiver() {
        return ContextType.getInstance().getTypeAttribute("requestReceiver").getId();
    }

    static public Integer refRequestSubject() {
        return ContextType.getInstance().getTypeAttribute("requestSubject").getId();
    }

}
