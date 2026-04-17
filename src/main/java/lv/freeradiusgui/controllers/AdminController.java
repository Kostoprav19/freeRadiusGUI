package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
