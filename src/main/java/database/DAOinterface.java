package database;

import java.util.List;

/**
 * Created by Daniels on 20.10.2015..
 */

public interface DAOinterface<T> {

    void create(T obj) throws DBException;

    T getById(Long id) throws DBException;

    void delete(Long id) throws DBException;

    void update(T obj) throws DBException;

    List<T> getAll() throws DBException;

    List<T> filter(String criteria) throws DBException;

    T getByName(String name) throws DBException;

    int getCount() throws DBException;

}
