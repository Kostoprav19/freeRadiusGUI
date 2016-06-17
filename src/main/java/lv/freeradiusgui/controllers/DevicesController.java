package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.services.DeviceService;
import lv.freeradiusgui.validators.DeviceFormValidator;
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
@SessionAttributes("device")
public class DevicesController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceFormValidator deviceFormValidator;

    @ModelAttribute("page")
    public String module() {
        return "devices";
    }

    @InitBinder("device")
    protected void initBinder(WebDataBinder binder) {
        try {
            if (deviceFormValidator.supports(binder.getTarget().getClass())) {
                binder.setValidator(deviceFormValidator);
            }
        }catch (Exception e) {
                e.printStackTrace();
    }
    }

    @RequestMapping(value = Views.DEVICE_LIST, method = RequestMethod.GET)
    public ModelAndView viewDevices() {
        ModelAndView mav = new ModelAndView(Views.DEVICE_LIST);
        mav.addObject("devices", deviceService.getAll());
        return mav;
    }

    @RequestMapping(value = Views.DEVICE + "/{id}", method = RequestMethod.GET)
    public ModelAndView showDevice(@PathVariable("id") Integer deviceId) {
        Device device = deviceService.getById(deviceId);
        ModelAndView mav = new ModelAndView(Views.DEVICE_VIEW);
        mav.addObject("device", deviceService.getById(deviceId));
        return mav;
    }

    @RequestMapping(value = Views.DEVICE + "/delete/{id}", method = RequestMethod.GET)
    public ModelAndView deleteDevice(@PathVariable("id") Integer deviceId) {

        Device device = deviceService.getById(deviceId);
        deviceService.delete(device);

        ModelAndView mav = new ModelAndView("redirect:/" + Views.DEVICE_LIST);
        mav.addObject("device", deviceService.getAll());
        mav.addObject("msg", "Device '" + device.getName() + "' successfully deleted.");
        mav.addObject("msgType", "success");
        return mav;
    }

    @RequestMapping(value = Views.DEVICE + "/submit", method = RequestMethod.POST)
    public ModelAndView storeDevice(@ModelAttribute("device") @Validated Device device,
                                     BindingResult result,
                                     SessionStatus status) {

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView(Views.DEVICE_VIEW);
            mav.addObject("device", device);
            return mav;
        }

        deviceService.store(device);
        status.setComplete();
        ModelAndView mav = new ModelAndView("redirect:/" + Views.DEVICE_LIST);
        mav.addObject("device", deviceService.getAll());
        mav.addObject("msg", "Device '" + device.getName() + "' successfully saved.");
        mav.addObject("msgType", "success");
        return mav;
    }

    @RequestMapping(value = Views.DEVICE + "/add", method = RequestMethod.GET)
    public ModelAndView addDevice() {
        Device device = deviceService.prepareNewDevice();
        ModelAndView mav = new ModelAndView(Views.DEVICE_VIEW);
        mav.addObject("device", device);
        return mav;
    }

    @RequestMapping(value = Views.DEVICE + "/reload", method = RequestMethod.GET)
    public ModelAndView reloadDevices() {
        ModelAndView mav = new ModelAndView("redirect:/" + Views.DEVICE_LIST);
        if (deviceService.reloadFromConfig()) {
            mav.addObject("msg", "Successfully loaded 'users' file.");
            mav.addObject("msgType", "success");
        } else {
            mav.addObject("msg", "Error loading 'users' file.");
            mav.addObject("msgType", "danger");
        }
        mav.addObject("devices", deviceService.getAll());
        return mav;
    }
}
