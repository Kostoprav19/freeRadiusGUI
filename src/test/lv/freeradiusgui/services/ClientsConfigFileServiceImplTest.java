package lv.freeradiusgui.services;

import lv.freeradiusgui.config.WebMVCConfig;
import lv.freeradiusgui.domain.Switch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Dan on 10.06.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMVCConfig.class)
public class ClientsConfigFileServiceImplTest {

    @Autowired
    ClientsConfigFileService clientsConfigFileService;


    @Test
    public void findSubList() throws Exception {
        List<String> list = Arrays.asList(
                "other line",
                "other line",
                "client 192.168.0.30 {",
                " secret = Pilakaknada123",
                " shortname = K12_user-sw-1",
                "}",
                "other line",
                "other line");
        List<String> newList = clientsConfigFileService.findSubList(list, 0);
        System.out.println(list);
        System.out.println(newList);
        assertEquals(newList, Arrays.asList("client 192.168.0.30 {", " secret = Pilakaknada123", " shortname = K12_user-sw-1", "}"));

    }


    @Test
    public void testRemoveComments(){
        List<String> list = Arrays.asList("   #comments","text1 #comments","#comments","text2");
        List<String> cleanList = clientsConfigFileService.removeComments(list);
        System.out.println(list);
        System.out.println(cleanList);
        assertEquals(cleanList, Arrays.asList("text1","text2"));
    }

    @Test
    public void testReadFromFile() {
        List<Switch> list = clientsConfigFileService.readFile();
        System.out.println(list);
    }

    @Test
    public void parseValue() throws Exception {
        List<String> list = Arrays.asList(
                "client 192.168.0.30 {",
                "secret = Pilakaknada123",
                "shortname = K12_user-sw-1",
                "}");

        for (String string:list){
            System.out.println("STRING: '" + string + "'");
            System.out.println("CLIENT: '" + clientsConfigFileService.parseValue(string, ClientsConfigFileServiceImpl.CLIENT_PATTERN) + "'");
            System.out.println("SECRET: '" + clientsConfigFileService.parseValue(string, ClientsConfigFileServiceImpl.SECRET_PATTERN) + "'");
            System.out.println("SHORTNAME: '" + clientsConfigFileService.parseValue(string, ClientsConfigFileServiceImpl.SHORTNAME_PATTERN) + "'");
            System.out.println();
        }
    }

}