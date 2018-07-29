package com.frend.demo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 远程调用类
 * @author Administrator
 *
 * @param <T>
 */
public class Client<T> {
	
	@SuppressWarnings("unchecked")
	public static <T> T get(final Class<?> serviceInterface,final InetSocketAddress addr){
		T instance =  (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), 
										    new Class<?>[] {serviceInterface}, 
										    new InvocationHandler() {
												public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
													
													Socket socket = null;
													ObjectOutputStream output = null;
													ObjectInputStream input = null;
													
													try {
														//链接服务端
														socket = new Socket();
														socket.connect(addr);
														//将调用的接口类 方法名 参数列表等序列化后发送给服务端提供者
														output = new ObjectOutputStream(socket.getOutputStream());
														output.writeUTF(serviceInterface.getName());
														output.writeUTF(method.getName());
														output.writeObject(method.getParameterTypes());
														output.writeObject(args);
														//同步阻塞等待服务器返回应发，获取应答返回
														input = new ObjectInputStream(socket.getInputStream());
														return input.readObject();
													} finally {
														if(socket != null) socket.close();
														if(output != null) output.close();
														if(input != null) input.close();
													}
												}
											});
				return instance;
	}

}
