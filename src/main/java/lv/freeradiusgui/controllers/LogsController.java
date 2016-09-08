package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.LogService;
import lv.freeradiusgui.services.filesServices.FileOperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
@SessionAttributes("log")
public class LogsController {

    @Autowired
    LogService logService;

    @Autowired
    Server server;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    DateTimeFormatter URLformatter = DateTimeFormatter.ofPattern("ddMMyyyy").withLocale(Locale.ENGLISH);
    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.ENGLISH);

    @ModelAttribute("page")
    public String page() {
        return "logs";
    }

    @RequestMapping(value = {Views.LOGS_LIST, Views.LOGS}, method = RequestMethod.GET)
    public String showLogs() {
        LocalDateTime date = LocalDateTime.now();
        return "redirect:/" + Views.LOGS_LIST + "/" + date.format(URLformatter);
    }

    @RequestMapping(value = Views.LOGS_LIST + "/{date}", method = RequestMethod.GET)
    public String viewLogs(Model model,
                           HttpServletRequest request,
                           @PathVariable("date") String dateStr) {

        LocalDateTime date = convertStringToDateTime(dateStr, URLformatter);
        LocalDate today = LocalDate.now();

        List<Log> list = logService.getByDate(date);

        model.addAttribute("logs", list);
        model.addAttribute("recordCount", list.size());
        model.addAttribute("date", date.format(displayFormatter));
        Integer rejectedCount = logService.countRejected(list);
        model.addAttribute("rejectedCount", rejectedCount);
        if (date.toLocalDate().equals(today)) {
            server.setTodayRejectedCount(rejectedCount);
        }
        return Views.LOGS_LIST;
    }

    @RequestMapping(value = Views.LOGS + "/refresh/{date}", method = RequestMethod.GET)
    public String refreshLogs(final RedirectAttributes redirectAttributes,
                             @PathVariable("date") String dateStr) {
        LocalDateTime date = LocalDateTime.from(LocalDate.parse(dateStr, URLformatter).atStartOfDay());
        FileOperationResult result = logService.loadFromFile(date);
        if (result.ok) {
            redirectAttributes.addFlashAttribute("msg", "Successfully loaded '" + result.message + "' file.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Error loading file - " + result.message);
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.LOGS_LIST + "/" + date.format(URLformatter);
    }

    @RequestMapping(value = {Views.LOGS_LIST + "/submit"}, method = RequestMethod.POST)
    public String goToDate(@RequestParam("date") String dateStr) {
        LocalDateTime date = convertStringToDateTime(dateStr, displayFormatter);
        return "redirect:/" + Views.LOGS_LIST + "/" + date.format(URLformatter);
    }

    private LocalDateTime convertStringToDateTime(String dateStr, DateTimeFormatter formatter) {
        LocalDateTime date;
        if (dateStr != null || !dateStr.equals("")) {
            try {
                date = LocalDateTime.from(LocalDate.parse(dateStr, formatter).atStartOfDay());
            } catch (DateTimeParseException e){
                date = LocalDateTime.now();
            }
        } else date = LocalDateTime.now();
        return date;
    }
}
