package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
public class ServerController {

    @Autowired
    Server server;

    @ModelAttribute("page")
    public String module() {
        return "server";
    }

    @RequestMapping(Views.SERVER)
    public String home(Model model) {
        server.updateStatus();
        model.addAttribute("lastServiceReboot", server.getLastServiceReboot());
        return Views.SERVER;
    }

    @RequestMapping(Views.SERVER + "/start")
    public String startService(final RedirectAttributes redirectAttributes) {
        server.startService();
        if (server.getStatus() == Server.SERVER_STATUS_UP){
            redirectAttributes.addFlashAttribute("msg", "Service 'freeradius' successfully started.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Failed to start service 'freeradius'.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.SERVER;
    }

    @RequestMapping(Views.SERVER + "/stop")
    public String stopService(final RedirectAttributes redirectAttributes) {
        server.stopService();
        if (server.getStatus() == Server.SERVER_STATUS_DOWN){
            redirectAttributes.addFlashAttribute("msg", "Service 'freeradius' successfully stopped.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Failed to stop service 'freeradius'.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.SERVER;
    }

    @RequestMapping(Views.SERVER + "/restart")
    public String restartService(final RedirectAttributes redirectAttributes) {
        server.restartService();
        if (server.getStatus() == Server.SERVER_STATUS_UP){
            redirectAttributes.addFlashAttribute("msg", "Service 'freeradius' successfully restarted.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Failed to restart service 'freeradius'.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.SERVER;
    }
}
