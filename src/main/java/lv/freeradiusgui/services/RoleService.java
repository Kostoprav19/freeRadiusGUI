package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Role;

import java.util.List;

/**
 * Created by Dan on 25.05.2016.
 */
public interface RoleService {

    List<Role> getAll();

    Role getByName(String name);

    Role getById(Integer id);
}
