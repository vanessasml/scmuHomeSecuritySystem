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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vanessa on 5/27/2017.
 */

public class LocalService extends Service {


    // just some arbitrary numbers for test purposes
    public static int statusCode = 99;
    private final static String TAG = LocalService.class.getSimpleName();
    public boolean isRunning=false;
    private boolean intenetAccess=false;

    private RequestQueue reQueue;
    private static final String URL_REQUEST_MEMBERS_LIST ="http://1/1";
    private static final String URL_REQUEST_MEMBERS_VI ="http://1/2";
    private static final String URL_SEND_NEW_MEMBER ="http://1/1/1"; //POST OU DELETE...
    private List<Member> membersI;
    private List<Member> membersIO;

    public void SendRequest()
    {
        reQueue = Volley.newRequestQueue(this);
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
                                    JSONArray jsonArray = new JSONArray(response);
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

    public List<Member> getMembersAtHome(){
        return membersI;
    }

    public List<Member> getAllMembersHistory(){
        return membersIO;
    }

    //may be POST or DELETE
    public void sendResponse(Member m, int method, String url) {
        try {

            JSONObject jsonBody = m.asJSON();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                    method, url, jsonBody,
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

