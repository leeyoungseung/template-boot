package com.boot.template.from;

import lombok.Data;

@Data
public class BoardType {

	private Integer id;
	private String name;
	private Boolean isValid;
	

	public BoardType(int id, String name, boolean isValid) {
		this.id = id;
		this.name = name;
		this.isValid = isValid;
	}
}