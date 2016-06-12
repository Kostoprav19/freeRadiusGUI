package lv.freeradiusgui.validators;

import lv.freeradiusgui.domain.Switch;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 29.05.2016.
 */
@Component("switchFormValidator")
public class SwitchFormValidator implements Validator{

    @Override
    public boolean supports(Class<?> clazz) {
        return Switch.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Switch aSwitch = (Switch) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.switchForm.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mac", "NotEmpty.switchForm.mac");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ip", "NotEmpty.switchForm.ip");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secret", "NotEmpty.switchForm.secret");

        if (!isMacValid(aSwitch.getMac())){
            errors.rejectValue("mac", "Pattern.accountForm.mac");
        }

        if (!isIPValid(aSwitch.getIp())){
            errors.rejectValue("ip", "Pattern.accountForm.ip");
        }

    }

    private boolean isMacValid(String mac){
        Pattern pattern;
        Matcher matcher;

        mac = mac.replaceAll("[^a-fA-F0-9]", "");

        pattern = Pattern.compile("^([0-9a-fA-F]){12}$");

        matcher = pattern.matcher(mac);
        return matcher.matches();
    }

    private boolean isIPValid(String ip){
        Pattern pattern;
        Matcher matcher;

        String IPADDRESS_PATTERN = "^((?:[0-9]{1,3}\\.){3}[0-9]{1,3})$";

        pattern = Pattern.compile(IPADDRESS_PATTERN);

        matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
