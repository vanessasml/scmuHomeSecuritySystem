
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
