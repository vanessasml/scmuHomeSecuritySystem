package com.scmu.ws.rest.scmuService;

import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

public class Settings {

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
    
    public Settings(@JsonProperty("SilentMode")boolean silentMode, @JsonProperty("AlarmMode")boolean alarmMode, @JsonProperty("Authenticate")String authenticate){
        this.silentMode=silentMode;
        this.alarmMode=alarmMode;
        this.authenticate = authenticate;
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
    public void getPassageFromJSON(JSONObject json) throws JSONException {
        if (json == null)
            return;

        silentMode = (Boolean)json.get("SilentMode");
        alarmMode = (Boolean)json.get("AlarmMode");
        authenticate= (String)json.get("Authenticate");
       

    }
    
}
