package com.yc.mapping;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yc.bean.Resuser;

public interface ResuserMapper {	
	//使用@Select注解指明getById方法要执行的SQL
	@Select("select * from resuser where username=#{username} and pwd=#{pwd}")
	public Resuser login(@Param("username")String username,@Param("pwd")String pwd);
}
