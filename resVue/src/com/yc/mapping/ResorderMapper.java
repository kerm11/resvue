package com.yc.mapping;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yc.bean.Resfood;

public interface ResorderMapper {
	@Select("select * from resfood where fid=#{fid}")
	public Resfood getFoodByFid(@Param("fid")int fid);
}
