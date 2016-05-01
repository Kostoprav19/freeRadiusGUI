package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
public class HomeController {

   // private final Logger logger = LogManager.getLogger(IndexController.class);

    @RequestMapping(value = "/")
    public String index(Model model) {
    //    model.addAttribute("name", name);
        return Views.HOME;
    }

  @RequestMapping(Views.ADMIN)
    public String gambling() {

        return Views.ADMIN;
    }

}
