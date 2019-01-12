package com.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mvc.annotation.MyController;
import com.mvc.annotation.MyRequestMapping;

public class MyDispatherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private List<String> CLASSES = new ArrayList<>();
	//存放 类:类实例
	private Map<String, Object> IOC_CONTAINER = new HashMap<>();
	//存放 url:方法
	private Map<String, Method> HANDLER_MAPPING = new HashMap<>();
	//存放 url:类实例
	private Map<String, Object> CONTROLLER_MAP = new HashMap<>();


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doGet");
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doPost");
		if (HANDLER_MAPPING.isEmpty()) {
			return;
		}
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		url = url.replace(contextPath, "").replaceAll("/+", "/");
		if (!this.HANDLER_MAPPING.containsKey(url)) {
			resp.getWriter().write("404 NOT FOUND!");
			return;
		}
		Method method = this.HANDLER_MAPPING.get(url);
		try {
			method.invoke(this.CONTROLLER_MAP.get(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.service(req, resp);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		// 拼装class 
		String controllerPackage = "com.mvc.controller";
		URL url = this.getClass().getClassLoader().getResource("/" + controllerPackage.replaceAll("\\.", "/"));
		File dir = new File(url.getFile());
		for (File file : dir.listFiles()) {
				String className = controllerPackage + "." + file.getName().replace(".class", "");
				CLASSES.add(className);
		}
		if (CLASSES.isEmpty()) {
			return;
		}
		for (String className : CLASSES) {
			try {
				Class<?> clazz = Class.forName(className);
				//将带有MyController标记的class放到IOC容器
				if (clazz.isAnnotationPresent(MyController.class)) {
					IOC_CONTAINER.put(clazz.getSimpleName(), clazz.newInstance());
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
		if (IOC_CONTAINER.isEmpty()) {
			return;
		}
		
		try {
			for (Entry<String, Object> entry : IOC_CONTAINER.entrySet()) {
				Class<? extends Object> clazz = entry.getValue().getClass();
				if (!clazz.isAnnotationPresent(MyController.class)) {
					continue;
				}
				Object instance = entry.getValue();
				
				//类url
				String classUrl ="";
				if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
					MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
					classUrl = annotation.path();
				}
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (!method.isAnnotationPresent(MyRequestMapping.class)) {
						continue;
					}
					MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
					String methodUrl = annotation.path();
 
					methodUrl = (classUrl + "/" + methodUrl).replaceAll("/+", "/");
					HANDLER_MAPPING.put(methodUrl, method);
					CONTROLLER_MAP.put(methodUrl,  instance);
					System.out.println(methodUrl + "," + method);
				}
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

}
