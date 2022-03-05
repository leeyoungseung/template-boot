package com.boot.template.dto;

import lombok.Data;

@Data
public class LikeStatusDto {

	private Integer likeCount;
	
	private Integer dislikeCount;
	
	private Boolean likeStatus;
	
	private Boolean dislikeStatus;
}
