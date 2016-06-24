package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.services.AccountService;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
public class AdminController {

    @Autowired
    AccountService accountService;

    @ModelAttribute("page")
    public String page() {
        return "admin";
    }

    @RequestMapping(value = Views.ADMIN, method = RequestMethod.GET)
    public String adminView(Model model) {
        model.addAttribute("accounts", accountService.getAll());
        return Views.ADMIN;
    }
}
