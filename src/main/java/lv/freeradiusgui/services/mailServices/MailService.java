package lv.freeradiusgui.services.mailServices;

import lv.freeradiusgui.utils.OperationResult;

/**
 * Created by Daniels on 02.11.2016..
 */
public interface MailService {
    OperationResult sendMail(String subject, String body);
}
