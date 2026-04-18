package lv.freeradiusgui.services;

import java.util.List;
import lv.freeradiusgui.dao.roleDAO.RoleDAO;
import lv.freeradiusgui.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired private RoleDAO roleDAO;

    @Override
    public List<Role> getAll() {
        return roleDAO.getAll();
    }

    @Override
    public Role getByName(String name) {
        return roleDAO.getByName(name);
    }

    @Override
    public Role getById(Integer id) {
        return roleDAO.getById(id);
    }
}
