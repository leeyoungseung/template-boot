package com.boot.template.form;

import lombok.Data;

@Data
public class BoardTypeForm {

	private Integer id;
	private String name;
	private Boolean isValid;
	

	public BoardTypeForm(int id, String name, boolean isValid) {
		this.id = id;
		this.name = name;
		this.isValid = isValid;
	}
}
