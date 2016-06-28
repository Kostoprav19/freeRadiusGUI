package lv.freeradiusgui.controllers;

import lv.freeradiusgui.constants.Views;
import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.services.DeviceService;
import lv.freeradiusgui.validators.DeviceFormValidator;
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
@SessionAttributes("device")
public class DevicesController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceFormValidator deviceFormValidator;

    @ModelAttribute("page")
    public String page() {
        return "devices";
    }

    @ModelAttribute("allTypes")
    public List<String> allType() {
        return Device.TYPE_ALL;
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

    @RequestMapping(value = {Views.DEVICE_LIST, Views.DEVICE}, method = RequestMethod.GET)
    public String viewDevices(Model model) {
        model.addAttribute("devices", deviceService.getAll());
        return Views.DEVICE_LIST;
    }

    @RequestMapping(value = Views.DEVICE + "/{id}", method = RequestMethod.GET)
    public String showDevice(@PathVariable("id") Integer deviceId,
                             Model model) {
        Device device = deviceService.getById(deviceId);
        model.addAttribute("device", device);
        return Views.DEVICE_VIEW;
    }

    @RequestMapping(value = Views.DEVICE + "/delete/{id}", method = RequestMethod.GET)
    public String deleteDevice(@PathVariable("id") Integer deviceId,
                               final RedirectAttributes redirectAttributes) {
        Device device = deviceService.getById(deviceId);
        deviceService.delete(device);

        redirectAttributes.addFlashAttribute("msg", "Device '" + device.getName() + "' successfully deleted.");
        redirectAttributes.addFlashAttribute("msgType", "success");
        return "redirect:/" + Views.DEVICE_LIST;
    }

    @RequestMapping(value = Views.DEVICE + "/submit", method = RequestMethod.POST)
    public String storeDevice(@ModelAttribute("device") @Validated Device device,
                              BindingResult result,
                              SessionStatus status,
                              Model model,
                              final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("device", device);
            return Views.DEVICE_VIEW;
        }

        deviceService.store(device);
        status.setComplete();
        redirectAttributes.addFlashAttribute("msg", "Device '" + device.getName() + "' successfully saved.");
        redirectAttributes.addFlashAttribute("msgType", "success");
        return "redirect:/" + Views.DEVICE_LIST;
    }

    @RequestMapping(value = Views.DEVICE + "/add", method = RequestMethod.GET)
    public String addDevice(Model model) {
        Device device = deviceService.prepareNewDevice(null); //null - empty mac
        model.addAttribute("device", device);
        return Views.DEVICE_VIEW;
    }

    @RequestMapping(value = Views.DEVICE + "/add/{mac}", method = RequestMethod.GET)
    public String addDeviceWithMac(@PathVariable("mac") String mac,
                                   Model model) {
        Device device = deviceService.prepareNewDevice(mac);
        model.addAttribute("device", device);
        return Views.DEVICE_VIEW;
    }

    @RequestMapping(value = Views.DEVICE + "/reload", method = RequestMethod.GET)
    public String reloadDevices(final RedirectAttributes redirectAttributes) {
        if (deviceService.reloadFromConfig()) {
            redirectAttributes.addFlashAttribute("msg", "Successfully loaded 'users' file.");
            redirectAttributes.addFlashAttribute("msgType", "success");
        } else {
            redirectAttributes.addFlashAttribute("msg", "Error loading 'users' file.");
            redirectAttributes.addFlashAttribute("msgType", "danger");
        }
        return "redirect:/" + Views.DEVICE_LIST;
    }
}
