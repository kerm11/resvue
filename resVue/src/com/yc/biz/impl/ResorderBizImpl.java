package com.yc.biz.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.yc.bean.Resorder;
import com.yc.biz.ResorderBiz;
import com.yc.dao.DBUtil;
import com.yc.web.entity.CartItem;

public class ResorderBizImpl implements ResorderBiz {

	@Override
	public void completeOrder(Resorder resorder, Map<Integer, CartItem> shopCart) throws SQLException {
		DBUtil db=new DBUtil();
		int roid=0;
		Connection con=null;
		
		String sql1="insert into resorder( userid,address,tel,ordertime,deliverytime,ps,status) values( ?,?,?,now(),?,?,?)";
		try {
			con=db.getConn();
			con.setAutoCommit(false);
			PreparedStatement pstmt=con.prepareStatement(sql1);
			
			pstmt.setString(1, String.valueOf(resorder.getUserid()));
			pstmt.setString(2, String.valueOf(resorder.getAddress()));
			pstmt.setString(3, String.valueOf(resorder.getTel()));
			pstmt.setString(4, String.valueOf(resorder.getDeliverytime()));
			pstmt.setString(5, String.valueOf(resorder.getPs()));
			pstmt.setString(6, String.valueOf(resorder.getStatus()));
			pstmt.executeUpdate();
			sql1="select max( roid ) from resorder";
			pstmt=con.prepareStatement(sql1);
			ResultSet rs=pstmt.executeQuery();
			if( rs.next() ){
				roid=rs.getInt(1);
			}
			if( shopCart!=null&& shopCart.size()>0){
				for(   Map.Entry<Integer, CartItem> entry: shopCart.entrySet()){
					sql1="insert into resorderitem(roid,fid,dealprice,num) values( ?,?,?,?)";
					pstmt=con.prepareStatement(sql1);
					pstmt.setString(1, String.valueOf( roid ));
					pstmt.setString(2, String.valueOf( entry.getKey()));
					pstmt.setString(3, String.valueOf(entry.getValue().getFood().getRealprice()));
					pstmt.setString(4, String.valueOf(entry.getValue().getNum()));
					pstmt.executeUpdate();
				}
			}
			con.commit();
		} catch (SQLException e) {
			if( con!=null ){
				con.rollback();
			}
			e.printStackTrace();
			throw e;
		}finally{
			if( con!=null ){
				con.setAutoCommit(true);
				con.close();
			}
		}
		
	}

}
