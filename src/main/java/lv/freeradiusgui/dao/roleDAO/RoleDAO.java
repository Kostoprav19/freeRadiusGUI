package lv.freeradiusgui.dao.roleDAO;

import java.util.List;
import lv.freeradiusgui.domain.Role;

public interface RoleDAO {
  boolean store(Role role);

  Role getById(Integer id);

  Role getByName(String name);

  List<Role> getAll();
}
