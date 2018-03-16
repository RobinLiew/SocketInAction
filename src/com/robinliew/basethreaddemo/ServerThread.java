package com.robinliew.basethreaddemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ServerDemo的例子是单线程的，只能服务一个客户，现将其改为多线程
 * @author RobinLiew
 *
 */
public class ServerThread extends Thread{

	public ServerThread(Socket socketServer, int i) {
		System.out.println("这是第："+i+"个Socket！");
	}

	public void run() {
	    try{
	    	
			ServerSocket server = new ServerSocket(10000);  
			int i=0;
			//循环检测是否有客户连接到服务器上，如果有，则创建一个线程来服务这个客户
		    for(;;){
		    	Socket socketServer = server.accept();
		    	new ServerThread(socketServer,i);
		    	i++;
		    	
				//输入流和输出流的封装
				final BufferedReader in=new BufferedReader(new InputStreamReader(socketServer.getInputStream()));
				final PrintWriter out=new PrintWriter(socketServer.getOutputStream(),true);
		    	
				out.println("这是服务端向客户端的第："+i+"次响应！");
				
				//为了可以随时得到客户端传过来消息，创建该线程
		    	new Thread(){
		    		public void run() {
			    		while(true){
			    			checkInput(in);
			    			try {
			    				sleep(1000);//每1000毫秒检测一次
			    			} catch (InterruptedException e) {
			    				e.printStackTrace();
			    			}
			    		}
			    	}
		    	}.start();
		    	
		    	out.close();
		    	in.close();
		    }

	    }catch (IOException e){
	    	e.printStackTrace(); 
	    }   

	}
	
	private void checkInput(BufferedReader in) {
		String line;  
		try{
			if((line=in.readLine())!=null){
				//检测输入流中是否有新的数据  
				//将数据流中的消息显示出来 
			}
		}catch(Exception e){
			
		} 
	}

}
