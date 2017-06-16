package com.p1.scmu.home_security_system;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vanessa on 5/23/2017.
 */

public class Member implements Parcelable{

    public String fullName;
    public String email;
    public String rfid;
    public int mobile;
    public String arrivesDepartures;

    public String id; // identify user

    public Member() {
        this.fullName = "";
        this.email = "";
        this.mobile = 0;
        this.rfid = "";
        this.arrivesDepartures="";
    }

    public Member(String fullName, String email, int mobile, String rfid) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.rfid = rfid;
        this.arrivesDepartures="";
    }

    public Member(String fullName, String email, int mobile, String rfid, String dates) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.rfid = rfid;
        this.arrivesDepartures = dates;
    }

    public Member(Parcel in) {

        fullName = in.readString();
        email = in.readString();
        mobile = in.readInt();
        rfid = in.readString();
        arrivesDepartures= in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeInt(mobile);
        parcel.writeString(id);
        parcel.writeString(arrivesDepartures);
    }

    public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void getMemberFromJSON(JSONObject json) throws JSONException {
        if (json == null)
            return;

        fullName = (String)json.get("name");
        email = (String)json.get("email");
        mobile = (int)json.get("number");
        rfid = (String)json.get("rfid");
        arrivesDepartures = (String) json.get("date");
    }

    public void getMemberFromJSON(JsonObject json) throws JSONException {
        if (json == null)
            return;

        fullName = json.get("name").getAsString();
        email = json.get("email").getAsString();
        mobile = json.get("number").getAsInt();
        rfid = json.get("rfid").getAsString();
        arrivesDepartures = json.get("date").getAsString();

    }

    public JSONObject asJSON() throws JSONException {
        return new JSONObject() {{
            put("name", fullName);
            put("email", email);
            put("number", mobile);
            put("rfid", rfid);
            put("date", arrivesDepartures);
        }};
    }
}
