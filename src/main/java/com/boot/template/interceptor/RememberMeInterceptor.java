/**
 * @author lee-y
 * Only Use Auto-Login
 * To apply this Interceptor, You have to add setting in WebConfig that implements WebMvcConfigurer.
 * 
 * Login of Auto-Login
 * 1. allow Auto-Login on login form.
 * 2. create a sessionId setting up limit-time and save sessionId and sessionId's limit-time both Cookie(member) and DB.
 * 3. If sessionId in null in Session and exist valid sessionId in Cookie, RememberMeInterceptor make user state of login.
 * 
 */
package com.boot.template.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.WebUtils;

import com.boot.template.entity.Member;
import com.boot.template.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RememberMeInterceptor implements HandlerInterceptor {
	
	@Autowired
	private MemberService memberService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("RememberMeInterceptor Start");
		String requestedUri = request.getRequestURI().isBlank() ? "/" : request.getRequestURI();
		HttpSession session = request.getSession(false);
		
		log.info("requestedUri : {} ", requestedUri);
		
		if (session == null || session.getAttribute("member") == null) {
			log.info("SessionKey is Null");
			Cookie authCookie = WebUtils.getCookie(request, "authCookie");
			
			if (authCookie != null) {
				String sessionKey = authCookie.getValue();
				log.info("SessionKey from Cookie : {}", sessionKey);
				
				Member member = memberService.getMemberInfoBySessionKey(sessionKey);
				
				if (member == null) {
					log.info("inValid Cookie");
					authCookie.setPath("/");
					authCookie.setMaxAge(0);
					response.addCookie(authCookie);
					
					response.sendRedirect(requestedUri);
					return true;
				}
				
				session = request.getSession();
				session.setAttribute("member", member);
				log.info("Pre-SessionKey : {} , New-SessionKey {}", sessionKey, session.getId());
				
				response.sendRedirect(requestedUri);
				return true;
			}
			
			log.info("Cookie is Null");
			
		}
		
		return true;
	}
}
