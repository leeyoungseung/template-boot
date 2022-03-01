package com.boot.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.boot.template.form.BoardForm;
import com.boot.template.service.BoardService;

@Controller
@RequestMapping(path = "/board")
public class BoardController {

	@Autowired
	BoardService boardService;
	
	@RequestMapping(method = RequestMethod.GET, path = "list")
	public String viewBoardList(Model model) {
		
		model.addAttribute("boards", boardService.getAllBoard());
		
		return "boards/list";
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "read/{boardNo}")
	public String viewBoardOne(@PathVariable int boardNo, Model model) {
		
		model.addAttribute("board", boardService.getBoard(boardNo));

		return "boards/read";
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "write")
	public String writeBoard(Model model) {
		
		model.addAttribute("boardForm", boardService.createBoardForm());
		
		return "boards/write";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "write")
	public String writeCompleteBoard(BoardForm form, Model model) {
		Integer no = null;
		try {
			no = boardService.createBoard(form.makeEntity()).getBoardNo();
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Not exist data");
			return "redirect:boards/write";
		}
		
		if (no == null) {
			model.addAttribute("error", "Not exist data Number");
			return "redirect:boards/write";
		}

		return "redirect:/board/read/" + no;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "update/{boardNo}")
	public String updateBoard(@PathVariable int boardNo, Model model) {
		try {
			
			boardService.updateBoardForm(boardNo, model);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Not exist data");
			return "redirect:/board/read/"+boardNo;
		}
		
		return "boards/update";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "update/{boardNo}")
	public String updateCompleteBoard(@PathVariable int boardNo, BoardForm form,  Model model) {
		try {
			System.out.println("updateBoard Param : "+form.toString());
			boardService.updateBoard(boardNo, form.makeEntity());
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "Not exist data");
			model.addAttribute("nextUrl", "/board/read/"+boardNo);
			
			return "/common/message";
		}
		
		model.addAttribute("message", "Update Success");
		return "redirect:/board/read/"+boardNo;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "delete/{boardNo}")
	public String deleteBoard(@PathVariable int boardNo, Model model) {
		try {
			model.addAttribute("board", boardService.getBoard(boardNo));
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Not exist data");
			return "redirect:/board/read/"+boardNo;
		}
		
		return "boards/delete";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "delete")
	public String deleteCompleteBoard(@RequestParam(name = "boardNo", required = true) int boardNo,
			@RequestParam(name = "memberId", required = false, value = "") String memberId, 
			Model model) {
		
		try {
			boardService.deleteBoard(boardNo, memberId);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Not exist data or Unmatched MemberId");
			return "redirect:/board/read/"+boardNo;
		}
		
		model.addAttribute("message", "Delete Success");
		return "redirect:/board/list";
	}
	
}
