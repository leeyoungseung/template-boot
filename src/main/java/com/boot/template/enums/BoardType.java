package com.boot.template.enums;

public enum BoardType {

	NORMAL(1), MEMBERSHIP(2);
	
	public int value;
	
	BoardType(int value) {
		this.value = value;
	}

}
