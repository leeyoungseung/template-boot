package com.boot.template.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boot.template.conf.DatabaseConfigMybatis;
import com.boot.template.dto.LikeStatusDto;
import com.boot.template.entity.Board;
import com.boot.template.entity.Like;
import com.boot.template.entity.Reply;
import com.boot.template.enums.LikeType;
import com.boot.template.form.LikeForm;
import com.boot.template.mapper.LikeMapper;
import com.boot.template.repo.BoardRepository;
import com.boot.template.repo.LikeRepository;
import com.boot.template.repo.MemberRepository;
import com.boot.template.repo.ReplyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LikeService {
	
	@Autowired
	private LikeMapper likeMapper;
	
	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private ReplyRepository replyRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	
	public boolean existContent(Integer typeNo, String likeType) {
		log.info("param check typeNo : {}, likeType : {}", typeNo, likeType);
		Integer res = likeMapper.existContent(likeType, typeNo);
		return (res == null || res <= 0) ? false : true;
	}
	
	
	// create or update like
	@Transactional
	public boolean createOrUpdateLike(LikeForm likeForm) throws Exception {
		
		
		
		if (existContent(likeForm.getTypeNo(), likeForm.getLikeType())) {
			log.info("exist content");
		} else {
			log.info("not exist content");
		}
		
		Like like = likeForm.toEntity();
		
		
		// 1. Exist Like data??
		Optional<Like> originLikeOp = existLikeData(like);
		
		if (originLikeOp.isEmpty()) {
			// 2. Case-Create
			// 2-1. Exist Data by BoardNo or ReplyNo?
			if (!(
					(like.getLikeType().equals(LikeType.BOARD.value) 
					      && like.getBoardNo() != null
					      && boardRepository.existsById(like.getBoardNo())
					     ) ||
					(like.getLikeType().equals(LikeType.REPLY.value) 
					     && like.getReplyNo() != null
					     && replyRepository.existsById(like.getReplyNo())
					     )
					)
				) { 
				throw new Exception("Not Exist Data!! ["+like.toString()+"]");
			}
			
			// 2-2. Exist Member by MemberId?
			if (like.getMemberId() == null || memberRepository.findByMemberId(like.getMemberId()).isEmpty()) {
				throw new Exception("Not Exist Member Data!! ["+like.toString()+"]");
			}
			
			// 2-3. Save Data 
			like.setCreatedTime(new Date());
			like.setUpdatedTime(new Date());
			
			likeRepository.save(like);
			
			if (like.getLikeType().equals(LikeType.BOARD.value)) {
				Board board = boardRepository.getById(like.getBoardNo());
				if (like.isLikeStatus()) {
					board.setLikes(1);
				} else {
					board.setDislikes(1);
				}
				
				board.setUpdatedTime(new Date());
				boardRepository.save(board);
				
			} else if (like.getLikeType().equals(LikeType.REPLY.value)) {
				Reply reply = replyRepository.getById(like.getReplyNo());
				if (like.isLikeStatus()) {
					reply.setLikes(1);
				} else {
					reply.setDislikes(1);
				}
				
				reply.setUpdatedTime(new Date());
				replyRepository.save(reply);
				
			} else {
				throw new Exception("Not Exist Data!! ["+like.toString()+"]");
			}
			
			
			return true;
			
		} else {
			// 3. Case-Update
			// 3-1. Exist Board by boardNo? If not exist Board, this process have to finish.
			if (!(
					(like.getLikeType().equals(LikeType.BOARD.value)
						 && like.getBoardNo() != null
						 && boardRepository.existsById(like.getBoardNo())
						  ) ||
					(like.getLikeType().equals(LikeType.REPLY.value) 
						  && like.getReplyNo() != null
						  && replyRepository.existsById(like.getReplyNo())
						  )
				     )
				) { 
				throw new Exception("Not Exist Data!! ["+like.toString()+"]");
			}
			
			// 3-2. Exist Member by MemberId? If not exist Member, this process have to finish.
			if (like.getMemberId() == null || memberRepository.findByMemberId(like.getMemberId()).isEmpty()) {
				throw new Exception("Not Exist Member Data!! ["+like.toString()+"]");
			}
			
			Like originLike = originLikeOp.get();
			
			if (like.isLikeStatus() == originLike.isLikeStatus()) {
				// 3-3-1. Is it equal likeStatus requested and in database? 
				//       execute to delete Like in DB.
				likeRepository.delete(originLike);
				
				if (like.getLikeType().equals(LikeType.BOARD.value)) {
					Board board = boardRepository.getById(like.getBoardNo());
					if (like.isLikeStatus()) {
						board.setLikes(board.getLikes() - 1);
					} else {
						board.setDislikes(board.getDislikes() - 1);
					}
					
					board.setUpdatedTime(new Date());
					boardRepository.save(board);
					
				} else if (like.getLikeType().equals(LikeType.REPLY.value)) {
					Reply reply = replyRepository.getById(like.getReplyNo());
					if (like.isLikeStatus()) {
						reply.setLikes(reply.getLikes() - 1);
					} else {
						reply.setDislikes(reply.getDislikes() - 1);
					}
					
					reply.setUpdatedTime(new Date());
					replyRepository.save(reply);
					
				} else {
					throw new Exception("Not Exist Data!! ["+like.toString()+"]");
				}
				
				return true;
				
			} else {
				// 3-3-2. Is it not equal likeStatus requested and in database?
				//       execute to update Like in DB.
				originLike.setLikeStatus(like.isLikeStatus());
				originLike.setUpdatedTime(new Date());
				likeRepository.save(originLike);
				
				if (like.getLikeType().equals(LikeType.BOARD.value)) {
					Board board = boardRepository.getById(like.getBoardNo());
					
					if (like.isLikeStatus()) { // origin is false.
						board.setLikes(board.getLikes() + 1);
						board.setDislikes(board.getDislikes() - 1);
						
					} else { // origin is true.
						board.setLikes(board.getLikes() - 1);
						board.setDislikes(board.getDislikes() + 1);
					}
					
					board.setUpdatedTime(new Date());
					boardRepository.save(board);
					
				} else if (like.getLikeType().equals(LikeType.REPLY.value)) {
					Reply reply = replyRepository.getById(like.getReplyNo());
					if (like.isLikeStatus()) {
						reply.setLikes(reply.getLikes() + 1);
						reply.setDislikes(reply.getDislikes() - 1);
						
					} else {
						reply.setLikes(reply.getLikes() - 1);
						reply.setDislikes(reply.getDislikes() + 1);
					}
					
					reply.setUpdatedTime(new Date());
					replyRepository.save(reply);
					
				} else {
					throw new Exception("Not Exist Data!! ["+like.toString()+"]");
				}
				
				return true;

			}	
		} 
		
	}
	
	
    private Optional<Like> existLikeData(Like like) {
    	Optional<Like> result = null;
    	
    	if (like.getLikeType().equals(LikeType.BOARD.value) 
    			&& like.getBoardNo() != null) {
    		result = likeRepository.findByBoardNoAndMemberId(like.getBoardNo(), like.getMemberId());
    	} else if (like.getLikeType().equals(LikeType.REPLY.value) 
				&& like.getReplyNo() != null ) {
    		result = likeRepository.findByReplyNoAndMemberId(like.getReplyNo(), like.getMemberId());
    	}
    	
    	return result;
    }


	// get like status
	@Transactional
	public LikeStatusDto getLikeStatusByTargetNoAndMemberId(String targetType, Integer targetNo, String memberId) throws Exception {
		
		if (!typeValidation(targetType, targetNo)) {
			throw new Exception("Not Exist Data!! targetType : ["+targetType+"] , targetNo : ["+targetNo+"], memberId : ["+memberId+"]");
		}
		
		LikeStatusDto result = new LikeStatusDto();
		List<Like> likeList = new ArrayList<Like>();
		List<Like> dislikeList = new ArrayList<Like>();
		Optional<Like> dataByMemberId = null;
		Optional<List<Like>> dataListByTargetId = null;

		if (targetType.equals(LikeType.BOARD.value)) {
			dataByMemberId = likeRepository.findByBoardNoAndMemberId(targetNo, memberId);
			if (dataByMemberId.isEmpty()) {
				result.setLikeStatus(false);
				result.setDislikeStatus(false);
				
			} else {
				if (dataByMemberId.get().isLikeStatus()) {
					result.setLikeStatus(dataByMemberId.get().isLikeStatus());
					result.setDislikeStatus(!dataByMemberId.get().isLikeStatus());
					
				} else {
					result.setLikeStatus(!dataByMemberId.get().isLikeStatus());
					result.setDislikeStatus(dataByMemberId.get().isLikeStatus());
					
				}
				
			}
			
			dataListByTargetId = likeRepository.findByBoardNo(targetNo);
			if (dataListByTargetId.isEmpty()) {
				result.setLikeCount(0);
				result.setDislikeCount(0);
				
			} else {
				dataListByTargetId.get().forEach(like -> {
					if (like.isLikeStatus()) {
						likeList.add(like);
						
					} else {
						dislikeList.add(like);
					}
				});
			}
			
			result.setLikeCount(likeList.size());
			result.setDislikeCount(dislikeList.size());
			
			return result;
		    
		} else if (targetType.equals(LikeType.REPLY.value)) {
			dataByMemberId = likeRepository.findByReplyNoAndMemberId(targetNo, memberId);
			if (dataByMemberId.isEmpty()) {
				result.setLikeStatus(false);
				result.setDislikeStatus(false);
				
			} else {
				if (dataByMemberId.get().isLikeStatus()) {
					result.setLikeStatus(dataByMemberId.get().isLikeStatus());
					result.setDislikeStatus(!dataByMemberId.get().isLikeStatus());
					
				} else {
					result.setLikeStatus(!dataByMemberId.get().isLikeStatus());
					result.setDislikeStatus(dataByMemberId.get().isLikeStatus());
					
				}
				
			}
			
			dataListByTargetId = likeRepository.findByReplyNo(targetNo);
			if (dataListByTargetId.isEmpty()) {
				result.setLikeCount(0);
				result.setDislikeCount(0);
				
			} else {
				dataListByTargetId.get().forEach(like -> {
					if (like.isLikeStatus()) {
						likeList.add(like);
						
					} else {
						dislikeList.add(like);
					}
				});
			}
			
			result.setLikeCount(likeList.size());
			result.setDislikeCount(dislikeList.size());
			
			return result;
		}
		
		return result;
	}
	
	
	private boolean typeValidation(String likeType, Integer targetValue) throws Exception {
		
		if (
			!(
			    (likeType.equals(LikeType.BOARD.value) 
				&& targetValue != null
				&& boardRepository.existsById(targetValue)
				) 
				||
				(likeType.equals(LikeType.REPLY.value) 
				&& targetValue != null
				&& replyRepository.existsById(targetValue)
				)
			 )
			) { 
			return false;
		}
		
		return true;
	}
	
}
