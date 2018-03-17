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
