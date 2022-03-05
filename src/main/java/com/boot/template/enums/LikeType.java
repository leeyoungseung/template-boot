package com.boot.template.enums;

public enum LikeType {

	BOARD("board"), REPLY("reply");
	
	public String value;
	
	LikeType(String value) {
	    this.value = value;
	}
}
