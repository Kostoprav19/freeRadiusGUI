package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
public class AccountController {

    // private final Logger logger = LogManager.getLogger(IndexController.class);

    @Autowired
    AccountService accountService;


    @RequestMapping(value = Views.ACCOUNT, params = { "id" })
    public ModelAndView showAccount(@RequestParam("id") Long accountId) {
        ModelAndView mav = new ModelAndView(Views.ACCOUNT);
        mav.addObject("account", accountService.getById(accountId));
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT, params = { "id", "action=delete" })
    public ModelAndView deleteAccount(@RequestParam("id") Long accountId) {

        Account account = accountService.getById(accountId);
        accountService.delete(account);

        ModelAndView mav = new ModelAndView("redirect:/" + Views.ADMIN);
        mav.addObject("accounts", accountService.getAll());
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT, params = {"action=store" }, method=RequestMethod.POST)
    public ModelAndView storeAccount(@ModelAttribute(value="account") Account account) {

        if (account.getCreationDate() == null) {account.setCreationDate(LocalDateTime.now());}
        accountService.store(account);

        ModelAndView mav = new ModelAndView("redirect:/" + Views.ADMIN);
        mav.addObject("accounts", accountService.getAll());
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT, params = {"action=add" }, method=RequestMethod.GET)
    public ModelAndView addAccount() {
        Account account = new Account();

        ModelAndView mav = new ModelAndView(Views.ACCOUNT);
        mav.addObject("account", account);
        return mav;
    }
}
