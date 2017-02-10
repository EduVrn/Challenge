package challenge.dbside.models.dbentity.mmaptype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;




/*import org.apache.commons.collections.DefaultMapEntry;
import org.apache.commons.collections.MultiHashMap;

import org.apache.commons.collections.MultiMap;*/


import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;



import org.hibernate.HibernateException;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.Type;

import com.google.common.collect.ListMultimap;

import challenge.dbside.models.dbentity.DBSource;

@SuppressWarnings( "unchecked" )
//@SuppressWarnings("serial")
public class PersistentMultiMap extends PersistentMap implements MultiMap {

	public PersistentMultiMap(SessionImplementor session, MultiMap map) {
        super(session, map);
    }

    public PersistentMultiMap(SessionImplementor session) {
        super(session);
    }

    
    /*public Object remove(Object key, Object item) {
        Object old = isPutQueueEnabled() ? readElementByIndex(key) : UNKNOWN;
        if (old == UNKNOWN) {
            write();
            return ((MultiMap) map).remove(key, item);
        } else {
            queueOperation(new RemoveItem(key, item));
            return old;
        }
    }*/

    private class RemoveItem implements DelayedOperation {

        private final Object key;

        private final Object item;

        private RemoveItem(Object key, Object item) {
            this.key = key;
            this.item = item;
        }

        public Object getAddedInstance() {
            return null;
        }

        public Object getOrphan() {
            return item;
        }

        public void operate() {
            ((ListMultimap) map).remove(key, item);
        }

    }

    @Override
    public Iterator entries(CollectionPersister persister) {
        return new KeyValueCollectionIterator(super.entries(persister));
    }

    private final static class KeyValueCollectionIterator implements Iterator {

        private final Iterator parent;

        private Object key;

        private Iterator current;

        private KeyValueCollectionIterator(Iterator parent) {
            this.parent = parent;
            move();
        }

        public boolean hasNext() {
            return key != null;
        }

        public Object next() {
            if (key == null) {
                throw new NoSuchElementException();
            } else {
            	
                DefaultMapEntry result = new DefaultMapEntry(key, current.next());
                if (!current.hasNext()) {
                    move();
                }
                return result;
            }
        }

        private void move() {
            while (this.parent.hasNext()) {
                Map.Entry entry = (Entry) this.parent.next();
                key = entry.getKey();
                current = ((Collection) entry.getValue()).iterator();
                if (current.hasNext()) {
                    return;
                }
            }
            key = null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {

    	MultiValueMap clonedMap = new MultiValueMap();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry e = (Map.Entry) iter.next();
            Collection collection = (Collection) e.getValue();
            for (Iterator i = collection.iterator(); i.hasNext();) {
                final Object copy = persister.getElementType().deepCopy(i.next(), persister.getFactory());
                clonedMap.put(e.getKey(), copy);
            }
        }
        return clonedMap;
    }

    @Override
    public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
        Map sn = (Map) getSnapshot();
        if (sn.size() != map.size())
            return false;
        Type elemType = persister.getElementType();
        for (Iterator i = sn.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Map oldState = getCollectionAsIdentityMap((Collection) entry.getValue());
            Collection newState = (Collection) map.get(entry.getKey());
            for (Iterator iter = newState.iterator(); iter.hasNext();) {
                Object newValue = iter.next();
                Object oldValue = oldState.get(newValue);
                if (newValue != null && oldValue != null && elemType.isDirty(oldValue, newValue, getSession())) {
                    return false;
                }
            }
        }
        return true;
    }

    private Map getCollectionAsIdentityMap(Collection collection) {
        Map map = new HashMap();
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            Object element = iter.next();
            map.put(element, element);
        }
        return map;
    }

    @Override
    public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
        Set result = new HashSet();
        Map sn = (Map) getSnapshot();
        for (Iterator i = sn.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Entry) i.next();
            Collection oldState = (Collection) entry.getValue();
            Collection newState = (Collection) map.get(entry.getKey());
            if(newState == null) {
            	newState = (Collection)new ArrayList();
            }
            
            for (Iterator j = oldState.iterator(); j.hasNext();) {
                Object element = j.next();
                if (!(newState.contains(element))) {
                    result.add(((DBSource)element).getId());
                }
            }
        }
        return result.iterator();
    }

    @Override
    public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
        Map.Entry e = (Entry) entry;
        Map sn = (Map) getSnapshot();
        Collection oldState = (Collection) sn.get(e.getKey());
        return oldState == null || !oldState.contains(e.getValue());
    }

    @Override
    public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
        Map.Entry e = (Entry) entry;
        Map sn = (Map) getSnapshot();
        Collection collection = (Collection) sn.get(e.getKey());
        if (collection == null) {
            return false;
        }
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            Object oldValue = iter.next();
            if (oldValue != null && oldValue.equals(e.getValue())) {
                return e.getValue() != null && elemType.isDirty(oldValue, e.getValue(), getSession());
            }
        }
        return false;
    }

	@Override
	public MapIterator mapIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeMapping(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}