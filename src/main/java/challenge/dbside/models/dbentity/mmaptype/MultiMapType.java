package challenge.dbside.models.dbentity.mmaptype;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

//import org.apache.commons.collections.MultiHashMap;
//import org.apache.commons.collections.MultiMap;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.usertype.UserCollectionType;

public class MultiMapType implements UserCollectionType {

	public boolean contains(Object collection, Object entity) {
		return ((MultiMap) collection).containsValue(entity);
	}

	public Iterator getElementsIterator(Object collection) {
		return ((MultiMap) collection).values().iterator();
	}

	public Object indexOf(Object collection, Object entity) {
		for (Iterator i = ((MultiMap) collection).entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();    
			Collection value = (Collection) entry.getValue();
			if (value.contains(entity)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Object instantiate() {
		return new MultiValueMap();
	}

	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister) throws HibernateException {
		return new PersistentMultiMap(session);
	}

	public PersistentCollection wrap(SessionImplementor session, Object collection) {
		if(collection instanceof MultiMap) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!1");
		}
		PersistentCollection pc = new PersistentMultiMap(session, (MultiMap) collection); 
		
		return pc;
	}

	public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner, Map copyCache, SessionImplementor session) throws HibernateException {

		MultiMap result = (MultiMap) target;
		result.clear();

		Iterator iter = ( (java.util.Map) original ).entrySet().iterator();
		while ( iter.hasNext() ) {
			java.util.Map.Entry me = (java.util.Map.Entry) iter.next();
			Object key = persister.getIndexType().replace( me.getKey(), null, session, owner, copyCache );
			Collection collection = (Collection) me.getValue();
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				Object value = persister.getElementType().replace( iterator.next(), null, session, owner, copyCache );
				result.put(key, value);
			}
		}

		return result;
	}

	public Object instantiate(int anticipatedSize) {
		return new MultiValueMap();
	}



}
