package lv.freeradiusgui.services.shellServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Daniels on 02.09.2016..
 */
@Service
public class ShellExecutorImpl implements ShellExecutor{



    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            logger.error("Error executing command: '" + command + "'.");
            logger.error("STACK TRACE: ", e);
        }

        return output.toString();
    }
}