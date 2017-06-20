package com.scmu.ws.rest.scmuService;

import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Resident {
	String name,email,tag;
	int phoneNr;
	boolean atHome;
	String date;
public  Resident(){
	
}

public Resident(@JsonProperty("name")String name, @JsonProperty("email")String email,@JsonProperty("number")int phoneNr,@JsonProperty("rfid")String tag){
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
public String getDate() {
	return date;
}

public void setDate(String date) {
	this.date = date;
}
public void getMemberFromJSON(JSONObject json) throws JSONException {
    if (json == null)
        return;

    name = (String)json.get("name");
    email = (String)json.get("email");
    phoneNr = (int)json.get("number");
    tag= (String)json.get("rfid");
    date = json.getString("date");
        
    

}
public JSONObject asJSON() throws JSONException {
    return new JSONObject() {{
        put("name", name);
        put("email", email);
        put("number", phoneNr);
        put("rfid", tag);
        put("date", date);
    }};
}
}
