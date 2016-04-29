package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.User;

import java.util.List;

public interface UserService {

    boolean store(User user);

    User getById(Long id);

    List<User> getAll();

    List<User> getAllByCriteria(String fieldName, Object object);

    void delete(User user);

    Long getCount();

}


