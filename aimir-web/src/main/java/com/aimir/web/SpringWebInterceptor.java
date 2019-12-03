package com.aimir.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.aimir.util.AimirThreadLocal;

public class SpringWebInterceptor extends HandlerInterceptorAdapter{
 
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
		 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView mav) throws Exception {
    	String sequence = AimirThreadLocal.sequence.get();
    	if(sequence != null && sequence !="") {
//    		log.info("### This is Seq Interceptor   : "+sequence);
    		mav.addObject("sequence", sequence);
    		mav.addObject("URL", "http://172.16.10.180:8082/log-list?sequence=" + sequence);
    	}
    	AimirThreadLocal.sequence.remove();
        super.postHandle(request, response, handler, mav);
    }
     
}
