package com.boot.template.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/reply")
public class ReplyController {


	@Autowired
	private ReplyService replyService;


	@GetMapping(path = "list-v2/{boardNo}")
	public ResponseEntity<?> viewReplyListByBoardNoNew(
			@PathVariable(required = true) int boardNo, Pageable pageable) {

		Page<Reply> replyList = null;

		try {
			replyList = replyService.getReplyAllByBoardNoPaging(boardNo, pageable);

			if (replyList == null || replyList.getSize() == 0) {
				System.err.println(ResponseInfo.NO_CONTENT.getMessage());


				return ResponseEntity.ok(ResponseDto.builder()
						.resultCode(ResponseInfo.NO_CONTENT.getResultCode())
						.message(ResponseInfo.NO_CONTENT.getMessage())
						.data(replyList)
						.build()
						);
			}


			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(replyList)
					.build()
					);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	}

	@GetMapping(path = "list/{boardNo}")
	public ResponseEntity<?> viewReplyListByBoardNo(
			@PathVariable(required = true) int boardNo) {

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


			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.SUCCESS.getResultCode())
					.message(ResponseInfo.SUCCESS.getMessage())
					.data(replyList)
					.build()
					);

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


	@PutMapping(path = "/{replyNo}")
	public ResponseEntity<?> updateReply(
			@Validated @RequestBody ReplyForm form,
			BindingResult bindingResult,
			@PathVariable(required = true) Integer replyNo) {


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


		if (replyService.updateReply(replyNo, form.toEntity())) {
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


	@DeleteMapping(path = "/{replyNo}")
	public ResponseEntity<?> deleteReply(@PathVariable(required = true) Integer replyNo,
			@RequestParam(name = "boardNo", required = false) Integer boardNo,
			@RequestParam(name = "memberId", required = false, value = "") String memberId) {

		if (replyService.deleteReply(replyNo, boardNo, memberId)) {
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


}
