package lv.freeradiusgui.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lv.freeradiusgui.config.AppConfig;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.serverServices.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class SessionVariablesInterceptor implements HandlerInterceptor {

    @Autowired ServerService serverService;

    @Autowired AppConfig appConfig;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object o,
            ModelAndView modelAndView)
            throws Exception {
        if (serverService != null && modelAndView != null && modelAndView.hasView()) {
            String viewName = modelAndView.getViewName();
            if (viewName != null && viewName.startsWith("redirect:")) {
                return;
            }
            modelAndView.addObject(
                    "todayRejectedCount", serverService.getRejectedLogsTodayCounter());
            modelAndView.addObject("freeradiusStatus", serverService.getStatus(Server.FREERADIUS));
            modelAndView.addObject("dbChangesFlag", serverService.getDbgChangesFlag());
            modelAndView.addObject("appVersion", appConfig.getAppVersion());
        }
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object o, Exception e)
            throws Exception {}
}
