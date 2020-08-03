package com.yc.web.servlets;

import static com.yc.utils.YcConstants.ERROR_404;
import static com.yc.utils.YcConstants.ERROR_500;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.yc.bean.Resuser;
import com.yc.dao.DBUtil;
import com.yc.mapping.ResuserMapper;
import com.yc.utils.Encrypt;
import com.yc.utils.YcConstants;
import com.yc.web.entity.JsonModel;

public class ResuserServlet extends BaseServlet {
	private static final long serialVersionUID = 6052965419326485519L;
	static ResuserMapper mapper;
	
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
		mapper = sqlSession.getMapper(ResuserMapper.class);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException  {
		try {
			if(  "login".equals(op)){
				loginOp( req,resp);
			}else if( "checkLogin".equals(op)){
				checkLoginOp(   req, resp);
			}else if(   "logout".equals(op) ){
				logoutOp( req, resp);
			}else{
				resp.sendRedirect( ERROR_404);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect( ERROR_500);
		}
	}

	private void logoutOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//TODO:   购物车中的数据要缓存  => 将购物车数据保存到resorderitemtemp. 
		//session.removeAttribute(    YcConstants.CART );
		session.removeAttribute(   YcConstants.LOGINUSER );
		JsonModel jm=new JsonModel();
		jm.setCode(1);
		outJson(req, resp, jm);
		
	}

	private void checkLoginOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JsonModel jm=new JsonModel();
		if(   session.getAttribute(   YcConstants.LOGINUSER )==null){
			jm.setCode(0);
		}else{
			jm.setCode(1);
			Resuser user=(Resuser) session.getAttribute(   YcConstants.LOGINUSER );
			jm.setObj(   user );
		}
		outJson(req, resp, jm);
	}

	private void loginOp(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
		JsonModel jm=new JsonModel();
		if(   map.get("valcode") ==null  ){
			jm.setCode(0);
			jm.setMsg("验证码不能为空...");
			outJson(req, resp, jm);
			return;
		}
		String valcode=map.get("valcode")[0];
		String validateCode=(String)session.getAttribute("validateCode");
		if(    !valcode.equalsIgnoreCase(validateCode) ){    // equals  -> 区分大小写
			jm.setCode(0);
			jm.setMsg("验证码输入错误...");
			outJson(req, resp, jm);
			return;
		}
		
		Resuser resuser=super.getReqParamObj(map, Resuser.class);
		String username=resuser.getUsername();
		String pwd=Encrypt.md5(resuser.getPwd());
		System.out.println(username + "--" + pwd);
		
		
		//执行查询操作，将查询结果自动封装成List<User>返回
		Resuser users = mapper.login(username, pwd);
		//使用SqlSession执行完SQL之后需要关闭SqlSession
		//sqlSession.close();
		
		if(  users!=null   ){
			resuser.setUserid(  users.getUserid() );
			session.setAttribute(YcConstants.LOGINUSER, resuser);
			jm.setCode(1);
			//在看地址
			if( session.getAttribute( YcConstants.LASTVISITEDADDR)!=null ){
				jm.setUrl(   (String)session.getAttribute( YcConstants.LASTVISITEDADDR));
			}else{
				jm.setUrl(   YcConstants.HOMEPAGE  );
			}
		}else{
			jm.setCode(0);
			jm.setMsg("wrong username or password,please try again");
		}
		outJson(req, resp, jm);
	}
}
