package lv.freeradiusgui.services.filesServices;

import lv.freeradiusgui.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Daniels on 20.08.2016..
 */
public abstract class AbstractFileServices<T> {

    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AppConfig appConfig;


    public List<String> readFile(String pathString){
        List<String> listFromFile = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(pathString))) {
            //br returns as stream and convert it into a List
            listFromFile = reader.lines().collect(Collectors.toList());
        } catch (NoSuchFileException e){
            logger.error("No such file exception: '" + pathString +"'.");
            return null;
        } catch (IOException e) {
            logger.error("Error reading file  '" + pathString + "'");
            logger.error("STACK TRACE: ",e);
            return null;
        }
        logger.info("Successfully loaded '" + pathString + "'" +  " file.");
        return listFromFile;
    }

    public boolean writeFile(List<String> list, String pathString) {
        Path path = Paths.get(pathString);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String str : list) {
                writer.write(str);
                writer.newLine();
            }
            writer.close();
        }  catch (NoSuchFileException e){
            logger.error("No such file exception: '" + pathString +"'.");
            return false;
        } catch (IOException e) {
            logger.error("Error writing data to file  '" + pathString +"'.");
            logger.error("STACK TRACE: ",e);
            return false;
        }
        logger.info("Successfully written data to file '" + pathString + "'.");
        return true;
    }

    public List<String> removeComments(List<String> list) {
        List<String> result = new ArrayList<>();

        for (String string : list) {
            string = string.trim();
            int position = string.indexOf("#");
            if (string.equals("")) position = 0; //do not add empty lines
            switch (position){
                case -1: result.add(string); break;
                case 0: break;
                default: result.add(string.substring(0, position).trim()); break;
            }
        }

        return result;
    }

    public String parseValue(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find()) return m.group(1).trim(); else return "";
    }


}
