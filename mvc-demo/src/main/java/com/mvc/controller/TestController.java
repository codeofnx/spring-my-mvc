package com.mvc.controller;

import com.mvc.annotation.MyController;
import com.mvc.annotation.MyRequestMapping;

@MyController
public class TestController {
	
	@MyRequestMapping(path="/test",method="GET")
	public void test() {
		System.out.println("test!-----------≤‚ ‘£°£°£°");
		
	}

}
