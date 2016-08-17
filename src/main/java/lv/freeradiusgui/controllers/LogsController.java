package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
@SessionAttributes("log")
public class LogsController {

    @Autowired
    LogService logService;

    @ModelAttribute("page")
    public String page() {
        return "logs";
    }

    @RequestMapping(value = {Views.LOGS_LIST, Views.LOGS}, method = RequestMethod.GET)
    public String viewLogs(Model model, HttpServletRequest request) {
        List<Log> list = logService.getAll();
        model.addAttribute("logs", list);
        model.addAttribute("recordCount", list.size());
        request.getSession().setAttribute("rejectedCount", logService.countRejected(list));
        return Views.LOGS_LIST;
    }

    @RequestMapping(value = Views.LOGS + "/{id}", method = RequestMethod.GET)
    public String showLog(@PathVariable("id") Integer logId,
                             Model model) {
        Log log = logService.getById(logId);
        model.addAttribute("log", log);
        return Views.LOG_VIEW;
    }

    @RequestMapping(value = Views.LOGS + "/refresh", method = RequestMethod.GET)
    public String reloadLogs(final RedirectAttributes redirectAttributes) {
        String result = logService.loadFromFile();
        if (result != null) {
            redirectAttributes.addFlashAttribute("msg", "Successfully loaded '" + result + "' file.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Error loading file.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.LOGS_LIST;
    }
}
