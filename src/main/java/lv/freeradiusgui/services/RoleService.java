package lv.freeradiusgui.services;

import java.util.List;
import lv.freeradiusgui.domain.Role;

public interface RoleService {
  List<Role> getAll();

  Role getByName(String name);

  Role getById(Integer id);
}
