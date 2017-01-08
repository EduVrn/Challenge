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
	
	//Ref \/
	
	static public Integer refAcceptedChalIns() {
		return ContextType.getInstance().getTypeAttribute("acceptedChalIns").getId();
	}
	
	static public Integer refAcceptorChalIns() {
		return ContextType.getInstance().getTypeAttribute("acceptorChalInstance").getId();
	}
	
	static public Integer refCreatedChal() {
		return ContextType.getInstance().getTypeAttribute("createdChallenges").getId();
	}
	
	static public Integer refChalIns() {
		return ContextType.getInstance().getTypeAttribute("challengeInstances").getId();
	}
	
	static public Integer refFriend() {
		return ContextType.getInstance().getTypeAttribute("friends").getId();
	}
	
	static public Integer refAutorComment() {
		return ContextType.getInstance().getTypeAttribute("autorComment").getId();
	}

}
