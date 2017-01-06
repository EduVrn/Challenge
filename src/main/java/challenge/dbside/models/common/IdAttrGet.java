package challenge.dbside.models.common;

import challenge.dbside.ini.ContextType;

public class IdAttrGet {

	static public Integer IdName() {
		return ContextType.getInstance().getTypeAttribute("name").getId();
	}
	
	static public Integer IdChalStat() {
		return ContextType.getInstance().getTypeAttribute("chalStatus").getId();
	}
	
	static public Integer IdImgRef() {
		return ContextType.getInstance().getTypeAttribute("imageref").getId();
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
	
}
