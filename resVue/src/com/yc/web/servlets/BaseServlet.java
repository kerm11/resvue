package com.yc.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import com.google.gson.Gson;
import com.yc.web.entity.JsonModel;
import com.yc.web.utils.UploadUtil;


/**
 * 锟斤拷锟斤拷锟斤拷募锟斤拷洗锟�   enctype="mutipart/form-data" )锟斤拷锟斤拷通锟斤拷service()锟结将锟斤拷锟叫的诧拷锟斤拷娴揭伙拷锟絤ap锟斤拷锟斤拷,  锟斤拷锟斤拷 锟较达拷锟斤拷锟侥硷拷锟斤拷锟芥到锟斤拷应位锟斤拷 
 * @author Administrator
 *
 */
public abstract class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected String op;                   //   op锟斤拷锟斤拷锟斤拷
	protected String contentType;         //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷(  multipart/form-data     -----     wwww-xxxx-xxx )
	protected Map<String,String[]> map;   //锟侥硷拷锟较达拷时锟斤拷锟斤拷锟叫碉拷锟斤拷莼岜伙拷锟斤拷锟斤拷锟斤拷map锟斤拷
	protected ServletContext application;
	protected HttpSession session;
	
	public BaseServlet() {
		super();
	}

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		application=arg0.getSession().getServletContext();
		session=arg0.getSession();
		//锟矫碉拷锟斤拷锟斤拷锟斤拷锟酵ｏ拷锟斤拷锟叫讹拷锟斤拷锟侥硷拷锟较达拷锟斤拷锟斤拷锟斤拷通锟斤拷锟斤拷
		contentType=arg0.getContentType();
		if(  contentType==null||   !contentType.startsWith(  "multipart/form-data")   ){
			op = arg0.getParameter("op");
			map=arg0.getParameterMap();
		}else{
			
			PageContext pageContext = JspFactory .getDefaultFactory().getPageContext(this, arg0, arg1, null, true, 8192, true);
			UploadUtil uu=new UploadUtil();
			try {
				map=uu.upload(    pageContext  );
				if(  map.get("op")!=null&& map.get("op").length>0){
					op=map.get("op")[0];
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.service(arg0, arg1);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	
	private <T> void extractSingleParameterType(T t, Method method,
			String[] values, Class parameterType)
			throws IllegalAccessException, InvocationTargetException,
			ParseException {
		//没锟斤拷[锟斤拷头锟侥撅拷锟角碉拷锟斤拷值
		String s = values[0];
		String parameterTypeName = parameterType.getName();  //  set锟斤拷锟斤拷锟叫的诧拷锟斤拷锟斤拷锟斤拷
		if("int".equals(parameterTypeName) || "java.lang.Integer".equals(parameterTypeName)){
			method.invoke(t, Integer.parseInt(s));
		}else if("double".equals(parameterTypeName) || "java.lang.Double".equals(parameterTypeName)){
			method.invoke(t, Double.parseDouble(s));
		}else if("float".equals(parameterTypeName) || "java.lang.Float".equals(parameterTypeName)){
			method.invoke(t, Float.parseFloat(s));
		}else if("byte".equals(parameterTypeName) || "java.lang.Byte".equals(parameterTypeName)){
			method.invoke(t, Byte.parseByte(s));
		}else if("java.util.Date".equals(parameterTypeName)){    //  setXX( Date d )
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			method.invoke(t, sdf.parse(s));
		}else{
			method.invoke(t, s);
		}
	}
	
	private <T> void extractArrayParameterType(T t, Method method,
			String[] values, Class parameterType)
			throws IllegalAccessException, InvocationTargetException,
			ParseException {
		//锟斤拷[锟斤拷头锟侥撅拷锟斤拷锟斤拷锟斤拷
		String parameterTypeName = parameterType.getName();  //   [I  => int[]     [Ljava.lange.Integer;
		if("[I".equals(parameterTypeName)){
			int[] intArray = new int[values.length];
			for(int i = 0; i < intArray.length; i++){
				intArray[i] = Integer.parseInt(values[i]);
			}
			method.invoke(t, intArray);
		}else if("[Ljava.lang.Integer;".equals(parameterTypeName)){
			Integer[] intArray = new Integer[values.length];
			for(int i = 0; i < intArray.length; i++){
				intArray[i] = Integer.parseInt(values[i]);
			}
			method.invoke(t, (Object)intArray);
		}else if("[D".equals(parameterTypeName)){
			double[] doubleArray = new double[values.length];
			for(int i = 0; i < doubleArray.length; i++){
				doubleArray[i] = Double.parseDouble(values[i]);
			}
			method.invoke(t, doubleArray);
		}else if("[Ljava.lang.Double;".equals(parameterTypeName)){
			Double[] doubleArray = new Double[values.length];
			for(int i = 0; i < doubleArray.length; i++){
				doubleArray[i] = Double.parseDouble(values[i]);
			}
			method.invoke(t, (Object)doubleArray);
		}else if("[F".equals(parameterTypeName)){
			float[] floatArray = new float[values.length];
			for(int i = 0; i < floatArray.length; i++){
				floatArray[i] = Float.parseFloat(values[i]);
			}
			method.invoke(t, floatArray);
		}else if("[Ljava.lang.Float;".equals(parameterTypeName)){
			Float[] floatArray = new Float[values.length];
			for(int i = 0; i < floatArray.length; i++){
				floatArray[i] = Float.parseFloat(values[i]);
			}
			method.invoke(t, (Object)floatArray);
		}else if("[B".equals(parameterTypeName)){
			byte[] byteArray = new byte[values.length];
			for (int i = 0; i < byteArray.length; i++) {
				byteArray[i] = Byte.parseByte(values[i]);
			}
			method.invoke(t, byteArray);
		}else if("[Ljava.lang.Byte;".equals(parameterTypeName)){
			Byte[] byteArray = new Byte[values.length];
			for (int i = 0; i < byteArray.length; i++) {
				byteArray[i] = Byte.parseByte(values[i]);
			}
			method.invoke(t, (Object)byteArray);
		}else if("[Ljava.lang.String;".equals(parameterTypeName)){
			method.invoke(t, (Object)values);
		}
	}
	
	/**
	 * 
	 * @param request  锟斤拷锟斤拷锟斤拷锟斤拷锟�
	 * @param clazz  要转锟斤拷锟缴碉拷实锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
	 * @return  实锟斤拷锟斤拷锟�
	 */
	protected <T> T getReqParamObj(Map<String,String[]> map, Class<T> clazz) {
		T t = null;
		map = parseRequestMap(map);
		try {
			t = clazz.newInstance();  //通锟斤拷锟斤拷锟斤拷螅锟斤拷锟斤拷锟侥讹拷锟斤拷锟洁当(new T();)
			Method[] ms = clazz.getMethods(); //取锟斤拷锟斤拷应锟斤拷锟皆的凤拷锟斤拷
			//Field[] fs = clazz.getDeclaredFields(); // 取锟斤拷指锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
			for (Method method : ms) {
				String methodName = method.getName(); //取锟斤拷锟斤拷应锟斤拷锟叫碉拷锟斤拷锟叫凤拷锟斤拷(setxxx., getxxx,toStirng(),....)
				//锟皆硷拷值锟皆碉拷锟斤拷式锟斤拷锟皆斤拷锟斤拷娲拷锟斤拷锟斤拷bug
				//TODO : 锟斤拷request锟斤拷锟斤拷锟斤拷锟斤拷械牟锟斤拷锟斤拷装锟斤拷map<String, String[]>
				if(    map.containsKey(methodName)  ){
					String[] values =map.get(methodName);
					Class parameterType = method.getParameterTypes()[0];  //锟介看 set 锟斤拷锟斤拷 锟侥诧拷锟斤拷锟斤拷锟斤拷       setNid( Integer)   setTitle( String )    setIns( List<String> )
				
					if(parameterType.getName().startsWith("[")){
						extractArrayParameterType(t, method, values,									parameterType);
					}else{
						extractSingleParameterType(t, method, values,									parameterType);
					}
				}
			}
		} catch (
		Exception e) {
			throw new RuntimeException(e);
		} 
		return t;
	}
	
	/**
	 * 
	 * @param request  锟斤拷锟斤拷锟斤拷锟斤拷锟�
	 * @param clazz  要转锟斤拷锟缴碉拷实锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
	 * @return  实锟斤拷锟斤拷锟�
	 */
	protected <T> T getReqParamObj(HttpServletRequest request, Class<T> clazz) {
		T t = null;
		Map<String, String[]> map = request.getParameterMap(); //取锟斤拷锟斤拷锟斤拷request锟叫的诧拷锟斤拷
		return getReqParamObj( map, clazz);
	}
	
	
	//锟斤拷锟斤拷锟斤拷锟斤拷锟角帮拷锟斤拷锟斤拷一锟斤拷set   锟轿筹拷map锟侥硷拷
	private Map<String, String[]> parseRequestMap(Map<String, String[]> map) {
		Map<String, String[]> newMap = new HashMap<String, String[]>();
		for(Map.Entry<String, String[]> entry : map.entrySet()){
			String key = entry.getKey();
			key = "set" + key.substring(0,1).toUpperCase() + key.substring(1);
			newMap.put(key, entry.getValue());
		}
		return newMap;
	}
	
	public void writeJson(HttpServletResponse response,HttpServletRequest req, Object obj)
			throws IOException {
		response.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));
		response.setHeader("P3P", "CP=CAO PSA OUR");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setContentType("text/plain;charset=utf-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String jsonString = gson.toJson(obj);
		out.println(jsonString);
		out.flush();
		out.close();
	}

	private void writeXssJson(HttpServletResponse response,HttpServletRequest req, JsonModel jm, String callback) throws IOException {
		response.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));
		response.setHeader("P3P", "CP=CAO PSA OUR");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setContentType("text/javascript;charset=utf-8");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String jsonString = gson.toJson(jm);
		//   abc( '{code:1,obj:{}}' )
		out.println(    callback+"("+  jsonString+")"     );
		out.flush();
		out.close();
	}
	
	
	public void outJson(HttpServletRequest req, HttpServletResponse resp, JsonModel jm) throws IOException {
		
		
		if (req.getParameter("XSS_HTTP_REQUEST_CALLBACK") != null
				&& !"".equals(req.getParameter("XSS_HTTP_REQUEST_CALLBACK"))) {  // abc
			// 是跨站请求
			writeXssJson( resp,req,jm, req.getParameter("XSS_HTTP_REQUEST_CALLBACK") );
		}else{
			writeJson(resp,req, jm);
		}



	}
	

}
