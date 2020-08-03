package com.yc.mapping;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.yc.bean.Resfood;


public interface ResfoodMapper {
	//使用@Select注解指明getAll方法要执行的SQL
	@Select("select * from resfood")
	public List<Resfood> getAllFood();
}
