package lv.freeradiusgui.validators;

import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 29.05.2016.
 */
@Component("deviceFormValidator")
public class DeviceFormValidator implements Validator{

    @Autowired
    DeviceService deviceService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Device.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Device device = (Device) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mac", "NotEmpty.deviceForm.mac");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.deviceForm.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "access", "NotEmpty.deviceForm.access");

        if (!isMacValid(device.getMac())){
            errors.rejectValue("mac", "Pattern.deviceForm.mac");
        }

        if (deviceService.getByMac(device.getMac()) != null){
            errors.rejectValue("mac", "Exist.deviceForm.mac");
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

}
