package com.boot.template.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.boot.template.entity.Like;
import com.boot.template.enums.LikeType;

import lombok.Data;

@Data
public class LikeForm {

	private Integer likeId;
	
	@NotNull
	private boolean likeStatus;
	
	@NotBlank(message = "LikeType is mandatory")
	private String likeType;
	
	@NotNull
	private Integer typeNo;
	
	@NotBlank(message = "ID is mandatory")
	@Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Unsuitable inputed ID")
	private String memberId;
	
	public Like toEntity() throws Exception {
		Like like = new Like();
		if (likeType.equals(LikeType.BOARD.value)) {
			like.setBoardNo(typeNo);
			
		} else if (likeType.equals(LikeType.REPLY.value)) {
			like.setReplyNo(typeNo);
			
		} else {
			like = null; return like;
		}
		
		like.setLikeStatus(likeStatus);
		like.setLikeType(likeType);
		like.setMemberId(memberId);
		
		return like;
	}
	
}
