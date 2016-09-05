package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
public class ServerController {

   // private final Logger logger = LogManager.getLogger(IndexController.class);

    @ModelAttribute("page")
    public String module() {
        return "home";
    }

    @RequestMapping(Views.SERVER)
    public String home() {
        return Views.SERVER;
    }

}
