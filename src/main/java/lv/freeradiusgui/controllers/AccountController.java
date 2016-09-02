package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.domain.Role;
import lv.freeradiusgui.services.AccountService;
import lv.freeradiusgui.services.RoleService;
import lv.freeradiusgui.validators.AccountFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String page() {
        return "admin";
    }

    @ModelAttribute("allRoles")
    public List<Role> populateRoles() {
        return roleService.getAll();
    }

    @RequestMapping(value = Views.ACCOUNT + "/{id}", method = RequestMethod.GET)
    public String showAccount(@PathVariable("id") Integer accountId,
                              Model model) {
        Account account = accountService.getById(accountId);
        model.addAttribute("account", account);
        return Views.ACCOUNT_VIEW;
    }

    @RequestMapping(value = Views.ACCOUNT + "/delete/{id}", method = RequestMethod.GET)
    public String deleteAccount(@PathVariable("id") Integer accountId,
                                final RedirectAttributes redirectAttributes) {

        Account account = accountService.getById(accountId);

        if (accountService.delete(account)) {
            redirectAttributes.addFlashAttribute("msg", "Account '" + account.getLogin() + "' successfully deleted.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Error deleting account '" + account.getLogin() + "'.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }

        return "redirect:/" + Views.ADMIN;
    }

    @RequestMapping(value = Views.ACCOUNT + "/submit", method = RequestMethod.POST)
    public String storeAccount(@ModelAttribute("account") @Validated Account account,
                               BindingResult result,
                               SessionStatus status,
                               Model model,
                               final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("accounts", account);
            return Views.ACCOUNT_VIEW;
        }
        accountService.fixRolesWithOutId(account);
        accountService.store(account);
        status.setComplete();

        redirectAttributes.addFlashAttribute("msg", "Account '" + account.getLogin() + "' successfully saved.");
        redirectAttributes.addFlashAttribute("msgType", "success");
        return "redirect:/" + Views.ADMIN;
    }

    @RequestMapping(value = Views.ACCOUNT + "/add", method = RequestMethod.GET)
    public String addAccount(Model model) {
        Account account = accountService.prepareNewAccount();
        model.addAttribute("account", account);
        return Views.ACCOUNT_VIEW;
    }
}