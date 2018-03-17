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
