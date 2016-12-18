package challenge.dbside.dao.ini;

import java.util.List;

public interface MediaDao<E> {

    public void save(E entity);

    public List<E> getAll(Class<E> classType);

    public void delete(E entity);

    public void update(E entity);

    public E findById(Integer id, Class<E> classType);
}
