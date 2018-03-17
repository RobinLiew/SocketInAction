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
