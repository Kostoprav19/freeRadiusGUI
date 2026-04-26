package lv.freeradiusgui.repositories;

import lv.freeradiusgui.domain.Switch;
import org.springframework.data.repository.CrudRepository;

public interface SwitchRepository extends CrudRepository<Switch, Integer> {

    Switch findByIp(String ip);
}
