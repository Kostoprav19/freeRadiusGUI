package lv.freeradiusgui.repositories;

import java.util.Collection;
import java.util.List;
import lv.freeradiusgui.domain.Device;
import org.springframework.data.repository.CrudRepository;

public interface DeviceRepository extends CrudRepository<Device, Integer> {

    Device findByMac(String mac);

    List<Device> findByMacIn(Collection<String> macs);

    List<Device> findByPortSpeed(Integer portSpeed);
}
