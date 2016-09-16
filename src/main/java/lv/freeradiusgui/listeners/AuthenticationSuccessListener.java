package lv.freeradiusgui.listeners;

/**
 * Created by Dan on 10.08.2016.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        String userName = event.getAuthentication().getName();
        logger.info("Users logged in using USERNAME: '" + userName + "'");

    }
}
