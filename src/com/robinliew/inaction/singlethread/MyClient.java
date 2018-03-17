package com.robinliew.inaction.singlethread;

import java.io.BufferedReader;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.net.Socket;  

/**
 * 一个Client类，这个类连接上面启动的Server类，然后接收任何用户输入，当遇到回车时发送字符串到Server上，当输入“bye”是退出。
 * @author RobinLiew
 *
 */
public class MyClient {  
    public static void main(String[] args) throws Exception {  
    	//注意创建Socket除了使用IP+port的形式，还可以用 服务器主机名+port的形式
        Socket socket = new Socket("localhost", 10000);  
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
        PrintWriter out = new PrintWriter(socket.getOutputStream());  
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  
 
        while (true) {  
            String msg = reader.readLine();  
            out.println(msg);  
            out.flush();  
            if (msg.equals("bye")) {  
                break;  
            }  
            System.out.println(in.readLine());  
        }  
        socket.close();  
    }  
} 
