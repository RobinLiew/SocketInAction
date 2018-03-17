package com.robinliew.transferdemo;

import java.io.Serializable;
import java.util.Date;



public class Person implements Serializable{

	private String name;
	private int tall;
	private Date birthday;
	private Address address;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getTall() {
		return tall;
	}
	public void setTall(int tall) {
		this.tall = tall;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
}
