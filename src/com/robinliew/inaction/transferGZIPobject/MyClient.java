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
