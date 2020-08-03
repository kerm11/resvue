package com.yc.biz;

import java.sql.SQLException;
import java.util.Map;

import com.yc.bean.Resorder;
import com.yc.web.entity.CartItem;

public interface ResorderBiz {
	
	public void  completeOrder(  Resorder resorder, Map<Integer, CartItem> shopCart  ) throws SQLException;
}
