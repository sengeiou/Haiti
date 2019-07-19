package com.aimir.web;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;

public class MessageFilter implements Filter {

    Log log = LogFactory.getLog(MessageFilter.class);
    
    private Pattern otherLoginPathPattern = Pattern.compile(".*/admin/[\\S]+/login.*");
    
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain)
    throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // grab messages from the session and put them into request
        // this is so they're not lost in a redirect
        Object message = request.getSession().getAttribute("message");

        if (message != null) {
            request.setAttribute("message", message);
            request.getSession().removeAttribute("message");
        }
        
        AimirUser user = getUser(request, response);
        if (user == null) {
            String uri = request.getRequestURI().toString();
            
            String firmware_check = "false";
            if(uri.indexOf("firmwareDown.jsp")>0||uri.indexOf("downServlet")>0||uri.indexOf("downfw.jsp")>0){
                //firmwareDown.jsp 는 외부에서 펌웨어를 다운로드 하여야 하기때문에 로그인 체크가 필요 없다.
            	firmware_check = "true";
            }
            
            String ext = "";
            if (uri.lastIndexOf(".") != -1) {
                ext=uri.substring(uri.lastIndexOf("."));
                //log.info("ext : " + ext);
            }
            	
            if (ext.length() == 0 || ext.equals(".do") || ext.equals(".jsp")) {  // Check login
                if(firmware_check.equals("false")){
                    if (!request.getServletPath().contains("/report") 
                            && !request.getServletPath().contains("/admin/login")
                            && !otherLoginPathPattern.matcher(request.getServletPath()).matches() 
                            && !request.getServletPath().contains("/customer/login") 
                            && !request.getServletPath().contains("/gadget/system/membership")
                            && !request.getServletPath().contains("/services")) {
                        /*if (request.getServerPort()!=8443) {
                            request.getSession().setAttribute("requestPort", String.valueOf(request.getLocalPort()));
                            //log.info("세션포트="+request.getSession().getAttribute("requestPort"));
                        }*/
                        //log.info("SERVLET PATH : "+request.getServletPath());
                        if(request.getServletPath().contains("/admin/login")
                                || request.getServletPath().contains("/gadget/index.jsp")
                                || request.getServletPath().contains("/admin/logout")) {
                            //response.sendRedirect("https://"+request.getServerName().toString()+":8443"+request.getContextPath().toString()+"/admin/login.do");
                            //response.sendRedirect("http://"+request.getServerName().toString()+":"+request.getSession().getAttribute("requestPort")+request.getContextPath().toString()+"/admin/login.do");
                            response.sendRedirect(request.getContextPath().toString()+"/admin/login.do");
                        } else {
                            //response.sendRedirect("https://"+request.getServerName().toString()+":8443"+request.getContextPath().toString()+"/customer/login.do");
                            //response.sendRedirect("http://"+request.getServerName().toString()+":"+request.getSession().getAttribute("requestPort")+request.getContextPath().toString()+"/customer/login.do");
                            response.sendRedirect(request.getContextPath().toString()+"/customer/login.do");
                        }
                    }
                }
            }
        } else {
            /*if (request.getServerPort()==8443) {
                String url = request.getRequestURL().toString();
                url = url.replaceAll("https", "http");
                url = url.replaceAll("8443", ""+request.getSession().getAttribute("requestPort"));
                response.sendRedirect(url);
            }*/
        }
        // set the requestURL as a request attribute for templates
        // particularly freemarker, which doesn't allow request.getRequestURL()
        request.setAttribute("requestURL", request.getRequestURL());

        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }
    
    public AimirUser getUser(HttpServletRequest request, HttpServletResponse response) {
        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = null;
        
        try{
            user = (AimirUser)instance.getUserFromSession();
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return user;
    }
}
