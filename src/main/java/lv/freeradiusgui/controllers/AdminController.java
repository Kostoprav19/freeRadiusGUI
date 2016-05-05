package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
public class AdminController {

    // private final Logger logger = LogManager.getLogger(IndexController.class);

    @Autowired
    AccountService accountService;

    @RequestMapping(Views.ADMIN)
    public ModelAndView admin() {
        ModelAndView mav = new ModelAndView(Views.ADMIN);
        mav.addObject("accounts", accountService.getAll());
        return mav;
    }

}
