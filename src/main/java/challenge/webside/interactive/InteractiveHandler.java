package challenge.webside.interactive;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.springframework.stereotype.Component;

@Component
public class InteractiveHandler {

	private MultiMap storageConnections;
	
	public InteractiveHandler() {
		storageConnections = new MultiValueMap();
	}
	
	public void addConnection(Integer mainObjectId, String userId) {
		storageConnections.put(mainObjectId, userId);
	}
	
	public void rmConnection(Integer mainObjectId, String userId) {
		storageConnections.remove(mainObjectId, userId);
	}
	
	public Set<String> getConnection4Object(Integer mainObjectId) {
		Collection c = (Collection)storageConnections.get(mainObjectId);
		Set<String> l = new HashSet(); 
		if(c != null) {
			l.addAll(c);
		}		
		return l; 
	}
	
}
