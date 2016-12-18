package challenge.dbside.services.ini;

import java.util.List;

public interface MediaService<E> {

    public void save(E entity);

    public List<E> getAll(Class<E> classType);
    
    public void update(E entity);
    
    public void delete(E entity);

    public E findById(Integer id, Class<E> classType);
    
}
