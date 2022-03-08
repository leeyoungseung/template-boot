package com.boot.template.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
import com.boot.template.exception.ProcFailureException;
import com.boot.template.exception.UnvalidParamException;
import com.boot.template.form.BoardTypeForm;
import com.boot.template.repo.BoardRepository;
import com.boot.template.repo.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	public Board createBoard(Board board) {
		
		validationBoardType(board);
		
		if (true) {
			throw new ProcFailureException("Failure Create Board");		
		}
		
		return boardRepository.save(board);
	}
	
	private void validationBoardType(Board board) {
		
		// is MemberShip Board?
		if (board.getType() == BoardType.MEMBERSHIP.value) {
			
			// has MemberId from param?
			if (board.getMemberId() == null || board.getMemberId().equals("")) {
				throw new UnvalidParamException("MemberId Null!!");
			}
			
			// exist MemberId?
			Optional<Member> data = memberRepository.findByMemberId(board.getMemberId());
			if (data.isEmpty()) {
				throw new NoSuchElementException("MemberId Empty!!");
			}
			
			// Validation Ok
			
		} else if (board.getType() == BoardType.NORMAL.value) {
			// Validation Ok
			
		} else {
			throw new UnvalidParamException("Validation Board Type Error!!");
		}
		
	}
	
	
	// get board one by id
	public Board getBoard(Integer no) {
		Optional<Board> board = boardRepository.findById(no);
		
		if (board.isEmpty()) {
			throw new NoSuchElementException("Not exist Board !! ["+no+"]");
		}
		
		return board.get();
	}
	
	
	public void updateBoardForm(int no, Model model) {
		// exist data in DB?
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new NoSuchElementException("Not exist Board Data by no : ["+no+"]"));
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
			Integer no, Board updatedBoard) {
		
		validationBoardType(updatedBoard);
		
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new NoSuchElementException("Not exist Board Data by no : ["+no+"]"));
		
		if (!board.getMemberId().equals(updatedBoard.getMemberId())) {
			throw new UnvalidParamException("Unmatch MemberId!!");
		}
		
		board.setType(updatedBoard.getType());
		board.setTitle(updatedBoard.getTitle());
		board.setContents(updatedBoard.getContents());
		board.setUpdatedTime(new Date());
		
		log.info("updateBoard_data : "+board.toString());
		
		Board endUpdatedBoard = boardRepository.save(board);
		return endUpdatedBoard;
	}
	
	// delete board
	public ResponseEntity<Map<String, Boolean>> deleteBoard(
			Integer no, String memberId) {
		Board board = boardRepository.findById(no)
				.orElseThrow(() -> new NoSuchElementException("Not exist Board Data by no : ["+no+"]"));
		
		if (!board.getMemberId().equals(memberId)) {
			throw new UnvalidParamException("Unmatch MemberId!!");
		}
		
		boardRepository.delete(board);
		Map<String, Boolean> response = new HashMap<>();
		response.put("Deleted Board Data by id : ["+no+"]", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}


}
