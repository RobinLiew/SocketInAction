# SocketInAction
Java Socket网络编程实战练习

### Socket基础类
- 服务端
```
package com.robinliew.basedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 知识要点：
 * 1.无论是服务器端ServerSocket通过accept()方法接收到的Socket，还是客户端连接服务器端的Socket，
 * 在创建输入输出流时不允许两个同时首先创建输入流，否则会堵塞Socket通道。
 * 
 * 2.服务器Socket和客户端Socket可以创建多个输入输出流对象，但是两端创建的个数必须保持对应，
 * 即通过客户端Socket创建多少个输入输出流对象，对应的服务器端的ServerSocket通过accepte()方法
 * 接收到Socket也必须创建多少个输入输出流对象，否则抛出java.io.StreamCorruptedException异常。
 * 
 * @author RobinLiew
 *
 */
public class ServerDemo {

	public static void main(String[] args) {
		
		try {
			//专门用来创建Socket服务器的类
			ServerSocket server=new ServerSocket(10000);//这个服务器使用10000端口号
			/*
			 * 当一个客户端程序建立一个连接端口号为10000的Socket连接时，
			 * 服务器对象server便响应这个连接，并且用accept()方法创建
			 * 一个Socket对象,服务器端可以利用这个对象与客户端通讯。
			 */
			Socket socketServer=server.accept();
			//输入流和输出流的封装
			BufferedReader in=new BufferedReader(new InputStreamReader(socketServer.getInputStream()));
			PrintWriter out=new PrintWriter(socketServer.getOutputStream(),true);
			//得到客户端的输入
			String inFromClient=in.readLine();
			//向客户端发送数据
			out.println();
			//关闭两个数据流
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

```
- 客户端

```
package com.robinliew.basedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * 代码中这两个方法read()和readLine()都会读取对端发送过来的数据，如果无数据可读，就会阻塞直到有数据可读。或者到达流的末尾，这个时候分别返回-1和null。
 *	这个特性使得编程非常方便也很高效。
 *	但是这样也有一个问题，就是如何让程序从这两个方法的阻塞调用中返回。
 *
 *	总结一下，有这么几个方法：
 *	1）发送完后调用Socket的shutdownOutput()方法关闭输出流，这样对端的输入流上的read操作就会返回-1。
 *	注意不能调用socket.getInputStream().close()。这样会导致socket被关闭。
 *	当然如果不需要继续在socket上进行读操作，也可以直接关闭socket。
 *	但是这个方法不能用于通信双方需要多次交互的情况。
 *
 *	2）发送数据时，约定数据的首部固定字节数为数据长度。这样读取到这个长度的数据后，就不继续调用read方法。
 *
 *	3）为了防止read操作造成程序永久挂起，还可以给socket设置超时。
 *	如果read()方法在设置时间内没有读取到数据，就会抛出一个java.net.SocketTimeoutException异常。
 *	例如下面的方法设定超时3秒。
 *	socket.setSoTimeout(3000);
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

```

- Socket设置字符流的编码

```
package com.robinliew.basedemo2;

import java.io.DataInputStream;   
import java.io.DataOutputStream;   
import java.io.IOException;   
import java.net.ServerSocket;   
import java.net.Socket; 

/**
 * 注意：
 * 1.Socket的发送与接收是需要同步进行的，即客户端发送一条信息，服务器必需先接收这条信息，   
* 而后才可以向客户端发送信息，否则将会有运行时出错。   
* 
* 2.注意这里我们前面的例子处理字符流默认使用Unicode编码，这里通过
* writeUTF在写入数据流的时候会加上两个字节以表示字节的长度，且使用UTF-8编码。
 * @author RobinLiew
 *
 */
public class ServerDemoUTF {
	public static void main(String[] args) {   
		ServerSocket ss = null;   
		try {   
			ss = new ServerSocket(10002);   
			//服务器接收到客户端的数据后，创建与此客户端对话的Socket   
			Socket socket = ss.accept();   
			//用于向客户端发送数据的输出流   
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());   
			//用于接收客户端发来的数据的输入流   
			DataInputStream dis = new DataInputStream(socket.getInputStream());   
			System.out.println("服务器接收到客户端的连接请求：" + dis.readUTF());   
			//服务器向客户端发送连接成功确认信息   
			dos.writeUTF("接受连接请求，连接成功!");   
			//不需要继续使用此连接时，关闭连接   
			socket.close();   
			ss.close();   
		} catch (IOException e) {   
			e.printStackTrace();   
		}   
	}   
}

```

- Socket URL通讯

```
package com.robinliew.basedemo2;

import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.File;  
import java.io.FileWriter;  
import java.io.InputStreamReader;  
import java.net.URL;

/**
 * basedemo中的例子中客户端的知道服务端的IP地址与端口号后可以与其进行通信，但如果我们只知道网络URL地址，不知道IP呢？
 * 
 * 在JAVA中，Java.net包里面的类是进行网络编程的，其中java.net.URL类和java.net.URLConection类使编程者方便地
 * 利用URL在Internet上进行网络通信。
 * 
 * @author RobinLiew
 *
 */
public class URLReader {  
	
	public static void main(String[] args) throws Exception {  
		
		//构建一URL对象
		URL tirc = new URL("https://robinliew.github.io/");  
		//用于存放服务器返回的数据
		File writeFile = new File("d:\\information.html");  
		
		// 使用openStream得到一输入流并由此构造一个BufferedReader对象 
		BufferedReader in = new BufferedReader(new InputStreamReader(tirc.openStream()));  
		BufferedWriter bos = new BufferedWriter(new FileWriter(writeFile));  
		 
		String inputLine;  
		 
		while ((inputLine = in.readLine()) != null){  
			bos.write(inputLine);  
			System.out.println(inputLine);  
		}  
		bos.flush();  
		in.close();  
		bos.close();  
	}  
} 

``` 

- Socket服务端与客户端交互
服务端
```
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

```

客户端

```
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

```

### Socket多线程一般设计原则

```
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

```

### Socket传输的序列化对象介绍

```
package com.robinliew.transferdemo;

import java.io.ByteArrayInputStream;  
import java.io.ByteArrayOutputStream;   
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.ObjectInputStream;  
import java.io.ObjectOutputStream;  
import java.io.OutputStream;  
import java.util.Date; 

/**
 * 序列化是一种对象持久化的手段。普遍应用在网络传输、RMI等场景中.
 * 
 * 序列化与反序列化：
 * 
 * 1.只要一个类实现了java.io.Serializable接口，那么它就可以被序列化。
 * 
 * 2.通过ObjectOutputStream和ObjectInputStream对对象进行序列化及反序列化。
 * 
 * 3.虚拟机是否允许反序列化，不仅取决于类路径和功能代码是否一致，一个非常重要的一点是两个类的序列化 ID 是否一致（就是 private static final long serialVersionUID）
 * 
 * 4.序列化并不保存静态变量。
 * 
 * 5.要想将父类对象也序列化，就需要让父类也实现Serializable 接口。
 * 
 * 6.Transient 关键字的作用是控制变量的序列化，在变量声明前加上该关键字，可以阻止该变量被序列化到文件中，
 * 		在被反序列化后，transient 变量的值被设为初始值，如 int 型的是 0，对象型的是 null。
 * 
 * 7.服务器端给客户端发送序列化对象数据，对象中有一些数据是敏感的，比如密码字符串等，希望对该密码字段在序列化时，进行加密，
 * 		而客户端如果拥有解密的密钥，只有在客户端进行反序列化时，才可以对密码进行读取，这样可以一定程度保证序列化对象的数据安全。
 * 
 *  
 *  这个例子是用文件持久化展示，Socket传输中亦是如此。
 * @author RobinLiew
 *
 */
public class Persistence {  
	public static void main(String[] args) {  
		Persistence.savePerson();  
		Persistence.getPerson();  
	}  
	public static void getPerson() {  
		try {  
			InputStream in = new FileInputStream("d:\\person.dat");  
			ObjectInputStream dataInput = new ObjectInputStream(in);  
			Person p = (Person) dataInput.readObject();  
			System.out.println(p.getName());  
			System.out.println(p.getTall());  
			System.out.println(p.getBirthday());  
			System.out.println(p.getAddress().getCity());  
			System.out.println(p.getAddress().getStreet());  
		} catch (Exception e) {  
		// TODO Auto-generated catch block  
			e.printStackTrace();  
		}  
	}  
	public static void savePerson() {  
		Person p = new Person();  
		p.setName("Robin Liew");  
		p.setTall(176);  
		p.setBirthday(new Date());  
		p.setAddress(new Address("xian", "tumen"));  
		OutputStream out = new ByteArrayOutputStream();  
		try {  
			OutputStream fileOut = new FileOutputStream(new File(  
			"d:\\person.dat"));  
			ObjectOutputStream dataOut = new ObjectOutputStream(fileOut);  
			dataOut.writeObject(p);  
			dataOut.close();  
			fileOut.close();  
		} catch (IOException e) {  
		// TODO Auto-generated catch block  
			e.printStackTrace();  
		}  
	}  
}  

```

### Socket网络交互的例子
- 单线程
	- 服务端
	

	```
	package com.robinliew.inaction.singlethread;

	import java.io.BufferedReader;  
	import java.io.IOException;  
	import java.io.InputStreamReader;  
	import java.io.PrintWriter;  
	import java.net.ServerSocket;  
	import java.net.Socket;  
	
	/**
	 * Server类，这个类用来监听10000端口，并从这个端口接收消息然后输出，当收到“bye”时退出。
	 * @author RobinLiew
	 *
	 */
	public class MyServer {  
	    public static void main(String[] args) throws IOException {  
	        ServerSocket server = new ServerSocket(10000);  
	        Socket socket = server.accept();  
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
	        PrintWriter out = new PrintWriter(socket.getOutputStream());  
	          
	        while (true) {  
	            String msg = in.readLine();  
	            System.out.println(msg);  
	            out.println("Server received " + msg);  
	            out.flush();  
	            if (msg.equals("bye")) {  
	                break;  
	            }  
	        }  
	        socket.close();  
	    }  
	} 

	```
	- 客户端
	
	```
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

	```

- 多线程
	- 服务端
	

	```
	package com.robinliew.inaction.multithread;

	import java.io.BufferedReader;  
	import java.io.IOException;  
	import java.io.InputStreamReader;  
	import java.io.PrintWriter;  
	import java.net.ServerSocket;  
	import java.net.Socket;  
	 
	/**
	 * singlethread包中的例子有一个问题就是Server只能接受一个Client请求，当第一个Client连接后就占据了这个位置，后续Client不能再继续连接，所以需要做些改动，
	 * 当Server没接受到一个Client连接请求之后，都把处理流程放到一个独立的线程里去运行，然后等待下一个Client连接请求，这样就不会阻塞Server端接收请求了。
	 * 每个独立运行的程序在使用完Socket对象之后要将其关闭。
	 * 
	 * @author RobinLiew
	 *
	 */
	public class MyServer {  
	    public static void main(String[] args) throws IOException {  
	        ServerSocket server = new ServerSocket(10000);  
	          
	        while (true) {  
	            Socket socket = server.accept();  
	            invoke(socket);  
	        }  
	    }  
	      
	    private static void invoke(final Socket client) throws IOException {  
	        new Thread(new Runnable() {  
	            public void run() {  
	                BufferedReader in = null;  
	                PrintWriter out = null;  
	                try {  
	                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));  
	                    out = new PrintWriter(client.getOutputStream());  
	 
	                    while (true) {  
	                        String msg = in.readLine();  
	                        System.out.println(msg);  
	                        out.println("Server received " + msg);  
	                        out.flush();  
	                        if (msg.equals("bye")) {  
	                            break;  
	                        }  
	                    }  
	                } catch(IOException ex) {  
	                    ex.printStackTrace();  
	                } finally {  
	                    try {  
	                        in.close();  
	                    } catch (Exception e) {}  
	                    try {  
	                        out.close();  
	                    } catch (Exception e) {}  
	                    try {  
	                        client.close();  
	                    } catch (Exception e) {}  
	                }  
	            }  
	        }).start();  
	    }  
	} 

	```
	
	- 客户端
	```
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

	```

### Socket传输对象
- 传输序列化对象
	- 服务端
	```
	package com.robinliew.inaction.transferobject;

	import java.io.*;  
	import java.net.ServerSocket;  
	import java.net.Socket;  
	import java.util.logging.Level;  
	import java.util.logging.Logger;  
	
	/**
	 * 使用Java Socket来传输对象
	 * @author RobinLiew
	 *
	 */
	public class MyServer {  
	 
	    private final static Logger logger = Logger.getLogger(MyServer.class.getName());  
	      
	    public static void main(String[] args) throws IOException {  
	        ServerSocket server = new ServerSocket(10000);  
	 
	        while (true) {  
	            Socket socket = server.accept();  
	            invoke(socket);  
	        }  
	    }  
	 
	    private static void invoke(final Socket socket) throws IOException {  
	        new Thread(new Runnable() {  
	            public void run() {  
	                ObjectInputStream is = null;  
	                ObjectOutputStream os = null;  
	                try {  
	                    is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));  
	                    os = new ObjectOutputStream(socket.getOutputStream());  
	 
	                    Object obj = is.readObject();  
	                    User user = (User)obj;  
	                    System.out.println("user: " + user.getName() + "/" + user.getPassword());  
	 
	                    user.setName(user.getName() + "_new");  
	                    user.setPassword(user.getPassword() + "_new");  
	 
	                    os.writeObject(user);  
	                    os.flush();  
	                } catch (IOException ex) {  
	                    logger.log(Level.SEVERE, null, ex);  
	                } catch(ClassNotFoundException ex) {  
	                    logger.log(Level.SEVERE, null, ex);  
	                } finally {  
	                    try {  
	                        is.close();  
	                    } catch(Exception ex) {}  
	                    try {  
	                        os.close();  
	                    } catch(Exception ex) {}  
	                    try {  
	                        socket.close();  
	                    } catch(Exception ex) {}  
	                }  
	            }  
	        }).start();  
	    }  
	} 

	```
	- 客户端
	

	```
	package com.robinliew.inaction.transferobject;

	import java.io.BufferedInputStream;  
	import java.io.IOException;  
	import java.io.ObjectInputStream;  
	import java.io.ObjectOutputStream;  
	import java.net.Socket;  
	import java.util.logging.Level;  
	import java.util.logging.Logger;  
	
	/**
	 * 使用Java Socket来传输对象
	 * @author RobinLiew
	 *
	 */
	public class MyClient {  
	      
	    private final static Logger logger = Logger.getLogger(MyClient.class.getName());  
	      
	    public static void main(String[] args) throws Exception {  
	        for (int i = 0; i < 100; i++) {  
	            Socket socket = null;  
	            ObjectOutputStream os = null;  
	            ObjectInputStream is = null;  
	              
	            try {  
	                socket = new Socket("localhost", 10000);  
	      
	                os = new ObjectOutputStream(socket.getOutputStream());  
	                User user = new User("user_" + i, "password_" + i);  
	                os.writeObject(user);  
	                os.flush();  
	                  
	                is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));  
	                Object obj = is.readObject();  
	                if (obj != null) {  
	                    user = (User)obj;  
	                    System.out.println("user: " + user.getName() + "/" + user.getPassword());  
	                }  
	            } catch(IOException ex) {  
	                logger.log(Level.SEVERE, null, ex);  
	            } finally {  
	                try {  
	                    is.close();  
	                } catch(Exception ex) {}  
	                try {  
	                    os.close();  
	                } catch(Exception ex) {}  
	                try {  
	                    socket.close();  
	                } catch(Exception ex) {}  
	            }  
	        }  
	    }  
	} 

	```
- 传输压缩对象
	- 服务端
	```
	package com.robinliew.inaction.transferGZIPobject;

	import java.io.IOException;  
	import java.io.ObjectInputStream;  
	import java.io.ObjectOutputStream;  
	import java.net.ServerSocket;  
	import java.net.Socket;  
	import java.util.logging.Level;  
	import java.util.logging.Logger;  
	import java.util.zip.GZIPInputStream;  
	import java.util.zip.GZIPOutputStream;  
	
	/**
	 * transfer包中用Java Socket来传输对象，但是在有些情况下比如网络环境不好或者对象比较大的情况下需要把数据对象进行压缩然后在传输，
	 * 此时就需要压缩这些对象流，此时就可以GZIPInputStream和GZIPOutputStream来处理一下socket的InputStream和OutputStream。
	 * @author RobinLiew
	 *
	 */
	public class MyServer {  
	 
	    private final static Logger logger = Logger.getLogger(MyServer.class.getName());  
	      
	    public static void main(String[] args) throws IOException {  
	        ServerSocket server = new ServerSocket(10000);  
	 
	        while (true) {  
	            Socket socket = server.accept();  
	            socket.setSoTimeout(10 * 1000);  
	            invoke(socket);  
	        }  
	    }  
	 
	    private static void invoke(final Socket socket) throws IOException {  
	        new Thread(new Runnable() {  
	            public void run() {  
	                GZIPInputStream gzipis = null;  
	                ObjectInputStream ois = null;  
	                GZIPOutputStream gzipos = null;  
	                ObjectOutputStream oos = null;  
	                  
	                try {  
	                    gzipis = new GZIPInputStream(socket.getInputStream());  
	                    ois = new ObjectInputStream(gzipis);  
	                    gzipos = new GZIPOutputStream(socket.getOutputStream());  
	                    oos = new ObjectOutputStream(gzipos);  
	 
	                    Object obj = ois.readObject();  
	                    User user = (User)obj;  
	                    System.out.println("user: " + user.getName() + "/" + user.getPassword());  
	 
	                    user.setName(user.getName() + "_new");  
	                    user.setPassword(user.getPassword() + "_new");  
	 
	                    oos.writeObject(user);  
	                    oos.flush();  
	                    gzipos.finish();  
	                } catch (IOException ex) {  
	                    logger.log(Level.SEVERE, null, ex);  
	                } catch(ClassNotFoundException ex) {  
	                    logger.log(Level.SEVERE, null, ex);  
	                } finally {  
	                    try {  
	                        ois.close();  
	                    } catch(Exception ex) {}  
	                    try {  
	                        oos.close();  
	                    } catch(Exception ex) {}  
	                    try {  
	                        socket.close();  
	                    } catch(Exception ex) {}  
	                }  
	            }  
	        }).start();  
	    }  
	} 

	```
	- 客户端
	

	```
	package com.robinliew.inaction.transferGZIPobject;

	import java.io.IOException;  
	import java.io.ObjectInputStream;  
	import java.io.ObjectOutputStream;  
	import java.net.InetSocketAddress;  
	import java.net.Socket;  
	import java.net.SocketAddress;  
	import java.util.logging.Level;  
	import java.util.logging.Logger;  
	import java.util.zip.GZIPInputStream;  
	import java.util.zip.GZIPOutputStream;  
	
	/**
	 * Client也和Server端类似，同样要不socket的XXXStream包装成GZIPXXXStream，然后再包装成ObjectXXXStream
	 * @author RobinLiew
	 *
	 */
	public class MyClient {  
	      
	    private final static Logger logger = Logger.getLogger(MyClient.class.getName());  
	      
	    public static void main(String[] args) throws Exception {  
	        for (int i = 0; i < 10; i++) {  
	            Socket socket = null;  
	            GZIPOutputStream gzipos = null;  
	            ObjectOutputStream oos = null;  
	            GZIPInputStream gzipis = null;  
	            ObjectInputStream ois = null;  
	              
	            try {  
	                socket = new Socket();  
	                SocketAddress socketAddress = new InetSocketAddress("localhost", 10000);   
	                socket.connect(socketAddress, 10 * 1000);  
	                socket.setSoTimeout(10 * 1000);  
	                  
	                gzipos = new GZIPOutputStream(socket.getOutputStream());  
	                oos = new ObjectOutputStream(gzipos);  
	                User user = new User("user_" + i, "password_" + i);  
	                oos.writeObject(user);  
	                oos.flush();  
	                gzipos.finish();  
	                  
	                gzipis = new GZIPInputStream(socket.getInputStream());  
	                ois = new ObjectInputStream(gzipis);  
	                Object obj = ois.readObject();  
	                if (obj != null) {  
	                    user = (User)obj;  
	                    System.out.println("user: " + user.getName() + "/" + user.getPassword());  
	                }  
	            } catch(IOException ex) {  
	                logger.log(Level.SEVERE, null, ex);  
	            }  
	            try {  
	                oos.close();  
	            } catch (IOException e) {  
	            }  
	            try {  
	                ois.close();  
	            } catch (IOException e) {  
	            }  
	            try {  
	                socket.close();  
	            } catch (IOException e) {  
	            }  
	        }  
	    }  
	} 

	```
- 传输加密对象
	- 服务端
	

	```
	package com.robinliew.inaction.transferSSLobject;

	import java.io.BufferedInputStream;  
	import java.io.IOException;  
	import java.io.ObjectInputStream;  
	import java.io.ObjectOutputStream;  
	import java.net.ServerSocket;  
	import java.net.Socket;  
	import java.util.logging.Level;  
	import java.util.logging.Logger;  
	 
	import javax.net.ServerSocketFactory;  
	import javax.net.ssl.SSLServerSocketFactory;  
	
	/**
	 * 这里需要用到ServerSocketFactory类来创建SSLServerSocket类实例，然后在通过SSLServerSocket来获取SSLSocket实例，
	 * 这里考虑到面向对象中的面向接口编程的理念，所以代码中并没有出现SSLServerSocket和SSLSocket，而是用了他们的父类ServerSocket和Socket。
	 * 在获取到ServerSocket和Socket实例以后，剩下的代码就和不使用加密方式一样了。
	 * @author RobinLiew
	 *
	 */
	public class MyServer {  
	      
	    private final static Logger logger = Logger.getLogger(MyServer.class.getName());  
	      
	    public static void main(String[] args) {  
	        try {  
	            ServerSocketFactory factory = SSLServerSocketFactory.getDefault();  
	            ServerSocket server = factory.createServerSocket(10000);  
	              
	            while (true) {  
	                Socket socket = server.accept();  
	                invoke(socket);  
	            }  
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	        }  
	    }  
	      
	    private static void invoke(final Socket socket) throws IOException {  
	        new Thread(new Runnable() {  
	            public void run() {  
	                ObjectInputStream is = null;  
	                ObjectOutputStream os = null;  
	                try {  
	                    is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));  
	                    os = new ObjectOutputStream(socket.getOutputStream());  
	 
	                    Object obj = is.readObject();  
	                    User user = (User)obj;  
	                    System.out.println("user: " + user.getName() + "/" + user.getPassword());  
	 
	                    user.setName(user.getName() + "_new");  
	                    user.setPassword(user.getPassword() + "_new");  
	 
	                    os.writeObject(user);  
	                    os.flush();  
	                } catch (IOException ex) {  
	                    logger.log(Level.SEVERE, null, ex);  
	                } catch(ClassNotFoundException ex) {  
	                    logger.log(Level.SEVERE, null, ex);  
	                } finally {  
	                    try {  
	                        is.close();  
	                    } catch(Exception ex) {}  
	                    try {  
	                        os.close();  
	                    } catch(Exception ex) {}  
	                    try {  
	                        socket.close();  
	                    } catch(Exception ex) {}  
	                }  
	            }  
	        }).start();  
	    }  
	} 

	```

	- 客户端
	

	```
	package com.robinliew.inaction.transferSSLobject;

	import java.io.BufferedInputStream;  
	import java.io.IOException;  
	import java.io.ObjectInputStream;  
	import java.io.ObjectOutputStream;  
	import java.net.Socket;  
	import java.util.logging.Level;  
	import java.util.logging.Logger;  
	 
	import javax.net.SocketFactory;  
	import javax.net.ssl.SSLSocketFactory;  
	 
	public class MyClient {  
	      
	private final static Logger logger = Logger.getLogger(MyClient.class.getName());  
	 
	    public static void main(String[] args) throws Exception {  
	        for (int i = 0; i < 100; i++) {  
	            Socket socket = null;  
	            ObjectOutputStream os = null;  
	            ObjectInputStream is = null;  
	              
	            try {  
	                SocketFactory factory = SSLSocketFactory.getDefault();  
	                socket = factory.createSocket("localhost", 10000);  
	      
	                os = new ObjectOutputStream(socket.getOutputStream());  
	                User user = new User("user_" + i, "password_" + i);  
	                os.writeObject(user);  
	                os.flush();  
	                  
	                is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));  
	                Object obj = is.readObject();  
	                if (obj != null) {  
	                    user = (User)obj;  
	                    System.out.println("user: " + user.getName() + "/" + user.getPassword());  
	                }  
	            } catch(IOException ex) {  
	                logger.log(Level.SEVERE, null, ex);  
	            } finally {  
	                try {  
	                    is.close();  
	                } catch(Exception ex) {}  
	                try {  
	                    os.close();  
	                } catch(Exception ex) {}  
	                try {  
	                    socket.close();  
	                } catch(Exception ex) {}  
	            }  
	        }  
	    }  
	} 

	```

