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
@Table(name = "likes")
@Data
public class Like {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "likes_no")
	private Integer likeNo;
	
	@Column(name = "likes_status")
	private boolean likeStatus;
	
	@Column(name = "likes_type")
	private String likeType;
	
	@Column(name = "created_time")
	private Date createdTime;
	
	@Column(name = "updated_time")
	private Date updatedTime;
	
	@Column(name = "board_no")
	private Integer boardNo;
	
	@Column(name = "reply_no")
	private Integer replyNo;
	
	@Column(name = "member_id")
	private String memberId;
	
	
}
