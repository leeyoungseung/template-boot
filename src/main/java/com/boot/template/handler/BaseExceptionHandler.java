package com.boot.template.handler;

import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.boot.template.controller.BoardController;
import com.boot.template.controller.IndexController;
import com.boot.template.exception.UnvalidParamException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice(assignableTypes = {BoardController.class, IndexController.class})
public class BaseExceptionHandler {

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public ModelAndView processErrorHandler(HttpServletRequest req, Exception e) {
		ModelAndView mav = new ModelAndView();
		
		String message = e.getMessage();
		String redirectUrl = "/boards/list";
		String view = "/common/error";
		
		mav.addObject("message", message);
		mav.addObject("redirectUrl", redirectUrl);
		mav.setViewName(view);
		
		log.error("processErrorHandler Message : {}, RedirectUrl : {}, View : {}", message, redirectUrl, view);
		
		return mav;
	}
	
	
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler
	public ModelAndView notFoundErrorHandler(HttpServletRequest req, NoSuchElementException e) {
		ModelAndView mav = new ModelAndView();
		
		String message = e.getMessage();
		String redirectUrl = (req.getHeader("referer") == null ||req.getHeader("referer").isBlank()) ?  "boards/list" : req.getHeader("referer");
		String view = "/common/error";
		mav.addObject("message", message);
		mav.addObject("redirectUrl", redirectUrl);
		mav.setViewName(view);
		
		log.error("Message : {}, RedirectUrl : {}, View : {}", message, redirectUrl,  view);
		
		return mav;
	}
	
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ModelAndView badRequestErrorHandler(HttpServletRequest req, UnvalidParamException e) {
		ModelAndView mav = new ModelAndView();
		
		String message = e.getMessage();
		String redirectUrl = (req.getHeader("referer") == null ||req.getHeader("referer").isBlank()) ?  "boards/list" : req.getHeader("referer");
		String view = "/common/error";
		
		mav.addObject("message", message);
		mav.addObject("redirectUrl", redirectUrl);
		mav.setViewName(view);
		
		log.error("Message : {}, RedirectUrl : {}, View : {}", message, redirectUrl, view);
		
		return mav;
	}
	
}
