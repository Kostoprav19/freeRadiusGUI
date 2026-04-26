package lv.freeradiusgui.repositories;

import lv.freeradiusgui.domain.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    Account findByLogin(String login);
}
