package lv.freeradiusgui.services.mailServices;

import lv.freeradiusgui.config.AppConfig;
import lv.freeradiusgui.utils.OperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Daniels on 02.11.2016..
 */
public class MailServiceImpl implements MailService {

    @Autowired
    AppConfig appConfig;

    private Session session;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MailServiceImpl(){
        Properties props = System.getProperties();
        props.put("mail.smtp.host", appConfig.getMailSmtpServer());
        this.session = Session.getInstance(props, null);
    }

    @Override
    public OperationResult sendMail(String subject, String body) {
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(appConfig.getMailFrom(), "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse(appConfig.getMailFrom(), false));

            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(appConfig.getMailTo(), false));

            Transport.send(msg);

            OperationResult result = new OperationResult(OperationResult.SUCCESS, "E-mail sent successfully.");
            return result;
        }
        catch (Exception e) {
            logger.error("Failed to send e-mail.");
            logger.error("STACK TRACE: ",e);
            OperationResult result = new OperationResult(OperationResult.FAIL, "");
            return result;
        }
    }
}
