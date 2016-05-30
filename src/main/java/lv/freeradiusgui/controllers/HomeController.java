package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
public class HomeController {

   // private final Logger logger = LogManager.getLogger(IndexController.class);

    @ModelAttribute("page")
    public String module() {
        return "home";
    }

    @RequestMapping(Views.HOME)
    public String home() {
        return Views.HOME;
    }

}
