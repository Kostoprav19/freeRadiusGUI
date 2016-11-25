package lv.freeradiusgui.services.mailServices;

import lv.freeradiusgui.config.AppConfig;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.services.serverServices.ServerService;
import lv.freeradiusgui.utils.OperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by Daniels on 02.11.2016..
 */
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    AppConfig appConfig;

    @Autowired
    ServerService serverService;

    private Session session;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withLocale(Locale.ENGLISH);

    @PostConstruct
    public void init() {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", appConfig.getMailSmtpServer());
        this.session = Session.getInstance(props, null);
    }

    private OperationResult transmit(String from, String to, String subject, String body) {
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(from, from));
            msg.setReplyTo(InternetAddress.parse(from, false));

            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

            Transport.send(msg);

            OperationResult result = new OperationResult(OperationResult.SUCCESS, "E-mail sent successfully.");
            logger.info("Successfully sent notification email to " + to);
            return result;
        }
        catch (Exception e) {
            logger.error("Failed to send e-mail.");
            logger.error("STACK TRACE: ",e);
            OperationResult result = new OperationResult(OperationResult.FAIL, "");
            return result;
        }
    }

    @Override
    public OperationResult sendMail() {
        String from = appConfig.getMailFrom();
        String to = appConfig.getMailTo();
        String subject = "Warning! New rejected RADIUS requests: " + serverService.getRejectedLogsCounter() + ".";
        String body = generateMailBody(serverService.getRejectedLogsListToday(), serverService.getRejectedLogsCounter());
        return transmit(from, to, subject, body);
    }

    private String generateMailBody(List<Log> list, int counter) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rejected RADIUS requests:\n\n");
        int j = 1;
        for (int i = list.size()-1; i > list.size()-1-counter; i--) {
            Log log = list.get(i);
            sb.append(j++ + ") " + log.getTimeOfRegistration().format(displayFormatter) + ", mac: " + log.getMac() + ", switch: " + log.getSwitch().getName() + " on port " + log.getSwitchPort() + "\n");
        }

        sb.append("\nKeep calm and login to the System - http://freeradius.rbp.lv");
        return sb.toString();
    }
}
