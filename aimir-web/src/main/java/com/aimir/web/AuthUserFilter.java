package com.aimir.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Arrays;
import javax.servlet.http.Cookie;

import org.owasp.esapi.ESAPI;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;

public class AuthUserFilter implements Filter {
    
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain)
    throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = null;

        try{
        	user = (AimirUser)instance.getUserFromSession();
        } catch(Exception e){
        	e.printStackTrace();
        }

        String url = request.getRequestURL().toString();
        System.out.println(url);
        int port = request.getLocalPort();
        
        
        if(user==null)
        	System.out.println("user is null");
        
        if(user!=null && user.isAnonymous())
        	System.out.println("userisAnonymous()::"+user.isAnonymous());
    	
        if(((user==null || user.isAnonymous()) && !(request.getServletPath().contains("/admin/login")))
        	||( port!=8443 && request.getServletPath().contains("/admin/login")) ) {
        	
        	if(port!=8443){
	        	url = url.replaceAll("http", "https");
	        	url = url.replaceAll(""+request.getLocalPort(), "8443");
	        	
	        //	Cookie cookie = new ("myPort", ) 
	        	if(request.getCookies() != null){
	                Iterator<Cookie> it = Arrays.asList(request.getCookies()).iterator();
	                Cookie cookie = null;
	                boolean hasId = false;
	                while(it.hasNext()){
	                    cookie = (Cookie)it.next();

	                    if("myPort".equals(cookie.getName())){    
	                    	hasId = true;
	                    	break;
	                    }
	                }
	                if(!hasId)
	                	response.addCookie(new Cookie("myPort", ""+port));
	        	}
	        	            
        	}

        	url = url.replaceAll(request.getServletPath(), "/admin/login.do");
        	System.out.println("url :: "+ url);
        	
        	response.sendRedirect(url);
        	
        }
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }
}
