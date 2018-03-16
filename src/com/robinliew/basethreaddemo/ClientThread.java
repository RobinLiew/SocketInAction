package com.robinliew.basethreaddemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 为了和ServerThread配合使用
 * @author RobinLiew
 *
 */
public class ClientThread extends Thread {
	
	public void run() {
		try {
			//模拟10个客户端向服务端发送数据
			for(int i=0;i<10;i++){
				Socket socket = new Socket("10.10.40.59",10000);
				final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
				PrintWriter out = new PrintWriter(socket.getOutputStream(),true); 
				
				out.println("这是第："+i+"个客户向服务端发送数据！");
				
				//为了可以随时得到服务端返回的消息，创建该线程
		    	new Thread(){
		    		public void run() {
			    		while(true){
			    			//检查服务端返回的信息
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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
