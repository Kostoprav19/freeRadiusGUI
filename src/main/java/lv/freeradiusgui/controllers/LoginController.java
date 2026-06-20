package lv.freeradiusgui.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lv.freeradiusgui.config.AppConfig;
import lv.freeradiusgui.constants.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired AppConfig appConfig;

    @RequestMapping(
            value = {Views.LOGIN, "/"},
            method = RequestMethod.GET)
    public String login(
            @RequestParam(value = "error", required = false) String error, Model model) {
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext()
                        .getAuthentication()
                        .isAuthenticated()
                && !"anonymousUser"
                        .equals(
                                SecurityContextHolder.getContext()
                                        .getAuthentication()
                                        .getPrincipal())) {
            return "redirect:/logs";
        }
        model.addAttribute("loginError", error != null);
        model.addAttribute("appVersion", appConfig.getAppVersion());
        return Views.LOGIN;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
    }
}
