package com.yc.web.utils;

import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.jspsmart.upload.File;
import com.jspsmart.upload.Files;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;

public class UploadUtil {

	private String path = "pics";// 上传的路�?
	private String allowed = "gif,jpg,png,jpeg";// 可以上传的类�?
	private String denied = "exe,bat,jsp,html,com";// 不允许上传的文件的类�?
	private long singleFileMaxSize = 1024 * 1024;// 单个文件�?��大小
	private long totalFileMaxSize = 1024 * 1024 * 20;// �?��文件大小

	public UploadUtil() {
	}

	public UploadUtil(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAllowed() {
		return allowed;
	}

	public void setAllowed(String allowed) {
		this.allowed = allowed;
	}

	public String getDenied() {
		return denied;
	}

	public void setDenied(String denied) {
		this.denied = denied;
	}

	public long getSingleFileMaxSize() {
		return singleFileMaxSize;
	}

	public void setSingleFileMaxSize(long singleFileMaxSize) {
		this.singleFileMaxSize = singleFileMaxSize;
	}

	public long getTotalFileMaxSize() {
		return totalFileMaxSize;
	}

	public void setTotalFileMaxSize(long totalFileMaxSize) {
		this.totalFileMaxSize = totalFileMaxSize;
	}
	
	public Map<String, String[]> upload(PageContext context) throws Exception {
		Map<String, String[]> map = new HashMap<String, String[]>();
		// 获取�?��图片上传的对�?
		SmartUpload su = new SmartUpload();
		// 初始�?
		su.initialize(context);
		// 设置上传的参�?
		su.setAllowedFilesList(allowed);
		su.setDeniedFilesList(this.denied);
		su.setMaxFileSize(singleFileMaxSize);
		su.setTotalMaxFileSize(totalFileMaxSize);
		su.setCharset("utf-8");
		// �?��上传
		su.upload();
		// 从sub中获取转换好的请求对象
		Request request = su.getRequest();
		// 获取请求区中的所有的表单元素
		Enumeration<String> keys = request.getParameterNames();
		String key;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			map.put(key, request.getParameterValues(key));   //   name=z  age=22   ins=["swim","reading]
		}
		// 判断用户有没有上传文件
		if (su.getFiles() != null && su.getFiles().getCount() > 0) {
			createDirInServer(context);
			Files fls = su.getFiles();
			Collection<File> file = fls.getCollection();// File是jspsmart.upload中的
			String fname;  //在服务器上保存的新文件名
			String newpath;   // 保存的路径
			for (File fl : file) {// 循环取出每一个文�?
				if (!fl.isMissing()) {// 如果当前上传的文件没有丢�?
					// 为了避免重名。给没个文件重新命名
					fname = new Date().getTime() + new Random().nextInt(10000) + "."+ fl.getFileExt();
					newpath = ".."+java.io.File.separatorChar + path +java.io.File.separatorChar + fname;
					// 将图片保存到服务
					fl.saveAs(newpath, SmartUpload.SAVE_VIRTUAL);
					String[] nps=new String[]{ newpath};
					map.put(fl.getFieldName(), nps);
				}
			}
		}
		return map;
	}

	/**
	 * 在服务器中创建一个目录
	 * @param context
	 */
	private void createDirInServer(PageContext context) {
		//先创建要保存文件的文件夹
		HttpServletRequest req=(HttpServletRequest) context.getRequest();
		//req.getServletPath()
		String projectPath=req.getServletPath();   //C:\apache-tomcat-7.0.67\webapps\j2eesample\
		java.io.File ff=new java.io.File(   projectPath);
		java.io.File f=new java.io.File(  ff.getParent()+java.io.File.separatorChar + path  );
		if(  !f.exists()){
			f.mkdirs();
		}
	}
}
