package lv.freeradiusgui.dao.deviceDAO;

import lv.freeradiusgui.config.WebMVCConfig;
import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.domain.Switch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Dan on 23.04.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMVCConfig.class)
@Transactional
@Rollback
public class DeviceDAOImplTest {

    @Autowired
    private DeviceDAO deviceDAO;

    private Device device1;
    private Device device2;
    private Optional<Device> deviceOptional1;
    private Optional<Device> deviceOptional2;


    @Before
    public void before() {

        //setForeignKeyCheckTo(0);


        device1 = new Device.DeviceBuilder()
                .withMac("0000000000000001")
                .withName("Test device 1")
                .withDescription("Test device")
                .onSwitch(new Switch())
                .onPort(1)
                .withPortSpeed(100)
                .withTOR(LocalDateTime.of(2016, 1, 1, 10, 00))
                .lastSeen(LocalDateTime.of(2016, 4, 1, 12, 30))
                .withAccess(1)
                .build();

        device2 = new Device.DeviceBuilder()
                .withMac("0000000000000002")
                .withName("Test device 2")
                .withDescription("Test device")
                .onSwitch(new Switch())
                .onPort(2)
                .withPortSpeed(1000)
                .withTOR(LocalDateTime.of(2016, 1, 1, 10, 01))
                .lastSeen(LocalDateTime.of(2016, 4, 1, 12, 40))
                .withAccess(1)
                .build();

        deviceOptional1 = Optional.of(device1);
        deviceOptional2 = Optional.of(device2);
    }

    @After
    public void after(){
        //setForeignKeyCheckTo(1);
    }

    @Test
    public void store() throws Exception {
        assertTrue(deviceDAO.store(deviceOptional1));

        Device stored = deviceDAO.getById(device1.getId()).get();

        assertEquals(device1.getName(), stored.getName());
        assertEquals(device1.getMac(), stored.getMac());
        assertEquals(device1.getDescription(), stored.getDescription());
        assertEquals(device1.getSwitch(), stored.getSwitch());
        assertEquals(device1.getSwitchPort(), stored.getSwitchPort());
        assertEquals(device1.getPortSpeed(), stored.getPortSpeed());
        assertEquals(device1.getTimeOfRegistration(), stored.getTimeOfRegistration());
        assertEquals(device1.getLastSeen(), stored.getLastSeen());
        assertEquals(device1.getAccess(), stored.getAccess());
    }

    @Test
    public void getById() throws Exception {

    }

}