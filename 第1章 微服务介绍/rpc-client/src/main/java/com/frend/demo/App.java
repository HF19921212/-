package com.frend.demo;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

	public static void main(String[] args) throws IOException {
		HelloService service = Client.get(HelloService.class, new InetSocketAddress("localhost", 8020));
		System.out.println(service.hello("RPC"));
	}

}
