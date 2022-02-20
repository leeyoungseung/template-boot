package com.boot.template.form;

import com.boot.template.entity.Board;

import lombok.Data;

@Data
public class BoardForm {
	
	private Integer no;
	private Integer type;
	private String title;
	private String contents;
	private Integer memberNo;
	
	public Board makeEntity() {
		Board board = new Board();
		board.setType(this.type);
		board.setTitle(this.title);
		board.setContents(this.contents);
		board.setMemberNo((this.memberNo == null ? 0 : this.memberNo));
		board.setLikes(0);
		board.setCounts(0);
		return board;
	}
}
