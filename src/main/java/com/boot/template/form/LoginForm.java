package com.boot.template.form;

import java.util.Date;

import com.boot.template.entity.Member;

import lombok.Data;

@Data
public class LoginForm {

	private String memberId;
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
