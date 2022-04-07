/**
 * @author lee-y
 * Only Use Limit function of this application. such as update, delete. 
 * If it accessed by who is unauthenticated, redirect login page. 
 * To apply this Interceptor, You have to add setting in WebConfig that implements WebMvcConfigurer.
 */
package com.boot.template.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import com.boot.template.entity.Member;
import com.boot.template.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
	
	@Autowired
	private MemberService memberService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		log.info("AuthInterceptor Start");
		String requestedUri = request.getRequestURI().isBlank() ? "/" : request.getRequestURI();
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("member") == null) {
			
			Cookie authCookie = WebUtils.getCookie(request, "authCookie");
			
			if (authCookie != null) {
				String sessionKey = authCookie.getValue();
				Member member = memberService.getMemberInfoBySessionKey(sessionKey);
				
				if (member == null) {
					log.info("inValid Cookie");
					response.sendRedirect("/auth/login?redirectedUri="+requestedUri);
					return false;
				}
				
				session = request.getSession();
				session.setAttribute("member", member);
				log.info("Pre-SessionKey : {} , New-SessionKey : {}", sessionKey, session.getId());
				
				response.sendRedirect(requestedUri);
				return true;
			}
			
			log.info("request from guest");
			response.sendRedirect("/auth/login?redirectedUri="+requestedUri);
			return false;
		}
		
		return true;
	}
}
