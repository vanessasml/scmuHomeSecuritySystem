package com.scmu.ws.rest.scmuService;

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
    
    public Settings(boolean silentMode, boolean alarmMode, String authenticate){
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

    
}
