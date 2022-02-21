package com.boot.template.form;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.boot.template.entity.Member;

import lombok.Data;

@Data
public class LoginForm {

	@NotBlank(message = "ID is mandatory")
	@Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Unsuitable inputed ID")
	private String memberId;
	
	@NotBlank(message = "Password is mandatory")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Unsuitable inputed Password")
	private String password;
	private Date createdTime;
	private Date updatedTime;
	
	public Member makeEntity() {
		Member member = new Member();
		member.setMemberId(memberId);
		member.setPassword(password);
		return member;
	}
	
}
