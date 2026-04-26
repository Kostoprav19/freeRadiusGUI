package lv.freeradiusgui.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.util.List;
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

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMVCConfig.class)
@Transactional
@Rollback
public class DeviceRepositoryTest {

    @Autowired private DeviceRepository deviceRepository;

    @Autowired private SwitchRepository switchRepository;

    private Switch persistedSwitch;

    @Before
    public void setUp() {
        Switch s = new Switch();
        s.setName("test-switch");
        s.setIp("10.0.0.99");
        s.setSecret("secret");
        persistedSwitch = switchRepository.save(s);
    }

    @After
    public void tearDown() {
        // @Rollback handles cleanup
    }

    /** T2: switchRef round-trip. Persist with a Switch FK, reload, verify {@code switchRef.id}. */
    @Test
    public void switchRefIdRoundTrip() {
        Device d = newDevice("AABBCCDDEEFF");
        d.setSwitch(persistedSwitch);
        Device saved = deviceRepository.save(d);

        Device reloaded = deviceRepository.findById(saved.getId()).orElse(null);
        assertNotNull(reloaded);
        assertEquals(persistedSwitch.getId(), reloaded.getSwitchId());
    }

    /** T4: switches.switch_id is INT UNSIGNED — JDBC returns Long, must coerce to Integer. */
    @Test
    public void intUnsignedReadsAsInteger() {
        Switch reloaded = switchRepository.findById(persistedSwitch.getId()).orElse(null);
        assertNotNull(reloaded);
        assertEquals(Integer.class, reloaded.getId().getClass());
    }

    /**
     * T1: derived finder {@code findByPortSpeed} replaces the old getAllByCriteria use. Uses a
     * port-speed value not present in dev-seed data (10/100/1000) so the assertion is independent
     * of seeded rows.
     */
    @Test
    public void findByPortSpeedReturnsMatching() {
        int uniqueSpeed = 2500;

        Device d1 = newDevice("AABBCCDDEE01");
        d1.setPortSpeed(uniqueSpeed);
        deviceRepository.save(d1);

        Device d2 = newDevice("AABBCCDDEE02");
        d2.setPortSpeed(100);
        deviceRepository.save(d2);

        List<Device> result = deviceRepository.findByPortSpeed(uniqueSpeed);
        assertEquals(1, result.size());
        assertEquals("AABBCCDDEE01", result.get(0).getMac());
    }

    @Test
    public void findByMacReturnsNullWhenAbsent() {
        assertNull(deviceRepository.findByMac("000000000000"));
    }

    private Device newDevice(String mac) {
        Device d = new Device();
        d.setMac(mac);
        d.setName("dev-" + mac);
        d.setType(Device.TYPE_COMPUTER);
        d.setAccess(Device.ACCESS_ACCEPT);
        d.setSwitchPort(1);
        d.setPortSpeed(1000);
        d.setDuplex(Device.FULL_DUPLEX);
        d.setTimeOfRegistration(LocalDateTime.now());
        return d;
    }
}
