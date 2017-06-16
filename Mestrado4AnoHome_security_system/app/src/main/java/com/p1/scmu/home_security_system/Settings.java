package com.p1.scmu.home_security_system;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vanessa on 5/28/2017.
 */

public class Settings implements Parcelable {

    private boolean silentMode;
    private boolean alarmMode;
    private String authenticate;

    public Settings(){
        this.silentMode=false;
        this.alarmMode=false;
        this.authenticate = "";
    }

    public Settings(String authenticate){
        this.silentMode=false;
        this.alarmMode=false;
        this.authenticate = authenticate;
    }
    
    public Settings(boolean silentMode, boolean alarmMode, String authenticate){
        this.silentMode=silentMode;
        this.alarmMode=alarmMode;
        this.authenticate = authenticate;
    }

    protected Settings(Parcel in) {
        silentMode = in.readByte() != 0;
        alarmMode = in.readByte() != 0;
        authenticate = in.readString();
    }

    public boolean getSilentMode(){
        return silentMode;
    }
    public boolean getAlarmMode(){
        return alarmMode;
    }
    public String getAuthTag(){
        return authenticate;
    }
    public void setSilentMode(boolean mode){
        silentMode=mode;
    }
    public void setAlarmMode(boolean mode){alarmMode=mode; }
    public void setAuthTag(String tag){ authenticate=tag;}

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    public void getSettingsFromJSON(JSONObject json) throws JSONException {
        if (json == null)
            return;

        silentMode = (boolean)json.get("SilentMode");
        alarmMode = (boolean)json.get("AlarmMode");
        authenticate = (String)json.get("Authenticate");
    }

    public void getSettingsFromJSON(JsonObject json) throws JSONException {
        if (json == null)
            return;

        silentMode = json.get("SilentMode").getAsBoolean();
        alarmMode = json.get("AlarmMode").getAsBoolean();
        authenticate = json.get("Authenticate").getAsString();
    }

    public JSONObject asJSON() throws JSONException {
        return new JSONObject() {{
            put("SilentMode", silentMode);
            put("AlarmMode", alarmMode);
            put("Authenticate", authenticate);

        }};
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        boolean[] modes = {silentMode, alarmMode};
        parcel.writeBooleanArray(modes);
        parcel.writeString(authenticate);
    }

    public String toString(){
        return getSilentMode() + " " + getAlarmMode() + " " + getAuthTag();
    }
}
