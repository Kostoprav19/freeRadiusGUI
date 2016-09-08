package lv.freeradiusgui.listeners;

/**
 * Created by Dan on 10.08.2016.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        Object userName = event.getAuthentication().getPrincipal();
        Object credentials = event.getAuthentication().getCredentials();
        logger.info("Failed login using USERNAME: '" + userName + "'");
        logger.info("Failed login using PASSWORD: '" + credentials + "'");
    }
}
