package lv.freeradiusgui.services.shellServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShellExecutorImpl implements ShellExecutor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String executeCommand(String command) {
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            // Run via a shell so command strings may use pipes/redirection
            // (e.g. curl ... | grep). Commands come from config.properties,
            // never from user input.
            p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", command});
            if (!p.waitFor(30, TimeUnit.SECONDS)) {
                logger.error("Command timed out after 30s: '" + command + "'.");
                p.destroyForcibly();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            logger.error("Error executing command: '" + command + "'.");
            logger.error("STACK TRACE: ", e);
        }

        return output.toString();
    }
}
