package com.frend.demo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 编写监听服务类
 * @author Administrator
 */
public class Server {

	private static ExecutorService executor = Executors.newFixedThreadPool(10);
	
	private static final HashMap<String,Class> serviceRegisty = new HashMap<String, Class>();
	
	/**
	 * 提供一个数组保存所注册的服务接口及实现类
	 * @param serviceInterface
	 * @param impl
	 */
	public void register(Class serviceInterface,Class impl) {
		//注册服务
		serviceRegisty.put(serviceInterface.getName(), impl);
	}
	
	/**
	 * 启动一个阻塞式的Socket服务用于等待客户端发送的调用请求,当收到请求后将码流反序列化成对象
	 * 并根据接口从注册列表中寻找具体实现类,最终通过反射的调用该实现类返回结果
	 * @param port
	 * @throws IOException
	 */
	public void start(int port) throws IOException {
		final ServerSocket server = new ServerSocket();
		server.bind(new InetSocketAddress(port));
		System.out.println("服务已启动");
		while(true) {
			executor.execute(new Runnable() {
				
				public void run() {
					Socket socket = null;
					ObjectInputStream input  = null;
					ObjectOutputStream output = null;
					try {
						socket = server.accept();
						//接收服务调用请求,将码流反序列化定位具体服务
						input = new ObjectInputStream(socket.getInputStream());
						String serviceName = input.readUTF();
						String methodName = input.readUTF();
						Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
						Object[] arguments = (Object[]) input.readObject();
						//在服务注册表中根据调用的服务获取具体的实现类
						Class serviceClass = serviceRegisty.get(serviceName);
						if(serviceClass == null) {
							throw new ClassNotFoundException(serviceName + "未找到"); 
						}
						Method method = serviceClass.getMethod(methodName, parameterTypes);
						//调用获取结果
						Object result = method.invoke(serviceClass.newInstance(),arguments);
						//将结果序列化后送回客户端
						output = new ObjectOutputStream(socket.getOutputStream());
						output.writeObject(result);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						//关闭资源
						try {
							if(socket != null) socket.close();
							if(input == null) input.close();
							if(output == null) output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}
	
}
