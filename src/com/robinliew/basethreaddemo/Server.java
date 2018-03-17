package com.robinliew.basethreaddemo;

import java.io.*;   
import java.net.*;   
import java.util.*;   
import java.lang.*; 

/**
 * Java Socket线程的设计原理：
 * 1.服务器端接受客户端的连接请求，同时启动一个线程处理这个连接，线程不停的读取客户端输入，然后把输入加入队列中，等候处理。
 * 2.在线程启动的同时将线程加入队列中，以便在需要的时候定位和取出。
 * 
 * 下面是一个简易聊天室的例子
 * @author RobinLiew
 *
 */
public class Server extends ServerSocket   {   
	private static ArrayList User_List = new ArrayList();   
	private static ArrayList Threader = new ArrayList();   
	private static LinkedList Message_Array = new LinkedList();   
	private static int Thread_Counter = 0;   
	private static boolean isClear = true;   
	protected static final int SERVER_PORT = 10000;   
	protected FileOutputStream LOG_FILE = new FileOutputStream("d:/connect.log", true);   
	public Server() throws FileNotFoundException, IOException   {   
		super(SERVER_PORT);   
		new Broadcast();   
		//append connection log   
		Calendar now = Calendar.getInstance();   
		String str = "[" + now.getTime().toString() + "] Accepted a connection";   
		byte[] tmp = str.getBytes();   
		LOG_FILE.write(tmp);   
		try {   
			while (true) {   
				Socket socket = accept();   
				new CreateServerThread(socket);   
			}   
		}finally {   
			close();   
		}   
	}   
	public static void main(String[] args) throws IOException {   
		new Server();   
	}   
	//Broadcast处理存放消息的队列   
	class Broadcast extends Thread {   
		public Broadcast() {   
			start();   
		}   
		public void run() {   
			while (true) {   
				if (!isClear){   
					String tmp = (String)Message_Array.getFirst();   
					for (int i = 0; i < Threader.size(); i++) {   
						CreateServerThread client = (CreateServerThread)Threader.get(i);   
						client.sendMessage(tmp);   
					}  
					Message_Array.removeFirst();   
					isClear = Message_Array.size() > 0 ? false : true;   
				}   
			}   
		}   
	}   
	//--- CreateServerThread   
	class CreateServerThread extends Thread {   
		private Socket client;   
		private BufferedReader in;   
		private PrintWriter out;   
		private String Username;   
		public CreateServerThread(Socket s) throws IOException {   
			client = s;   
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));   
			out = new PrintWriter(client.getOutputStream(), true);   
			out.println("--- Welcome to this chatroom ---");   
			out.println("Input your nickname:");   
			start();   
		}   
		public void sendMessage(String msg) {   
			out.println(msg);   
		}   
		public void run() {   
			try {   
				int flag = 0;   
				Thread_Counter++;   
				String line = in.readLine();   
				while (!line.equals("bye")) {   
					if (line.equals("l")) {   
						out.println(listOnlineUsers());   
						line = in.readLine();   
						continue;   
					}   
					if (flag++ == 0) {   
						Username = line;   
						User_List.add(Username);   
						out.println(listOnlineUsers());   
						Threader.add(this);   
						pushMessage("[< " + Username + " come on in >]");   
					} else {   
						pushMessage("<" + Username + ">" + line);   
					}   
					line = in.readLine();   
				}   
				out.println("--- See you, bye! ---");   
				client.close();   
			}catch (IOException e) {
				
			}finally {   
				try {  
					client.close();   
				} catch (IOException e) {
					
				}   
				Thread_Counter--;   
				Threader.remove(this);   
				User_List.remove(Username);   
				pushMessage("[< " + Username + " left>]");   
			}   
		}   
		private String listOnlineUsers() {   
			String s ="-+- Online list -+-1512";   
			for (int i = 0; i < User_List.size(); i++) {   
				s += "[" + User_List.get(i) + "]";   
			}   
			s += "-+---------------------+-";   
			return s;   
		}   
		private void pushMessage(String msg)   {   
			Message_Array.addLast(msg);   
			isClear = false;   
		}   
	}   
}  
