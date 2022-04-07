package com.boot.template.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.boot.template.interceptor.AuthInterceptor;
import com.boot.template.interceptor.RememberMeInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Autowired
	private AuthInterceptor authInterceptor;
	
	@Autowired
	private RememberMeInterceptor rememberMeInterceptor;
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
			.order(0)
			.addPathPatterns(
					"/**/delete/**",
					"/**/update/**"
					)
			.excludePathPatterns("/","/auth/**","/common/**")
			;
		registry.addInterceptor(rememberMeInterceptor)
		    .order(1)
		    .addPathPatterns("/board/**")
		    .excludePathPatterns("/auth/logout", "/auth/join", "/common/**")
		    ;
	}

}
