package com.boot.template.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {

	Integer existContent(
			@Param("likeType") String likeType,
			@Param("typeNo") Integer typeNo);
	
}
