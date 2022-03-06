package com.boot.template.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.boot.template.entity.Board;
import com.boot.template.entity.Member;
import com.boot.template.enums.BoardType;
import com.boot.template.form.BoardTypeForm;
import com.boot.template.repo.BoardRepository;
import com.boot.template.repo.MemberRepository;

@Service
public class BoardService {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	
	public int findAllCount() {
		return (int) boardRepository.count();
	}
	
	// get boards data
	public Page<Board> getAllBoard(Pageable pageable) {
		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
		pageable = PageRequest.of(page, 10);
		
		return boardRepository.findAll(pageable);
	}
	
	// create board
	public Map createBoardForm() {
		Map formMap = new LinkedHashMap<>();
		
		// Type list
		BoardTypeForm boardType1 = new BoardTypeForm(1, "Normal", true);
		BoardTypeForm boardType2 = new BoardTypeForm(2, "MemberShip", false);
		List<BoardTypeForm> boardTypeList = new ArrayList<BoardTypeForm>();
		boardTypeList.add(boardType1);
		boardTypeList.add(boardType2);
		
		formMap.put("types", boardTypeList);
		
		return formMap;
	}
	
	// create board
	public Board createBoard(Board board) throws Exception {
		
		if (!validationBoardType(board)) {
			throw new Exception("validationBoardType Error!!");
		}
		
		return boardRepository.save(board);
	}
	
	private boolean validationBoardType(Board board) throws Exception {
		
		// is MemberShip Board?
		if (board.getType() == BoardType.MEMBERSHIP.value) {
			
			// has MemberId from param?
			if (board.getMemberId() == null || board.getMemberId().equals("")) {
				throw new Exception("MemberId Null!!");
			}
			
			// exist MemberId?
			Optional<Member> data = memberRepository.findByMemberId(board.getMemberId());
			if (data.isEmpty()) {
				throw new Exception("MemberId Empty!!");
			}
			
			return true;
			
		} else if (board.getType() == BoardType.NORMAL.value) {
			return true;
			
		} else {
			return false;
		}
		
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
		BoardTypeForm boardType1 = null;
		BoardTypeForm boardType2 = null;
		
		boardType1 = new BoardTypeForm(1, "Normal", true);
		boardType2 = new BoardTypeForm(2, "MemberShip", false);
		
		List<BoardTypeForm> boardTypeList = new ArrayList<BoardTypeForm>();
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
		
		
		if (!validationBoardType(updatedBoard)) {
			throw new Exception("validationBoardType Error!!");
		}
		
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new Exception("Not exist Board Data by no : ["+no+"]"));
		
		if (!board.getMemberId().equals(updatedBoard.getMemberId())) {
			throw new Exception("Unmatch MemberId!!");
		}
		
		board.setType(updatedBoard.getType());
		board.setTitle(updatedBoard.getTitle());
		board.setContents(updatedBoard.getContents());
		board.setUpdatedTime(new Date());
		
		System.out.println("updateBoard_data : "+board.toString());
		
		Board endUpdatedBoard = boardRepository.save(board);
		return endUpdatedBoard;
	}
	
	// delete board
	public ResponseEntity<Map<String, Boolean>> deleteBoard(
			Integer no, String memberId) throws Exception {
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new Exception("Not exist Board Data by no : ["+no+"]"));
		
		if (!board.getMemberId().equals(memberId)) {
			throw new Exception("Unmatch MemberId!!");
		}
		
		boardRepository.delete(board);
		Map<String, Boolean> response = new HashMap<>();
		response.put("Deleted Board Data by id : ["+no+"]", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}


}
