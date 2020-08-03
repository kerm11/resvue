package com.yc.res;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.yc.bean.Resfood;
import com.yc.bean.Resorder;
import com.yc.dao.DBUtil;

public class TestFood {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		DBUtil db=new DBUtil();
		List<Resfood> list=null;
		try {
			list = db.find("select * from resfood", null, Resfood.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(  list );
	}
	
	@Test
	public void testOrder() {
		DBUtil db=new DBUtil();
		String sql="insert into resorder(userid,address,tel,ordertime,deliverytime,ps,status) values( ?,?,?,now(),CAST(? AS date),?,0)";
		List<Object> params=new ArrayList<Object>();
		params.add(1);
		params.add("南华");
		params.add("1234567");
		params.add(  "2016-09-12 12:00:00" );
		params.add("尽快...");
		try {
			db.doUpdate(sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFindOrder() {
		DBUtil db=new DBUtil();
		String sql="select * from resorder";
		List<Resorder> list=null;
		try {
			list = db.find(sql, null, Resorder.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(  list );
		
	}

}
