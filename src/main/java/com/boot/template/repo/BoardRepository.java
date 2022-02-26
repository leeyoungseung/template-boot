package com.boot.template.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.template.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {
	
	Optional<Board> findByNo(Integer no);

}
