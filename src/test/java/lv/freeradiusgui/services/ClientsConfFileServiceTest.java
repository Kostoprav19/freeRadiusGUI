package lv.freeradiusgui.services;

import lv.freeradiusgui.config.WebMVCConfig;
import lv.freeradiusgui.services.filesServices.ClientsConfFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMVCConfig.class)
public class ClientsConfFileServiceTest {

    @Autowired
    private ClientsConfFileService clientsConfFileService;

    @Test
    public void removeComments_stripsWholeLineAndTrailingComments() {
        List<String> input = Arrays.asList(
                "   #comments",
                "text1 #comments",
                "#comments",
                "text2");

        List<String> cleaned = clientsConfFileService.removeComments(input);

        assertEquals(Arrays.asList("text1", "text2"), cleaned);
    }

    @Test
    public void parseValue_extractsClientIpSecretAndShortname() {
        assertEquals("192.168.0.30", clientsConfFileService.parseValue(
                "client 192.168.0.30 {", ClientsConfFileService.CLIENT_PATTERN));

        assertEquals("Pilakaknada123", clientsConfFileService.parseValue(
                "secret = Pilakaknada123", ClientsConfFileService.SECRET_PATTERN));

        assertEquals("K12_user-sw-1", clientsConfFileService.parseValue(
                "shortname = K12_user-sw-1", ClientsConfFileService.SHORTNAME_PATTERN));
    }

    @Test
    public void parseValue_returnsEmptyStringWhenPatternDoesNotMatch() {
        assertEquals("", clientsConfFileService.parseValue(
                "shortname = foo", ClientsConfFileService.SECRET_PATTERN));
    }
}
