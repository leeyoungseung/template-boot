package com.boot.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.boot.template.from.BoardFrom;
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
	
	
	@RequestMapping(method = RequestMethod.GET, path = "read/{no}")
	public String viewBoardOne(@PathVariable int no, Model model) {
		
		model.addAttribute("board", boardService.getBoard(no));

		return "boards/read";
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "write")
	public String writeBoard(Model model) {
		
		model.addAttribute("boardForm", boardService.createBoardForm());
		
		return "boards/write";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "write")
	public String writeCompleteBoard(BoardFrom form) {

		return "redirect:/board/read/" + boardService.createBoard(form.makeEntity()).getNo();
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "update/{no}")
	public String updateBoard(@PathVariable int no, Model model) {
		try {
			boardService.updateBoardForm(no, model);
			
		} catch (Exception e) {
			model.addAttribute("error", "Not exist data");
			return "redirect:/board/read/"+no;
		}
		
		return "boards/update";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "update/{no}")
	public String updateCompleteBoard(@PathVariable int no, BoardFrom form,  Model model) {
		try {
			boardService.updateBoard(no, form.makeEntity());
			
		} catch (Exception e) {
			model.addAttribute("error", "Not exist data");
			return "redirect:/board/read/"+no;
		}
		
		model.addAttribute("message", "Update Success");
		return "redirect:/board/read/"+no;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "delete/{no}")
	public String deleteBoard(@PathVariable int no, Model model) {
		try {
			model.addAttribute("board", boardService.getBoard(no));
			
		} catch (Exception e) {
			model.addAttribute("error", "Not exist data");
			return "redirect:/board/read/"+no;
		}
		
		return "boards/delete";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "delete")
	public String deleteCompleteBoard(@RequestParam(name = "no", required = true) int no, Model model) {
		try {
			boardService.deleteBoard(no);
			
		} catch (Exception e) {
			model.addAttribute("error", "Not exist data");
			return "redirect:/board/read/"+no;
		}
		
		model.addAttribute("message", "Delete Success");
		return "redirect:/board/list";
	}
	
}
