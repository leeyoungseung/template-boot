package com.boot.template.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.boot.template.entity.Board;
import com.boot.template.form.BoardType;
import com.boot.template.repo.BoardRepository;

@Service
public class BoardService {
	
	@Autowired
	private BoardRepository boardRepository;
	
	public int findAllCount() {
		return (int) boardRepository.count();
	}
	
	// get boards data
	public List<Board> getAllBoard() {
		return boardRepository.findAll();
	}
	
	// create board
	public Map createBoardForm() {
		Map formMap = new LinkedHashMap<>();
		
		// Type list
		BoardType boardType1 = new BoardType(1, "Normal", true);
		BoardType boardType2 = new BoardType(2, "MemberShip", false);
		List<BoardType> boardTypeList = new ArrayList<BoardType>();
		boardTypeList.add(boardType1);
		boardTypeList.add(boardType2);
		
		formMap.put("types", boardTypeList);
		
		return formMap;
	}
	
	// create board
	public Board createBoard(Board board) {
		return boardRepository.save(board);
	}
	
	// get board one by id
	public Board getBoard(Integer no) {
		Optional<Board> board = boardRepository.findById(no);
		
		return board.isEmpty() ? null : board.get();
	}
	
	
	public void updateBoardForm(int no, Model model) throws Exception {
		// exist data in DB?
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new Exception("Not exist Board Data by no : ["+no+"]"));
		BoardType boardType1 = null;
		BoardType boardType2 = null;
		
		boardType1 = new BoardType(1, "Normal", true);
		boardType2 = new BoardType(2, "MemberShip", false);
		
		List<BoardType> boardTypeList = new ArrayList<BoardType>();
		boardTypeList.add(boardType1);
		boardTypeList.add(boardType2);
		
		// Set Form
		model.addAttribute("types", boardTypeList);
		model.addAttribute("board", board);
	}
	
	
	// update board 
	@Transactional
	public Board updateBoard(
			Integer no, Board updatedBoard) throws Exception {
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new Exception("Not exist Board Data by no : ["+no+"]"));
		board.setType(updatedBoard.getType());
		board.setTitle(updatedBoard.getTitle());
		board.setContents(updatedBoard.getContents());
		board.setUpdatedTime(new Date());
		
		Board endUpdatedBoard = boardRepository.save(board);
		return endUpdatedBoard;
	}
	
	// delete board
	public ResponseEntity<Map<String, Boolean>> deleteBoard(
			Integer no) throws Exception {
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new Exception("Not exist Board Data by no : ["+no+"]"));
		
		boardRepository.delete(board);
		Map<String, Boolean> response = new HashMap<>();
		response.put("Deleted Board Data by id : ["+no+"]", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}


}
