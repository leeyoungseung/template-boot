package com.boot.template.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.boot.template.dto.ResponseDto;
import com.boot.template.entity.Member;
import com.boot.template.enums.ResponseInfo;
import com.boot.template.form.LoginForm;
import com.boot.template.repo.MemberRepository;
import com.boot.template.service.MemberService;
import com.boot.template.utils.EncUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("auth")
@Slf4j
@Validated
public class AuthController {
	
	EncUtil enc = EncUtil.getInstance();
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping(method = RequestMethod.GET, path = "/join")
	public String joinForm() {
		return "auth/join";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "/join")
	public String doJoin(@Validated LoginForm form, BindingResult bindingResult, Model model) {
		
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			StringBuilder errorStr = new StringBuilder();
			errors.forEach(err -> {
				errorStr.append("["+err+"],");
			});
			
			model.addAttribute("message", errorStr);
			model.addAttribute("nextUrl", "/auth/join");
			return "/common/message";
		}
		
		// exist and data
		Optional<Member> memberOp = memberRepo.findByMemberId(form.getMemberId());
		if (!memberOp.isEmpty()) {
			model.addAttribute("message", form.getMemberId()+" is already exist!!");
			model.addAttribute("nextUrl", "/auth/join");
			return "/common/message";
		}
		
		
		Member member = form.makeEntity();
		member.setPassword(enc.generateSHA512(member.getPassword()));
		member.setCreatedTime(new Date());
		member.setUpdatedTime(new Date());
		member.setSessionKey("unused");
		member.setSessionLimitTime(new Date(System.currentTimeMillis()));
		
		memberRepo.save(member);
		model.addAttribute("message", member.getMemberId()+"has been joined!!");
		model.addAttribute("nextUrl", "/auth/login");
		
		return "/common/message";
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public String loginForm() {
		return "auth/login";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "/login")
	public String doLogin(LoginForm form, HttpServletRequest req, HttpServletResponse res, Model model) {
		
		log.info("LoginForm Data : {} ", form.toString());
		
		// exist and data
		Optional<Member> memberOp = memberRepo.findByMemberId(form.getMemberId());
		if (memberOp.isEmpty()) {
			model.addAttribute("message", form.getMemberId()+" is not exist!!");
			model.addAttribute("nextUrl", "/auth/join");
			return "/common/message";
		}
		
		// Password OK?
		if (!memberOp.get().getPassword().equals(enc.generateSHA512(form.getPassword()))) {
			model.addAttribute("message", "Unmatch Password!!");
			model.addAttribute("nextUrl", "/auth/login");
			return "/common/message";
		}
		
		// Set User Data in Session
		HttpSession session = req.getSession();
		session.setAttribute("member", memberOp.get());
		memberOp.get().setUpdatedTime(new Date());
		
		// If it checked UseAutoLogin, save SessionId in Cookie and Database. for Auto Login.
		if (form.isUseAutoLogin()) {
			log.info("set UseAutoLogin Cookie");
			Cookie authCookie = new Cookie("authCookie", session.getId());
			authCookie.setPath("/");
			authCookie.setMaxAge(3000);
			
			res.addCookie(authCookie);
			
			memberOp.get().setSessionKey(session.getId());
			Date sessionLimitTime = new Date(System.currentTimeMillis() + (1000 * 3000));
			memberOp.get().setSessionLimitTime(sessionLimitTime);
			
		} else {
			memberOp.get().setSessionKey("unused");
			memberOp.get().setSessionLimitTime(new Date(System.currentTimeMillis()));
		}
		
		log.info("Save login user info {} : ", memberOp.get().toString());
		memberRepo.save(memberOp.get());
		
		model.addAttribute("message", "Login Success!!");
		model.addAttribute("nextUrl", "/");

		return "/common/message";
	}
	

	@RequestMapping(method = RequestMethod.GET, path = "/logout")
	public String logout(HttpServletRequest req, HttpServletResponse res, Model model) {
		
		HttpSession session = req.getSession();
		Object obj = session.getAttribute("member");
		
		if (obj != null) {
			session.removeAttribute("member");
			session.invalidate();
			Cookie authCookie = WebUtils.getCookie(req, "authCookie");
			if (authCookie != null) {
				authCookie.setPath("/");
				authCookie.setMaxAge(0);
				res.addCookie(authCookie);
				
				Member member = memberService.getMemberInfoByMemberId(((Member)obj).getMemberId());
				member.setSessionKey("unused");
				member.setSessionLimitTime(new Date(System.currentTimeMillis()));
				
				memberService.updateMember(member);
			}
			
		}

		
		model.addAttribute("message", "Logout Success!!");
		model.addAttribute("nextUrl", "/");
		
		return "/common/message";
	}
	
	
	@PostMapping(path = "/checkId")
	@ResponseBody
	public ResponseEntity<?> checkId(
			@RequestParam(required = true,
			name = "memberId") 
			@Email 
			@NotBlank 
			String memberId
			) {
		
		log.info("Input memberId : {}", memberId);
		
		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SUCCESS.getResultCode())
				.message(ResponseInfo.SUCCESS.getMessage())
				.data(memberService.existMemberId(memberId))
				.build()
				);
	}
	
	@ExceptionHandler
	public ResponseEntity<?> validationHandler(HttpServletRequest req, ConstraintViolationException e) {
		
		log.error("Error Message : {}", e.getMessage() , e);
		
		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.PARAM_ERROR.getResultCode())
				.message(ResponseInfo.PARAM_ERROR.getMessage())
				.data(e.getMessage())
				.build()
				);
	}
}
