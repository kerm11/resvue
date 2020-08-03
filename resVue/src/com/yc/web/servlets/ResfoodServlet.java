package com.yc.web.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.yc.bean.Resfood;
import com.yc.dao.DBUtil;
import com.yc.mapping.ResfoodMapper;
import com.yc.mapping.ResuserMapper;
import com.yc.utils.YcConstants;
import com.yc.web.entity.JsonModel;
import com.yc.web.utils.FoodUtil;

import static com.yc.utils.YcConstants.RESFOODLIST;
import static com.yc.utils.YcConstants.ERROR_404;
import static com.yc.utils.YcConstants.ERROR_500;

public class ResfoodServlet extends BaseServlet {
	
	static ResfoodMapper mapper;
	
	static{
		//mybatis的资源配置文件
		String resource = "conf.xml";
		//得到IO流
		InputStream is = ResuserServlet.class.getClassLoader().getResourceAsStream(resource);
		//每个基于 MyBatis 的应用都是以一个 SqlSessionFactory 的实例为核心的
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
		//得到session
		SqlSession sqlSession=factory.openSession(true);		//true  自动提交
		
		//得到UserMapperI接口的实现类对象，UserMapperI接口的实现类对象由sqlSession.getMapper(UserMapper.class)动态构建出来
		mapper = sqlSession.getMapper(ResfoodMapper.class);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		try {
			if ("findAllFoods".equals(op)) {
				findAllFoodsOp(req, resp);
			} else if ("findFood".equals(op)) {
				findFoodOp(req, resp);
			} else if ("findAllSelectedFoods".equals(op)) {
				findAllSelectedFoodsOp(req, resp);
			} else {
				resp.sendRedirect(ERROR_404);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect(ERROR_500);
		}
	}

	private void findAllSelectedFoodsOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 从application中取出所有的菜
		List<Resfood> list = (List<Resfood>) application.getAttribute(YcConstants.RESFOODLIST);
		List<Resfood> result = new ArrayList<Resfood>();
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			HashSet<String> hs = new HashSet<String>();
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(YcConstants.BROWSEDFOOD)) {
					String cookiesid = cookie.getValue();
					String[] fids = cookiesid.split(",");
					for (String s : fids) {
						hs.add(s);
					}
				}
			}
			for (String t : hs) {
				for (Resfood rf : list) {
					if (rf.getFid() == Integer.parseInt(t)) {
						result.add(rf);
					}
				}
			}

		}
		JsonModel jm = new JsonModel();
		jm.setCode(1);
		jm.setObj(result);

		outJson(req, resp, jm);
	}

	private void findFoodOp(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, SQLException, ServletException {
		Resfood food = super.getReqParamObj(map, Resfood.class);
		// 取出编号
		int fid = food.getFid();
		// 从application中取出对应的菜品信息
		List<Resfood> list = FoodUtil.getAllFoods(req);
		food = FoodUtil.getFoodFromList(fid, list); // 是没有详情

		String sql = "select detail from resfood where fid=?";
		List<Object> params = new ArrayList<Object>();
		params.add(fid);
		DBUtil db = new DBUtil();

		Map<String, Object> map = db.doQueryOne(sql, params);
		if (map != null) {
			food.setDetail((String) map.get("detail"));
		}
		// 因为是跳页面方式，所以不json数据
		req.setAttribute("food", food); // 因为是将数据存到 req中，req指的是一次请求有效.
		// 必须用转发(是一次请求)，不能用重定向(两次请求)
		// req.getRequestDispatcher("detail.jsp").forward(req, resp);

		JsonModel jm = new JsonModel();
		jm.setCode(1);
		jm.setObj(food);

		outJson(req, resp, jm);
	}

	private void findAllFoodsOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<Resfood> list=null;
		// 1. 查看application中是否有数据，有则取出，
		ServletContext application=req.getSession().getServletContext();
		if(  application.getAttribute(    RESFOODLIST  ) !=null ){
			list=(List<Resfood>) application.getAttribute( RESFOODLIST  );
		}else{
			// 2. 没有，则查
			list=mapper.getAllFood();
			application.setAttribute( YcConstants.RESFOODLIST, list);
		}
		
		// 返回jsonModel
		JsonModel jm = new JsonModel();
		jm.setCode(1);
		jm.setObj(list);
		outJson(req, resp, jm);

	}

	

}
