package com.boot.template.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boot.template.dto.ResponseDto;
import com.boot.template.dto.LikeStatusDto;
import com.boot.template.enums.LikeType;
import com.boot.template.enums.ResponseInfo;
import com.boot.template.form.LikeForm;
import com.boot.template.service.LikeService;

@RestController
@RequestMapping(path = "/like")
public class LikeController {

	
	@Autowired
	private LikeService likeService;
	
	
	@GetMapping
	public ResponseEntity<?> getLikeInfo(
			@RequestParam(name = "targetType", required = true) String targetType,
			@RequestParam(name = "targetNo", required = true) Integer targetNo,
			@RequestParam(name = "memberId", required = false) String memberId) {
		
		System.out.println("getLikeInfo : "+targetType+","+targetNo+","+memberId);
		LikeStatusDto result = null;
		
		// parameter validation check
		if (targetType == null || targetNo == null) {
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.PARAM_ERROR.getResultCode())
					.message(ResponseInfo.PARAM_ERROR.getMessage())
					.data(result)
					.build()
					);
		}
		
		try {
			result = likeService.getLikeStatusByTargetNoAndMemberId(targetType, targetNo, memberId);
			
			return ResponseEntity.ok(ResponseDto.builder()
						.resultCode(ResponseInfo.SUCCESS.getResultCode())
						.message(ResponseInfo.SUCCESS.getMessage())
						.data(result)
						.build()
						);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SERVER_ERROR.getResultCode())
				.message(ResponseInfo.SERVER_ERROR.getMessage())
				.data(result)
				.build()
				);
	}
	
	
	// create or update Like
	@PostMapping
	public ResponseEntity<?> createOrUpdateLike(
			@Validated @RequestBody LikeForm form,
			BindingResult bindingResult,
			Model model) {
		
		System.out.println("createOrUpdateLike : "+form.toString());
		Boolean result = false;
		
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
			StringBuilder errorStr = new StringBuilder();
			errors.forEach(err -> {
				errorStr.append("["+err+"],\n");
			});
			
			return ResponseEntity.ok(ResponseDto.builder()
					.resultCode(ResponseInfo.PARAM_ERROR.getResultCode())
					.message(ResponseInfo.PARAM_ERROR.getMessage())
					.data(result)
					.build()
					);
		}
		
		try {
			
			result = likeService.createOrUpdateLike(form);
			if (result) {
				return ResponseEntity.ok(ResponseDto.builder()
						.resultCode(ResponseInfo.SUCCESS.getResultCode())
						.message(ResponseInfo.SUCCESS.getMessage())
						.data(result)
						.build()
						);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok(ResponseDto.builder()
				.resultCode(ResponseInfo.SERVER_ERROR.getResultCode())
				.message(ResponseInfo.SERVER_ERROR.getMessage())
				.data(result)
				.build()
				);
	}
	
	
}
