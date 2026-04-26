package lv.freeradiusgui.repositories;

import java.time.LocalDateTime;
import java.util.List;
import lv.freeradiusgui.domain.Log;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<Log, Integer> {

    List<Log> findByTimeOfRegistrationGreaterThanEqualAndTimeOfRegistrationLessThanOrderByIdDesc(
            LocalDateTime start, LocalDateTime end);
}
