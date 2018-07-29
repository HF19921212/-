package com.frend.demo;

public class HelloServiceImpl implements HelloService {

	public String hello(String name) {
		System.out.println("收到消息：" + name);
		return "你好：" + name;
	}

}
