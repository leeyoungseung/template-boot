package com.boot.template.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.template.entity.Member;
import com.boot.template.repo.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberService {
	
	@Autowired
	private MemberRepository memberRepo;
	
	/**
	 * return true existing memberId in DB. 
	 * @param memberId
	 * @return
	 */
	public boolean existMemberId(String memberId) {
		Optional<Member> result = memberRepo.findByMemberId(memberId);
		return result.isEmpty() ? false : true; 
	}
}
