package lv.freeradiusgui.interceptors;

import lv.freeradiusgui.domain.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Daniels on 08.09.2016..
 */
public class SessionVariablesInterceptor implements HandlerInterceptor {

    @Autowired
    Server server;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
        if (server != null){
            request.getSession().setAttribute("todayRejectedCount", server.getTodayRejectedCount());
            request.getSession().setAttribute("freeradiusStatus", server.getStatus(Server.FREERADIUS));
            request.getSession().setAttribute("dbChangesFlag", server.getDbgChangesFlag());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }
}
