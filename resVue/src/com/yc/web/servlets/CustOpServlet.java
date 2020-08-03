package com.yc.web.servlets;

import static com.yc.utils.YcConstants.ERROR_404;
import static com.yc.utils.YcConstants.ERROR_500;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yc.bean.Resorder;
import com.yc.bean.Resuser;
import com.yc.biz.ResorderBiz;
import com.yc.biz.impl.ResorderBizImpl;
import com.yc.utils.YcConstants;
import com.yc.web.entity.CartItem;
import com.yc.web.entity.JsonModel;

public class CustOpServlet extends BaseServlet {
	private static final long serialVersionUID = 7884358627459967791L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException  {
		try {
			if(  "confirmOrder".equals(op)){
				confirmOrderOp( req,resp);
			}else{
				resp.sendRedirect( ERROR_404);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect( ERROR_500);
		}
	}

	private void confirmOrderOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JsonModel jm=new JsonModel();
		// address, tel, ps, deliverytime
		Resorder resorder=super.getReqParamObj(map, Resorder.class);
		if(  session.getAttribute(   YcConstants.LOGINUSER) ==null  ){
			jm.setCode(0);
			jm.setMsg(   "user has not been logined....");
			outJson(req, resp, jm);
			return;
		}
		//查询用户id    从session中取出登录 用户.
		Resuser resuser=(Resuser) session.getAttribute(   YcConstants.LOGINUSER);
		resorder.setUserid(   resuser.getUserid());
		//准备   Resorderitem数据
		if(  session.getAttribute(  YcConstants.CART) ==null  ){
			jm.setCode(0);
			jm.setMsg(   "you have not buy any thing....");
			outJson(req, resp, jm);
			return;
		}
		Map<Integer, CartItem> cart=(Map<Integer, CartItem>) session.getAttribute(  YcConstants.CART);
		
		try {
			//调用后台业务层，完成事务处理..
			ResorderBiz rb=new ResorderBizImpl();
			rb.completeOrder(resorder, cart);
			
			session.removeAttribute(  YcConstants.CART);
			//TODO: 付款  -> 调用支付模块
			jm.setCode(1);
			jm.setUrl(   YcConstants.ORDERSUCCESSADDR  );
			outJson(req, resp, jm);
		} catch (Exception e) {
			jm.setCode(0);
			jm.setMsg("order failed ,please contact the administrator....QQ:12334455");
			outJson(req, resp, jm);
		}
		
		
		
	}
}
