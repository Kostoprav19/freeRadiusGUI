package lv.freeradiusgui.validators;

import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by Dan on 29.05.2016.
 */
@Component("accountFormValidator")
public class AccountFormValidator implements Validator{

    @Autowired
    @Qualifier("emailValidator")
    EmailValidator emailValidator;

    @Autowired
    AccountService accountService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Account account = (Account) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "NotEmpty.accountForm.login");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.accountForm.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.accountForm.name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.accountForm.email");

        if (!emailValidator.valid(account.getEmail())){
            errors.rejectValue("email", "Pattern.accountForm.email");
        }

        if (accountService.getByLogin(account.getLogin()) != null){
            errors.rejectValue("login", "Duplicate.accountForm.login");
        }
/*
        if(account.getNumber()==null || account.getNumber()<=0){
            errors.rejectValue("number", "NotEmpty.accountForm.number");
        }
  */

    }

}
