package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.roleDAO.RoleDAO;
import lv.freeradiusgui.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 25.05.2016.
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Override
    public List<Role> getAll() {
        /*List<Role> list = new ArrayList<>();
        list.add(new Role(Role.ROLE_ADMIN));
        list.add(new Role(Role.ROLE_USER));
        or
        List<String> list = new ArrayList<>();
        list.add(Role.ROLE_ADMIN);
        list.add(Role.ROLE_USER);*/

        return roleDAO.getAll();
    }
}
