package com.boot.template.service;

import java.util.Date;
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
	
	
	public Member getMemberInfoByMemberId(String memberId) {
		Optional<Member> result = memberRepo.findByMemberId(memberId);
		return result.isEmpty() ? null : result.get(); 
	}
	
	
	public void updateMember(Member member) {
		memberRepo.save(member);
	}
	
	
	public Member getMemberInfoBySessionKey(String sessionKey) {
		log.info("getMemberInfoBySessionKey Start");
		Optional<Member> result = memberRepo.findBySessionKey(sessionKey);
		
		if (result.isEmpty()) {
			log.info("sessionKey is inValid : {} ", sessionKey);
			return null;
		}
		
		Date now = new Date(System.currentTimeMillis());
		if (result.get().getSessionLimitTime().before(now)) {
			log.info("sessionKey is expiration Limit Time : {} , Now : {}", result.get().getSessionLimitTime(), now);
			return null;
		}
		
		log.info("Result Member Info {}", result.get().toString());
		return result.get();
	}
}
