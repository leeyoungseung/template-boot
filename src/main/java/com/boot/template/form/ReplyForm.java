package com.boot.template.form;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.boot.template.entity.Reply;

import lombok.Data;
@Data
public class ReplyForm {

	private Integer no;
	
	@NotBlank(message = "Please input contents")
	private String contents;
	
	@NotNull(message = "ID is mandatory")
	private Integer boardNo;
	
	@NotBlank(message = "ID is mandatory")
	@Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Unsuitable inputed ID")
	private String memberId;
	
	private Date createdTime;
	private Date updatedTime;
	
	public Reply toEntity() {
		Reply reply = new Reply();
		if (no != null) reply.setNo(no);
		reply.setContents(contents);
		reply.setBoardNo(boardNo);
		reply.setMemberId(memberId);
		return reply;
	}
	
}
