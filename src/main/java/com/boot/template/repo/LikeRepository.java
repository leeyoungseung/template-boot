package com.boot.template.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.boot.template.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Integer>{
	
	Optional<List<Like>> findByBoardNo(Integer boardNo);
	
	Optional<List<Like>> findByReplyNo(Integer replyNo);
	
	Optional<Like> findByBoardNoAndMemberId(Integer boardNo, String memberId);
	
	Optional<Like> findByReplyNoAndMemberId(Integer replyNo, String memberId);
	
//	@Query(value = "SELECT l FROM Like l WHERE l.memberId =: memberId AND l.boardNo =: boardNo OR l.replyNo =: replyNo")
//	Optional<Like> findByMemberIdAndBoardNoOrReplyNo(String memberId, Integer boardNo, Integer replyNo);

}
