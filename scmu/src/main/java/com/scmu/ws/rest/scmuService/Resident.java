package com.scmu.ws.rest.scmuService;



public class Resident {
	String name,email,tag;
	int phoneNr;
	boolean atHome;
public Resident(String name, String email,int phoneNr,String tag){
	this.name = name;
	this.email = email;
	this.phoneNr = phoneNr;
	this.tag = tag;
	this.atHome = true;
}

public void setName(String name) {
	this.name = name;
}

public void setEmail(String email) {
	this.email = email;
}

public void setTag(String tag) {
	this.tag = tag;
}

public void setPhoneNr(int phoneNr) {
	this.phoneNr = phoneNr;
}
public String getName(){
	return name;
}
public String getEmail(){
	return email;
}
public String getTag(){
	return tag;
}
public int getPhoneNr(){
	return phoneNr;
}
public boolean isAtHome(){
	return atHome;
}
void setAtHome(){
	atHome=!atHome;
}
}
