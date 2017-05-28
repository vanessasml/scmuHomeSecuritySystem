
public class Passage {
String date,tag;
boolean isEntrance;
boolean isAuthenticated;
public Passage(String date,String tag,boolean isEntrance,boolean isAuthenticated){
	this.date = date;
	this.tag = tag;
	this.isEntrance = isEntrance;
	this.isAuthenticated = isAuthenticated;
}
public String getDate(){
	return date;
}
public String getTag(){
	return tag;
}
public boolean isEntrance(){
	return isEntrance;
}
public boolean isAuthenticated(){
	return isAuthenticated;
}
}
