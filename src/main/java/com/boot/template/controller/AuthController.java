package com.boot.template.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
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
	public String doLogin(LoginForm form, HttpSession session, Model model) {
		
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
		session.setAttribute("member", memberOp.get());
		memberOp.get().setUpdatedTime(new Date());
		memberRepo.save(memberOp.get());
		
		model.addAttribute("message", "Login Success!!");
		model.addAttribute("nextUrl", "/");

		return "/common/message";
	}
	

	@RequestMapping(method = RequestMethod.GET, path = "/logout")
	public String logout(HttpSession session, Model model) {
		
		session.invalidate();
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
