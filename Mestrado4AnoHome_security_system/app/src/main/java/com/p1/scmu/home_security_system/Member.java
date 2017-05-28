package com.p1.scmu.home_security_system;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vanessa on 5/23/2017.
 */

public class Member implements Parcelable{

    public String fullName;
    public String email;
    public String rfid;
    public int mobile;
    public List<String> arrivesDepartures;

    public String id; // identify user

    public Member() { }

    public Member(String fullName, String email, int mobile, String rfid) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.rfid = rfid;
    }

    public Member(String fullName, String email, int mobile, String rfid, List<String> dates) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.rfid = rfid;
        this.arrivesDepartures = dates;
    }

    protected Member(Parcel in) {

        fullName = in.readString();
        email = in.readString();
        mobile = in.readInt();
        rfid = in.readString();

        List<String> dates  = new ArrayList<>();
        in.readList(dates, List.class.getClassLoader());
        if (dates.size() > 0)
            arrivesDepartures = new ArrayList<>(dates);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeInt(mobile);
        parcel.writeString(id);

        if (arrivesDepartures != null) {
            List<String> dates = new ArrayList<>();
            for(String entry : dates) arrivesDepartures.add(entry);

            parcel.writeList(arrivesDepartures);
        } else {
            parcel.writeList(null);
        }
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

        fullName = (String)json.get("Name");
        email = (String)json.get("Email");
        mobile = (int)json.get("Number");
        rfid = (String)json.get("RFID");

        JSONArray jArrayDates = json.getJSONArray("Dates");
        for (int j = 0; j < json.length(); j++) {
            try {
                arrivesDepartures.add(jArrayDates.get(j).toString());
            } catch (JSONException e) {
                Log.e("JsonArray", "JsonArray dates invalid");
                e.printStackTrace(); }
        }

    }

    public JSONObject asJSON() throws JSONException {
        return new JSONObject() {{
            put("Name", fullName);
            put("Email", email);
            put("Number", mobile);
            put("RFID", rfid);
            put("Dates", new JSONArray(arrivesDepartures));
        }};
    }
}
