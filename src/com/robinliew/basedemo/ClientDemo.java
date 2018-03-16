package com.robinliew.basedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author RobinLiew
 *
 */
public class ClientDemo {
	
	public static void main(String[] args) {
		
		try {
			/*
			 * 客户端需要用服务器所在机器的ip以及服务器的端口作为参数创建一个Socket对象
			 */
			Socket socket = new Socket("10.10.40.59",10000);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
			PrintWriter out = new PrintWriter(socket.getOutputStream(),true); 
			//得到服务端的输入
			String inFromServer=in.readLine();
			//向服务端发送数据
			out.println();
			//关闭两个数据流
			out.close();
			in.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
	}
}
