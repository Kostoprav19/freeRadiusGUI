package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.domain.Role;
import lv.freeradiusgui.services.AccountService;
import lv.freeradiusgui.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
@SessionAttributes("account")
public class AccountController {

    // private final Logger logger = LogManager.getLogger(IndexController.class);

    @Autowired
    AccountService accountService;

    @Autowired
    RoleService roleService;

   /* @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(Role.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                System.out.println("BINDER LOG-----------------------------------------");
                System.out.println(text);
                System.out.println("BINDER LOG-----------------------------------------");
                Integer id = new Integer(text);
                Role role = roleService.getById(id);
                setValue(role);
            }
        });
    }
*/
    @ModelAttribute("allRoles")
    public List<Role> populateRoles() {
        return roleService.getAll();
    }

    @RequestMapping(value = Views.ACCOUNT, params = { "id" })
    public ModelAndView showAccount(@RequestParam("id") Long accountId) {

        Account account = accountService.getById(accountId);
        System.out.println("VIEW-----------------------------------------");
        System.out.println(account);
        System.out.println("VIEW-----------------------------------------");

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

    @RequestMapping(value = Views.ACCOUNT + "/submit", method=RequestMethod.POST)
    public ModelAndView storeAccount(@ModelAttribute("account") Account account, SessionStatus status) {

        System.out.println("SUBMIT -----------------------------------------");
        System.out.println(account);
        System.out.println("SUBMIT -----------------------------------------");

        for (Role role: account.getRoles()){
            if (role.getId() == null) {
                role.setId(roleService.getByName(role.getName()).getId());
            }
        }
        if (account.getCreationDate() == null) account.setCreationDate(LocalDateTime.now());

        System.out.println("SUBMIT -----------------------------------------");
        System.out.println(account);
        System.out.println("SUBMIT -----------------------------------------");

        accountService.store(account);
        status.setComplete();
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
