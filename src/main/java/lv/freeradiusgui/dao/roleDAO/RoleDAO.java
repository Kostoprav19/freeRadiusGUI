package lv.freeradiusgui.dao.roleDAO;

import lv.freeradiusgui.domain.Role;

import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface RoleDAO {

    boolean store(Role role);

    Role getById(Integer id);

    Role getByName(String name);

    List<Role> getAll();


}
