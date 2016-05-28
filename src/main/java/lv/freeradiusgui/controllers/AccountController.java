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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @RequestMapping(value = Views.ACCOUNT + "/{id}")
    public ModelAndView showAccount(@PathVariable("id") Long accountId) {
        Account account = accountService.getById(accountId);
        ModelAndView mav = new ModelAndView(Views.ACCOUNT);
        mav.addObject("account", accountService.getById(accountId));
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT + "/delete/{id}")
    public ModelAndView deleteAccount(@PathVariable("id") Long accountId) {

        Account account = accountService.getById(accountId);
        accountService.delete(account);

        ModelAndView mav = new ModelAndView("redirect:/" + Views.ADMIN);
        mav.addObject("accounts", accountService.getAll());
        mav.addObject("msg", "Account '" + account.getLogin() + "' successfully deleted.");
        mav.addObject("msgType", "success");
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT + "/submit", method=RequestMethod.POST)
    public ModelAndView storeAccount(@ModelAttribute("account") Account account, SessionStatus status) {
        for (Role role: account.getRoles()){
            if (role.getId() == null) {
                role.setId(roleService.getByName(role.getName()).getId());
            }
        }
        accountService.store(account);
        status.setComplete();
        ModelAndView mav = new ModelAndView("redirect:/" + Views.ADMIN);
        mav.addObject("accounts", accountService.getAll());
        mav.addObject("msg", "Account '" + account.getLogin() + "' successfully saved.");
        mav.addObject("msgType", "success");
        return mav;
    }

    @RequestMapping(value = Views.ACCOUNT + "/add", method=RequestMethod.GET)
    public ModelAndView addAccount() {
        Account account = new Account();
        account.setCreationDate(LocalDateTime.now());

        ModelAndView mav = new ModelAndView(Views.ACCOUNT);
        mav.addObject("account", account);
        return mav;
    }
}
