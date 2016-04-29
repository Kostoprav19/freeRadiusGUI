package lv.freeradiusgui.dao.deviceDAO;

import lv.freeradiusgui.config.WebMVCConfig;
import lv.freeradiusgui.dao.switchDAO.SwitchDAO;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Dan on 23.04.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMVCConfig.class)
@Transactional
//@Rollback
public class DeviceDAOImplTest {

    @Autowired
    private DeviceDAO deviceDAO;
    @Autowired
    private SwitchDAO switchDAO;

    private Device device1;
    private Device device2;
    private Device device3;
    private Switch aSwitch1;
    

    @Before
    public void before() {

        //setForeignKeyCheckTo(0);

        aSwitch1 = new Switch.SwitchBuilder()
                .withMac("0000000000001001")
                .withName("Test switch 1")
                .withDescription("Test switch")
                .withIp("192.168.0.20")
                .build();
        switchDAO.store(aSwitch1);

        device1 = new Device.DeviceBuilder()
                .withMac("0000000000000001")
                .withName("Test device 1")
                .withDescription("Test device")
                .onSwitch(aSwitch1)
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
                .onSwitch(aSwitch1)
                .onPort(2)
                .withPortSpeed(1000)
                .withTOR(LocalDateTime.of(2016, 1, 1, 10, 01))
                .lastSeen(LocalDateTime.of(2016, 4, 1, 12, 40))
                .withAccess(1)
                .build();
        device3 = new Device.DeviceBuilder()
                .withMac("0000000000000003")
                .withName("Test device 3")
                .withDescription("Test device")
                .onSwitch(aSwitch1)
                .onPort(3)
                .withPortSpeed(1000)
                .withTOR(LocalDateTime.of(2016, 1, 1, 10, 02))
                .lastSeen(LocalDateTime.of(2016, 4, 1, 12, 50))
                .withAccess(1)
                .build();
    }

    @After
    public void after(){
        //setForeignKeyCheckTo(1);
    }

    @Test
    public void testStoreAndGet() throws Exception {
        assertTrue(deviceDAO.store(device1));
        assertTrue(deviceDAO.store(device2));

        Device stored = deviceDAO.getById(device1.getId());

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
    public void testGetById() throws Exception {
        assertTrue(deviceDAO.store(device1));
        assertTrue(deviceDAO.store(device2));

        Device stored = deviceDAO.getById(device1.getId());
        assertEquals(stored, device1);
        assertNotEquals(stored, device2);
    }

    @Test
    public void testGetByWrongId() throws Exception {
        assertTrue(deviceDAO.store(device1));
        assertTrue(deviceDAO.store(device2));

        Device storedDevice = deviceDAO.getById(Long.MAX_VALUE);
        assertTrue(storedDevice == null);
    }

    @Test
    public void testCount() throws Exception {
        Long startCount = deviceDAO.getCount();

        assertTrue(deviceDAO.store(device1));
        assertEquals(new Long(startCount + 1), deviceDAO.getCount());
        assertTrue(deviceDAO.store(device2));
        assertEquals(new Long(startCount + 2), deviceDAO.getCount());
        assertTrue(deviceDAO.store(device2));
        assertEquals(new Long(startCount + 2), deviceDAO.getCount());
        assertTrue(deviceDAO.store(device3));
        assertEquals(new Long(startCount + 3), deviceDAO.getCount());
    }

    @Test
    public void testDelete() throws Exception {
        Long startCount = deviceDAO.getCount();
        assertTrue(deviceDAO.store(device1));
        assertTrue(deviceDAO.store(device2));
        assertTrue(deviceDAO.store(device3));
        assertEquals(new Long(startCount + 3), deviceDAO.getCount());

        deviceDAO.delete(device1);
        Device storedDevice = deviceDAO.getById(device1.getId());
        assertTrue(storedDevice == null);
        assertEquals(new Long(startCount + 2), deviceDAO.getCount());

        deviceDAO.delete(device2);
        assertEquals(new Long(startCount + 1), deviceDAO.getCount());
    }

    @Test
    public void testGetAll() throws Exception {
        assertTrue(deviceDAO.store(device1));
        assertTrue(deviceDAO.store(device2));
        assertTrue(deviceDAO.store(device3));

        List<Device> list = new ArrayList<>();
        list.add(device1);
        list.add(device2);
        list.add(device3);

        List<Device> storedList = deviceDAO.getAll();
        assertEquals(storedList, list);
    }

    @Test
    public void testGetAllByCriteria() throws Exception {
        assertTrue(deviceDAO.store(device1));
        assertTrue(deviceDAO.store(device2));
        assertTrue(deviceDAO.store(device3));

        List<Device> list = new ArrayList<>();
        list.add(device2);
        list.add(device3);

        List<Device> storedList = deviceDAO.getAllByCriteria("portSpeed", 1000);
        assertEquals(storedList, list);
    }
}