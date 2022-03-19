package com.boot.template.enums;

import java.util.ArrayList;
import java.util.List;

public enum BoardType {

	NORMAL(1, "Normal"), MEMBERSHIP(2, "MemberShip");
	
	public Integer value;
	public String name;
	private static List<BoardType> typeList = null;
	
	BoardType(Integer value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public static List<BoardType> getBoardTypes() {
		if (typeList == null) { 
			typeList = new ArrayList<>();
			typeList.add(NORMAL);
			typeList.add(MEMBERSHIP);
		} 
		
		return typeList;
	}

}
