package com.boot.template.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "member")
@Data
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_no")
	private Integer memberNo;
	
	@Column(name = "member_id")
	private String memberId;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "created_time")
	private Date createdTime;
	
	@Column(name = "updated_time")
	private Date updatedTime;
	
	@Column(name = "role")
	private String role;
	
	@Column(name = "session_key")
	private String sessionKey;
	
	@Column(name = "session_limit_time")
	private Date sessionLimitTime;

	@Column(name = "profile_picture")
	private String profilePicture;
	
}
