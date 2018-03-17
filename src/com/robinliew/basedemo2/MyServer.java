package com.robinliew.basedemo2;

import java.io.*;   
import java.net.*; 

/**
 * 服务端的一个简单示例代码
 * @author RobinLiew
 *
 */
public class MyServer {   
	public static void main(String[] args) throws IOException{ 
		
		ServerSocket server=new ServerSocket(10001);   
		Socket socketServer=server.accept();   
		BufferedReader in=new BufferedReader(new InputStreamReader(socketServer.getInputStream()));   
		PrintWriter out=new PrintWriter(socketServer.getOutputStream());   
		while(true){   
			String str=in.readLine();   
			System.out.println(str);   
			out.println("has receive....");   
			out.flush();   
			if(str.equals("end"))   
			break;   
		}   
		socketServer.close();   
	}   
}  
