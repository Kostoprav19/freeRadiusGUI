package lv.freeradiusgui.dao.userDAO;

import lv.freeradiusgui.domain.User;

import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface UserDAO {

    boolean store(User user);

    User getById(Long id);

    List<User> getAll();

    List<User> getAllByCriteria(String fieldName, Object object);

    void delete(User user);

    Long getCount();

}
