package lv.freeradiusgui.services;


import lv.freeradiusgui.dao.userDAO.UserDAO;
import lv.freeradiusgui.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;

    @Override
    public boolean store(User user) {
        return userDAO.store(user);
    }

    @Override
    public User getById(Long id) {
        return userDAO.getById(id);
    }

    @Override
    public List<User> getAll() {
        return userDAO.getAll();
    }

    @Override
    public List<User> getAllByCriteria(String fieldName, Object object) {
        return userDAO.getAllByCriteria(fieldName, object);
    }

    @Override
    public void delete(User user) {
        userDAO.delete(user);
    }

    @Override
    public Long getCount() {
        return userDAO.getCount();
    }
}
