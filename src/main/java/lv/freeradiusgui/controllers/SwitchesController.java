package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.services.SwitchService;
import lv.freeradiusgui.services.SwitchService;
import lv.freeradiusgui.validators.SwitchFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

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

    @ModelAttribute("page")
    public String page() {
        return "switches";
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(switchFormValidator);
    }

    @RequestMapping(value = Views.SWITCH_LIST, method = RequestMethod.GET)
    public ModelAndView viewSwitches() {
        ModelAndView mav = new ModelAndView(Views.SWITCH_LIST);
        mav.addObject("switches", switchService.getAll());
        return mav;
    }

    @RequestMapping(value = Views.SWITCH + "/{id}", method = RequestMethod.GET)
    public ModelAndView showSwitch(@PathVariable("id") Integer aSwitchId) {
        Switch aSwitch = switchService.getById(aSwitchId);
        ModelAndView mav = new ModelAndView(Views.SWITCH_VIEW);
        mav.addObject("aSwitch", switchService.getById(aSwitchId));
        return mav;
    }

    @RequestMapping(value = Views.SWITCH + "/delete/{id}", method = RequestMethod.GET)
    public ModelAndView deleteSwitch(@PathVariable("id") Integer aSwitchId) {

        Switch aSwitch = switchService.getById(aSwitchId);
        switchService.delete(aSwitch);

        ModelAndView mav = new ModelAndView("redirect:/" + Views.SWITCH_LIST);
        mav.addObject("aSwitch", switchService.getAll());
        mav.addObject("msg", "Switch '" + aSwitch.getName() + "' successfully deleted.");
        mav.addObject("msgType", "success");
        return mav;
    }

    @RequestMapping(value = Views.SWITCH + "/submit", method = RequestMethod.POST)
    public ModelAndView storeSwitch(@ModelAttribute("aSwitch") @Validated Switch aSwitch,
                                     BindingResult result,
                                     SessionStatus status) {

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView(Views.SWITCH_VIEW);
            mav.addObject("aSwitch", aSwitch);
            return mav;
        }

        switchService.store(aSwitch);
        status.setComplete();
        ModelAndView mav = new ModelAndView("redirect:/" + Views.SWITCH_LIST);
        mav.addObject("aSwitch", switchService.getAll());
        mav.addObject("msg", "Switch '" + aSwitch.getName() + "' successfully saved.");
        mav.addObject("msgType", "success");
        return mav;
    }

    @RequestMapping(value = Views.SWITCH + "/add", method = RequestMethod.GET)
    public ModelAndView addSwitch() {
        Switch aSwitch = switchService.prepareNewSwitch();
        ModelAndView mav = new ModelAndView(Views.SWITCH_VIEW);
        mav.addObject("aSwitch", aSwitch);
        return mav;
    }

    @RequestMapping(value = Views.SWITCH + "/reload", method = RequestMethod.GET)
    public ModelAndView reloadSwitches() {
        ModelAndView mav = new ModelAndView("redirect:/" + Views.SWITCH_LIST);
        if (switchService.reloadFromConfig()) {
            mav.addObject("msg", "Successfully loaded 'clients.config' file.");
            mav.addObject("msgType", "success");
        } else {
            mav.addObject("msg", "Error loading 'clients.config' file.");
            mav.addObject("msgType", "danger");
        }
        mav.addObject("switches", switchService.getAll());
        return mav;
    }
}
