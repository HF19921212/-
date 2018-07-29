package com.frend.demo;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        Server server = new Server();
        //注册服务
        server.register(HelloService.class, HelloServiceImpl.class);
        //启动并绑定端口
        server.start(8020);
    }
}
