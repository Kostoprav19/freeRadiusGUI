package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
public class ServerController {

    @Autowired
    Server server;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute("page")
    public String module() {
        return "server";
    }

    @RequestMapping(Views.SERVER)
    public String home(Model model) {
        server.updateStatuses();
        model.addAttribute("lastServiceReboot", server.getLastServiceReboot());
        model.addAttribute("tomcatStatus", server.getStatus(Server.TOMCAT));
        model.addAttribute("mysqlStatus", server.getStatus(Server.MYSQL));
        return Views.SERVER;
    }

    @RequestMapping(Views.SERVER + "/start")
    public String startService(final RedirectAttributes redirectAttributes) {
        server.startService();
        if (server.getStatus(Server.FREERADIUS) == Server.SERVER_STATUS_UP){
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
        if (server.getStatus(Server.FREERADIUS) == Server.SERVER_STATUS_DOWN){
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
        if (server.getStatus(Server.FREERADIUS) == Server.SERVER_STATUS_UP){
            redirectAttributes.addFlashAttribute("msg", "Service 'freeradius' successfully restarted.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Failed to restart service 'freeradius'.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.SERVER;
    }

    @RequestMapping(value = {Views.ADMIN + "/runCommand"}, method = RequestMethod.POST)
    public String runCommand(final RedirectAttributes redirectAttributes,
                             @RequestParam("consoleInput") String consoleInput) {

        String consoleOutput = server.runCommand(consoleInput);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("User '" + currentUser + "' called console command: " + consoleInput);

        redirectAttributes.addFlashAttribute("consoleOutput", consoleOutput);

        return "redirect:/" + Views.SERVER;
    }
}
