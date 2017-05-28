package com.p1.scmu.home_security_system;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vanessa on 5/27/2017.
 */

public class LocalService extends Service {

    private static final String URL = "http://localhost:8000/";
    private static final String GET_MEMBERSI = "";
    private static final String GET_MEMBERSIO = "";
    private static final String GET_MEMBERS = "";
    private static final String INSERT_MEMBER = "";
    private static final String DELETE_MEMBER = "";
    private static final String URL_REQUEST_MEMBERS_LIST =URL+GET_MEMBERSI;
    private static final String URL_REQUEST_MEMBERS_VI =URL+GET_MEMBERSIO;
    private static final String URL_REQUEST_MEMBERS =URL+ GET_MEMBERS; //POST OU DELETE...
    private static final String URL_REQUEST_INSERT_MEMBERS =URL+INSERT_MEMBER; //POST OU DELETE...
    private static final String URL_REQUEST_DELETE_MEMBERS =URL+DELETE_MEMBER; //POST OU DELETE...

    private static final String CHANNEL_MEMBERSIO = "channel_membersIO";
    private static final String CHANNEL_MEMBERSI = "channel_membersI0";
    private static final String CHANNEL_MEMBERS = "channel_members";

    // just some arbitrary numbers for test purposes
    public static int statusCode = 99;
    private final static String TAG = LocalService.class.getSimpleName();
    public boolean isRunning=false;
    private boolean intenetAccess=false;

    private RequestQueue reQueue;
    private List<Member> allMembers;
    private List<Member> membersI;
    private List<Member> membersIO;
    PNConfiguration pnConfiguration;
    PubNub pubnub;
    PubSubPnCallback pubSubPnCallback;

    public void initPubNub(){
        pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("SubscribeKey");
        pnConfiguration.setPublishKey("PublishKey");
        pnConfiguration.setSecure(false);

        pubnub = new PubNub(pnConfiguration);

        pubnub.addListener(this.pubSubPnCallback);
        pubnub.subscribe()
                .channels(Arrays.asList(CHANNEL_MEMBERS, CHANNEL_MEMBERSI, CHANNEL_MEMBERSIO)) // subscribe to channels
                .execute();

    }

    public class PubSubPnCallback extends SubscribeCallback {
        @Override
        public void status(PubNub pubnub, PNStatus status) {
            // for common cases to handle, see: https://www.pubnub.com/docs/java/pubnub-java-sdk-v4
        }
        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            manageMessages(message.getMessage(), message.getChannel());
        }
        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            // no presence handling for simplicity
        }
    }

    private void manageMessages(JsonElement message, String channel) {

            try {
                JsonArray jsonArray = message.getAsJsonArray();
                for(int i=0;i<jsonArray.size();i++){
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    Member member = new Member();
                    member.getMemberFromJSON(jsonObject);

                    if(channel.equals(CHANNEL_MEMBERS)) {
                        allMembers.add(member);
                    }else if(channel.equals(CHANNEL_MEMBERSI)){
                        membersI.add(member);
                    }else if(channel.equals(CHANNEL_MEMBERSIO)){
                        membersIO.add(member);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void SendRequest()
    {
        reQueue = Volley.newRequestQueue(this);
        allMembers = sendRequestToMembersList(URL_REQUEST_MEMBERS);
        membersI = sendRequestToMembersList(URL_REQUEST_MEMBERS_LIST);
        membersIO = sendRequestToMembersList(URL_REQUEST_MEMBERS_VI);
    }

    private List<Member> sendRequestToMembersList(String url){

        final List<Member> members = new ArrayList<>();
        reQueue.add(

                new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("TAG", response.toString());
                                try {
                                    JSONArray jsonArray = response;
                                    for(int i=0;i<jsonArray.length();i++){
                                        Member member = new Member();
                                        member.getMemberFromJSON(jsonArray.getJSONObject(i));
                                        members.add(member);
                                    }
                                } catch (JSONException e) {
                                    Log.e("JsonArray", "JsonArray members invalid");
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                    }
                })
        );
        return members;
    }

    public List<Member> getMembers(){ return allMembers; }

    public List<Member> getMembersAtHome(){
        return membersI;
    }

    public List<Member> getAllMembersHistory(){
        return membersIO;
    }

    //may be POST or DELETE
    public void sendResponse(Member m, int method, String path) {
        try {

            JSONObject jsonBody = m.asJSON();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                    method, new String(URL+path), jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());

                            //msgResponse.setText(response.toString());

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                }
            });
        } catch (JSONException e) {
            Log.e("sendResponse", "Error parsing to json");
            e.printStackTrace();
        }
    }

        @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate");

        isRunning=true;

        initPubNub();

        Cache cache = new DiskBasedCache(getCacheDir(), 1024*10240);
        Network network = new BasicNetwork(new HurlStack());
        reQueue = new RequestQueue(cache, network);

    }

    public interface ILocalBinder { public int getStatusCode();  }

    public class LocalBinder extends Binder implements ILocalBinder  {
        LocalService getService() {
            return LocalService.this;
        }

        @Override
        public int getStatusCode() {
            return LocalService.statusCode;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service onBind");
        return mBinder;
    }

    public final IBinder mBinder = new LocalBinder();

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "Service onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service onUnBind");
        return true;
    }

    @Override
    public void onDestroy() {

        isRunning=false;

        Log.i(TAG, "Service onDestroy");
        super.onDestroy();
    }

}

