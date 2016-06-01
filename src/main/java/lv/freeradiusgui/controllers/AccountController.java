package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.domain.Role;
import lv.freeradiusgui.services.AccountService;
import lv.freeradiusgui.services.RoleService;
import lv.freeradiusgui.validators.AccountFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
@SessionAttributes("account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    RoleService roleService;

    @Autowired
    AccountFormValidator accountFormValidator;


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(accountFormValidator);
    }

    @ModelAttribute("page")
    public String module() {
        return "admin";
    }

    @ModelAttribute("allRoles")
    public List<Role> populateRoles() {
        return roleService.getAll();
    }

    @RequestMapping(value = Views.ACCOUNT + "/{id}", method = RequestMethod.GET)
    public ModelAndView showAccount(@PathVariable("id") Integer accountId) {
        Account account = accountService.getById(accountId);
        ModelAndView mav = new ModelAndView(Views.ACCOUNT_VIEW);
        mav.addObject("account", accountService.getById(accountId));
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT + "/delete/{id}", method = RequestMethod.GET)
    public ModelAndView deleteAccount(@PathVariable("id") Integer accountId) {

        Account account = accountService.getById(accountId);
        accountService.delete(account);

        ModelAndView mav = new ModelAndView("redirect:/" + Views.ADMIN);
        mav.addObject("accounts", accountService.getAll());
        mav.addObject("msg", "Account '" + account.getLogin() + "' successfully deleted.");
        mav.addObject("msgType", "success");
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT + "/submit", method = RequestMethod.POST)
    public ModelAndView storeAccount(@ModelAttribute("account") @Validated Account account,
                                     BindingResult result,
                                     SessionStatus status) {

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView(Views.ACCOUNT_VIEW);
            mav.addObject("accounts", account);
            return mav;
        }

        accountService.fixRolesWithOutId(account);
        accountService.store(account);
        status.setComplete();
        ModelAndView mav = new ModelAndView("redirect:/" + Views.ADMIN);
        mav.addObject("accounts", accountService.getAll());
        mav.addObject("msg", "Account '" + account.getLogin() + "' successfully saved.");
        mav.addObject("msgType", "success");
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT + "/add", method = RequestMethod.GET)
    public ModelAndView addAccount() {
        Account account = accountService.prepareNewAccount();
        ModelAndView mav = new ModelAndView(Views.ACCOUNT_VIEW);
        mav.addObject("account", account);
        return mav;
    }
}