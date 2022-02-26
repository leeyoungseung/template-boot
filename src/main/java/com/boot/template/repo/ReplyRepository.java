package com.boot.template.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.template.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
	Optional<List<Reply>> findByBoardNo(Integer baordNo);
}
