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

    // private final Logger logger = LogManager.getLogger(IndexController.class);

    @Autowired
    AccountService accountService;

    @RequestMapping(Views.ADMIN)
    public ModelAndView adminView() {
        ModelAndView mav = new ModelAndView(Views.ADMIN);
        mav.addObject("accounts", accountService.getAll());
        return mav;
    }
}
