package com.boot.template.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDto {

	private String resultCode;
	private String message;
	private Object data;
}
