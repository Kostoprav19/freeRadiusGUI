package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.services.SwitchService;
import lv.freeradiusgui.services.serverServices.ServerService;
import lv.freeradiusgui.validators.SwitchFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Created by Dan on 30.04.2016.
 */
@Controller
@SessionAttributes("switch")
public class SwitchesController {

    @Autowired
    SwitchService switchService;

    @Autowired
    SwitchFormValidator switchFormValidator;

    @Autowired
    ServerService serverService;

    @ModelAttribute("page")
    public String page() {
        return "switches";
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(switchFormValidator);
    }

    @RequestMapping(value = {Views.SWITCH_LIST, Views.SWITCH}, method = RequestMethod.GET)
    public String viewSwitches(Model model) {
        List<Switch> list = switchService.getAll();
        model.addAttribute("switches", list);
        model.addAttribute("recordCount", list.size());
        return Views.SWITCH_LIST;
    }

    @RequestMapping(value = Views.SWITCH + "/{id}", method = RequestMethod.GET)
    public String showSwitch(@PathVariable("id") Integer aSwitchId,
                             Model model) {
        Switch aSwitch = switchService.getById(aSwitchId);
        model.addAttribute("aSwitch", aSwitch);
        return Views.SWITCH_VIEW;
    }

    @RequestMapping(value = Views.SWITCH + "/delete/{id}", method = RequestMethod.GET)
    public String deleteSwitch(@PathVariable("id") Integer aSwitchId,
                               final RedirectAttributes redirectAttributes) {

        Switch aSwitch = switchService.getById(aSwitchId);
        if (switchService.delete(aSwitch)) {
            redirectAttributes.addFlashAttribute("msg", "Switch '" + aSwitch.getName() + "' successfully deleted.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Error deleting switch '" + aSwitch.getName() + "'.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }

        return "redirect:/" + Views.SWITCH_LIST;
    }

    @RequestMapping(value = Views.SWITCH + "/submit", method = RequestMethod.POST)
    public String storeSwitch(@ModelAttribute("aSwitch") @Validated Switch aSwitch,
                              BindingResult result,
                              SessionStatus status,
                              Model model,
                              final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("aSwitch", aSwitch);
            return Views.SWITCH_VIEW;
        }

        switchService.store(aSwitch);
        serverService.setDbChangesFlag();
        status.setComplete();

        redirectAttributes.addFlashAttribute("msg", "Switch '" + aSwitch.getName() + "' successfully saved.");
        redirectAttributes.addFlashAttribute("msgType", "success");
        return "redirect:/" + Views.SWITCH_LIST;
    }

    @RequestMapping(value = Views.SWITCH + "/add", method = RequestMethod.GET)
    public String addSwitch(Model model) {
        Switch aSwitch = switchService.prepareNewSwitch();
        model.addAttribute("aSwitch", aSwitch);
        return Views.SWITCH_VIEW;
    }

    @RequestMapping(value = Views.SWITCH + "/reload", method = RequestMethod.GET)
    public String reloadSwitches(final RedirectAttributes redirectAttributes) {
        if (switchService.reloadFromConfig()) {
            redirectAttributes.addFlashAttribute("msg", "Successfully loaded 'clients.conf' file.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Error loading 'clients.config' file.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.SWITCH_LIST;
    }

    @RequestMapping(value = Views.ADMIN + "/writeClients", method = RequestMethod.GET)
    public String writeSwitches(final RedirectAttributes redirectAttributes) {
        if (switchService.writeToConfig()) {
            if (serverService.restartFreeradius()) {
                redirectAttributes.addFlashAttribute("msg", "Successfully applied changes.");
                redirectAttributes.addFlashAttribute("msgType", "success");
                serverService.unsetDbChangesFlag();
            } else {
                redirectAttributes.addFlashAttribute("msg", "Changes saved to 'clinets.conf' file, but failed to restart freeradius service.");
                redirectAttributes.addFlashAttribute("msgType", "danger");
            }
        } else {
            redirectAttributes.addFlashAttribute("msg", "Error writing to 'clients.conf' file.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.SWITCH_LIST;
    }
}
