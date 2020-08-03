package com.yc.web.servlets;

import static com.yc.utils.YcConstants.ERROR_404;
import static com.yc.utils.YcConstants.ERROR_500;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.yc.bean.Resfood;
import com.yc.mapping.ResfoodMapper;
import com.yc.mapping.ResorderMapper;
import com.yc.utils.YcConstants;
import com.yc.web.entity.CartItem;
import com.yc.web.entity.JsonModel;
import com.yc.web.utils.FoodUtil;

public class ResorderServlet extends BaseServlet {

	private static final long serialVersionUID = -977974500768761840L;
	
	static ResorderMapper mapper;
	
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
		mapper = sqlSession.getMapper(ResorderMapper.class);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		try {
			if ("order".equals(op)) {
				orderOp(req, resp);
			} else if ("getCartInfo".equals(op)) {
				getCartInfoOp(req, resp);
			} else if ("delorder".equals(op)) {
				delorder(req, resp);
			}else if( "orderJson".equals(op) ){
				orderJsonOp( req,resp);
			}else if("clearAll".equals(op)){
				clearAllOp( req, resp);
			} else {
				resp.sendRedirect(ERROR_404);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect(ERROR_500);
		}
	}

	private void clearAllOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JsonModel jm = new JsonModel();
		session.removeAttribute(YcConstants.CART);
		jm.setCode(1);
		outJson(  req,resp,jm);
	}

	private void delorder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JsonModel jm = new JsonModel();
		Resfood food = super.getReqParamObj(map, Resfood.class);
		int fid = food.getFid();
		// 购物车跟用户相关，所以存 session
		Map<Integer, CartItem> cart = null;
		if (session.getAttribute(YcConstants.CART) != null) {
			cart = (Map<Integer, CartItem>) session.getAttribute(YcConstants.CART);
		} else {
			cart = new HashMap<Integer, CartItem>();
		}
		if(  cart.containsKey(fid)){
			cart.remove(fid);
			jm.setCode(1);
		}else{
			jm.setCode(0);
		}
		// 将cart存到 session中
		session.setAttribute(YcConstants.CART, cart);
		outJson(req, resp, jm);
	}

	private void getCartInfoOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JsonModel jm = new JsonModel();
		List<CartItem> list=new ArrayList<CartItem>();
		if (session.getAttribute(YcConstants.CART) != null) {
			jm.setCode(1);
			//变更，将对象改为list，方便后面循环取值
			Map<Integer, CartItem> cart = null;
			cart = (Map<Integer, CartItem>) session.getAttribute(YcConstants.CART);
			
			Set<Integer> sets=cart.keySet();
			Iterator<Integer> iterator=sets.iterator();
			while(iterator.hasNext()){
				int x=iterator.next();
				//System.out.println( x );
				//System.out.println( cart.get(x).getNum() );
				list.add(  cart.get(x)  );
			}
			//jm.setObj(session.getAttribute(YcConstants.CART));
		} else {
			jm.setCode(0);
		}
		jm.setObj(list);
		outJson(req, resp, jm);
	}

	// 1. 取出num, fid
	// 2. 根据fid查到要购买的菜
	// 3. 存到购物车(
	// a.如果购物车中有，则加数量
	// b.如果没有，存到购物车
	// )
	private void orderOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		commonOrder(req);
		// 跳转页面.
		// 重定向，转发.
		//resp.sendRedirect("shopCart.jsp");
		
		JsonModel jm=new JsonModel();
		jm.setCode(1);
		outJson(req, resp, jm);
	}
	
	private void orderJsonOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		commonOrder(req);
		JsonModel jm=new JsonModel();
		jm.setCode(1);
		outJson(req, resp, jm);
	}

	private void commonOrder(HttpServletRequest req) {
		int fid = Integer.parseInt(map.get("fid")[0].toString());
		int num = Integer.parseInt(map.get("num")[0].toString());

		Resfood food = mapper.getFoodByFid(fid);

		
		// 购物车跟用户相关，所以存 session
		Map<Integer, CartItem> cart = null;
		if (session.getAttribute(YcConstants.CART) != null) {
			cart = (Map<Integer, CartItem>) session.getAttribute(YcConstants.CART);
		} else {
			cart = new HashMap<Integer, CartItem>();
		}
		// 看这个购物车是否有 fid
		CartItem ci = null;
		if (cart.containsKey(fid)) {
			// 证明用户已经购买了这个菜，则数量增加
			ci = cart.get(fid);
			int newnum = ci.getNum() + num;
			ci.setNum(newnum);
		} else {
			// 还没有买过, 则创建 一个cartItem存到map中
			ci = new CartItem();
			ci.setFood(food);
			ci.setNum(num);
		}
		if (ci.getNum() <= 0) {
			cart.remove(fid);
		} else {
			ci.getSmallCount(); // 计算小计.
			// 将cartitem存到map中
			cart.put(fid, ci);
		}
		// 将cart存到 session中
		session.setAttribute(YcConstants.CART, cart);
	}
}
