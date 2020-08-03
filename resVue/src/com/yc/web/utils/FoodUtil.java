package com.yc.web.utils;

import static com.yc.utils.YcConstants.RESFOODLIST;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.yc.bean.Resfood;
import com.yc.dao.DBUtil;
import com.yc.utils.YcConstants;

public class FoodUtil {

	public static Resfood getFoodFromList(int fid, List<Resfood> list) {
		Resfood food=null;
		if(  list!=null &&list.size()>0){
			for( Resfood f: list){
				if( f.getFid()==fid){
					food=f;
					break;
				}
			}
		}
		return food;
	}
	
	public static  List<Resfood> getAllFoods(HttpServletRequest req) {
		List<Resfood> list=null;
		ServletContext application=req.getSession().getServletContext();
		if(  application.getAttribute(    RESFOODLIST  ) !=null ){
			list=(List<Resfood>) application.getAttribute( RESFOODLIST  );
		}else{
			// 2. 没有，则查
			DBUtil db=new DBUtil();
			try {
				list = db.find("select fid,fname,normprice, realprice, fphoto from resfood", null, Resfood.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			application.setAttribute( YcConstants.RESFOODLIST, list);
		}
		return list;
	}

}
