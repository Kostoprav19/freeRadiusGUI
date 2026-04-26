package lv.freeradiusgui.repositories;

import lv.freeradiusgui.domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Integer> {

    Role findByName(String name);
}
