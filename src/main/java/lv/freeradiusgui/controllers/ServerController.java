package lv.freeradiusgui.controllers;

import lv.freeradiusgui.config.AppConfig;
import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.serverServices.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ServerController {

    @Autowired ServerService serverService;
    @Autowired AppConfig appConfig;

    @ModelAttribute("page")
    public String module() {
        return "server";
    }

    @RequestMapping(Views.SERVER)
    public String home(Model model) {
        serverService.updateStatuses();
        model.addAttribute("lastServiceReboot", serverService.getLastServiceReboot());
        model.addAttribute("mysqlStatus", serverService.getStatus(Server.MYSQL));
        model.addAttribute("usersFilePath", appConfig.getPathToUsersFile());
        model.addAttribute("clientsFilePath", appConfig.getPathToClientsConfFile());
        model.addAttribute("logFilesDirPath", appConfig.getPathToLogDirectory());
        return Views.SERVER;
    }

    @RequestMapping(Views.SERVER + "/restart")
    public String restartService(final RedirectAttributes redirectAttributes) {
        serverService.restartFreeradius();
        if (serverService.getStatus(Server.FREERADIUS) == Server.SERVER_STATUS_UP) {
            redirectAttributes.addFlashAttribute(
                    "msg", "Service 'freeradius' restarted; changes applied.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Failed to restart service 'freeradius'.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.SERVER;
    }
}
