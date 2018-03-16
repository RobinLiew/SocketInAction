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
