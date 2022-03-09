package com.boot.template.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.boot.template.entity.Board;
import com.boot.template.exception.ProcFailureException;
import com.boot.template.form.BoardForm;
import com.boot.template.service.BoardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(path = "/board")
public class BoardController {
	
	
	@ExceptionHandler
	public ModelAndView procFailureErrorHandler(HttpServletRequest req, ProcFailureException e) {
		ModelAndView mav = new ModelAndView();
		
		String message = e.getMessage();
		String redirectUrl = (req.getHeader("referer") == null ||req.getHeader("referer").isBlank()) ?  "boards/list" : req.getHeader("referer");
		String view = "/common/error";
		
		mav.addObject("message", message);
		mav.addObject("redirectUrl", redirectUrl);
		mav.setViewName(view);
		
		log.error("Message : {}, RedirectUrl : {}, View : {}", message, redirectUrl, view);
		
		return mav;
	}
	

	@Autowired
	BoardService boardService;
	
	@RequestMapping(method = RequestMethod.GET, path = "list")
	public String viewBoardList(Model model, Pageable pageable) {
		
		log.info("PageNumber [{}], PageSize [{}] ", pageable.getPageNumber(), pageable.getPageSize());
		
		Page<Board> pageInfo = boardService.getAllBoard(pageable);
		
		model.addAttribute("pageInfo", pageInfo);
		
		return "boards/list";
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "read/{boardNo}")
	public String viewBoardOne(@PathVariable int boardNo, Model model) throws Exception {
		
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
			no = boardService.createBoard(form.makeEntity()).getBoardNo();
		
		if (no == null) {
			throw new ProcFailureException("Failure Create Board");
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
		
		log.info("updateBoard Param : {} ", form.toString());
		boardService.updateBoard(boardNo, form.makeEntity());
		
		model.addAttribute("message", "Update Success");
		
		return "redirect:/board/read/"+boardNo;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, path = "delete/{boardNo}")
	public String deleteBoard(@PathVariable int boardNo, Model model) {
		
		model.addAttribute("board", boardService.getBoard(boardNo));

		return "boards/delete";
	}
	
	
	@RequestMapping(method = RequestMethod.POST, path = "delete")
	public String deleteCompleteBoard(@RequestParam(name = "boardNo", required = true) int boardNo,
		@RequestParam(name = "memberId", required = false, value = "") String memberId, 
			Model model) {

		boardService.deleteBoard(boardNo, memberId);
		
		model.addAttribute("message", "Delete Success");
		return "redirect:/board/list";
	}
	
}
