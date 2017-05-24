package com.p1.scmu.home_security_system;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vanessa on 5/23/2017.
 */

public class Member implements Parcelable{

    public String fullName;
    public String email;
    public long imageUrl;
    public String mobile;
    public Map<String, Calendar> arrivesDepartures;

    public String id; // identify user

    public Member() { }

    public Member(String fullName, String email, long imageUrl, String mobile, String id) {
        this.fullName = fullName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.mobile = mobile;
        this.id = id;
    }

    public Member(String fullName, String email, long imageUrl, String mobile, String id, Map<String, Calendar> dates) {
        this.fullName = fullName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.mobile = mobile;
        this.id = id;
        this.arrivesDepartures = dates;
    }

    protected Member(Parcel in) {
        id = in.readString();
        fullName = in.readString();
        email = in.readString();
        imageUrl = in.readLong();
        mobile = in.readString();

        Map<String, Calendar> map = new HashMap<>();
        in.readMap(map, Map.class.getClassLoader());
        if (map.size() > 0)
            arrivesDepartures = new HashMap<>(map);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeLong(imageUrl);
        parcel.writeString(mobile);

        if (arrivesDepartures != null) {
            Map<String, Calendar> map = new HashMap<>();
            for(Map.Entry<String, Calendar> entry : map.entrySet()) {
                String key = entry.getKey();
                Calendar value = entry.getValue();

                arrivesDepartures.put(key, value);
            }
            parcel.writeMap(map);
        } else {
            parcel.writeMap(null);
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

    public void upCalendarFromJSON(Map<String, Object> json) {
        if (json == null)
            return;

        fullName = (String)json.get("full_name");
        email = (String)json.get("email");
        imageUrl = (Long)json.get("image_url");
        mobile = (String)json.get("mobile");
        arrivesDepartures = (Map<String, Calendar>)json.get("arrivesDepartures");
    }

    public Map<String, Object> asJSON() {
        return new HashMap<String, Object>() {{
            put("full_name", fullName);
            put("email", email);
            put("image_url", imageUrl);
            put("mobile", mobile);
            put("goals", arrivesDepartures);
        }};
    }
}
