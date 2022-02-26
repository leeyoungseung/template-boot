package com.boot.template.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.boot.template.dto.ResponseDto;
import com.boot.template.entity.Reply;
import com.boot.template.enums.ResponseInfo;
import com.boot.template.form.ReplyForm;
import com.boot.template.service.ReplyService;


@RestController
@RequestMapping(path = "/reply")
public class ReplyController {

	
	@Autowired
	private ReplyService replyService;
	
	
	@GetMapping(path = "list/{boardNo}")
	public ResponseEntity<?> viewReplyListByBoardNo(
			@PathVariable(required = true) int boardNo) {
		
		ResponseDto resultDto = null;
		List<Reply> replyList = null;
		
		
		try {
			replyList = replyService.getReplyAllByBoardNo(boardNo);
			
			if (replyList == null || replyList.size() == 0) {
				System.err.println(ResponseInfo.NO_CONTENT.getMessage());
				
				
				return ResponseEntity.ok(ResponseDto.builder()
						.resultCode(ResponseInfo.NO_CONTENT.getResultCode())
						.message(ResponseInfo.NO_CONTENT.getMessage())
						.data(replyList)
						.build()
						);
			} 
			
			resultDto = ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(replyList)
					.build();
			
			return ResponseEntity.ok(resultDto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	}
	
	
	@PostMapping
	public ResponseEntity<?> createReply(
			@Validated @RequestBody ReplyForm form, 
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			StringBuilder errorStr = new StringBuilder();
			errors.forEach(err -> {
				errorStr.append("["+err+"],\n");
			});
			
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.PARAM_ERROR.getResultCode())
					.message(ResponseInfo.PARAM_ERROR.getMessage())
					.data(new Boolean(false))
					.build()
					);
		}
		
		
		if (replyService.createReply(form.toEntity())) {
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(new Boolean(true))
					.build()
					);
		}
		
		
		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SERVER_ERROR.getResultCode())
				.message(ResponseInfo.SERVER_ERROR.getMessage())
				.data(new Boolean(false))
				.build()
				);
	}
	
	
	@PutMapping(path = "/{no}")
	public ResponseEntity<?> updateReply(
			@Validated @RequestBody ReplyForm form,
			BindingResult bindingResult,
			@PathVariable(required = true) Integer no) {
		
		
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			StringBuilder errorStr = new StringBuilder();
			errors.forEach(err -> {
				errorStr.append("["+err+"],\n");
			});
			
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.PARAM_ERROR.getResultCode())
					.message(ResponseInfo.PARAM_ERROR.getMessage())
					.data(new Boolean(false))
					.build()
					);
		}
		
		
		if (replyService.updateReply(no, form.toEntity())) {
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(new Boolean(true))
					.build()
					);
		}
		
		
		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SERVER_ERROR.getResultCode())
				.message(ResponseInfo.SERVER_ERROR.getMessage())
				.data(new Boolean(false))
				.build()
				);
	}
	
	
	@DeleteMapping(path = "/{no}")
	public ResponseEntity<?> deleteReply(@PathVariable(required = true) Integer no,
			@RequestParam(name = "boardNo", required = false) Integer boardNo,
			@RequestParam(name = "memberId", required = false, value = "") String memberId) {
		ResponseDto resultDto = null;
		
		
		if (replyService.deleteReply(no, boardNo, memberId)) {
			return ResponseEntity.ok(
					resultDto = ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(new Boolean(true))
					.build()
					);
		}
		
		
		return ResponseEntity.ok(
				resultDto = ResponseDto.builder()
				.resultCode(ResponseInfo.SERVER_ERROR.getResultCode())
				.message(ResponseInfo.SERVER_ERROR.getMessage())
				.data(new Boolean(false))
				.build()
				);
		
	}
	
	
}
