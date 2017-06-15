package com.p1.scmu.home_security_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Vanessa on 5/22/2017.
 */

public class ActivityTabAtHome extends Fragment{


    private final int request_code_update_member = 5;

    private MembersListAdapter membersAdapter;
    private View rootView;
    private ActivityMainMenu activityMainMenu;
    private Member toUpdate;
    private ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_at_home, container, false);
        activityMainMenu = (ActivityMainMenu) getActivity();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = (ListView) rootView.findViewById(R.id.list_at_home_users);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(activityMainMenu.serviceStarted) {
            activityMainMenu.refreshMembersLists();

            List<Member> members;
            if (activityMainMenu.memberList != null) members = activityMainMenu.memberList;
            else members = new ArrayList<>();

            membersAdapter = new MembersListAdapter(rootView.getContext(), members);
            listView.setAdapter(membersAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                    Member member = (Member) adapterView.getItemAtPosition(position);
                    toUpdate = member;
                    startMemberSettingsActivity(member);
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ActivityRFIDReader", "onActivity");
        if(requestCode==request_code_update_member){
            if(resultCode == RESULT_OK) {
                Log.i("ActivityAddUser", "guardando");
                Member updated = (Member) data.getExtras().get(ActivityUserSettings.UPDATE_MEMBER);
                activityMainMenu.memberList.remove(toUpdate.rfid);
                toUpdate = null;
                activityMainMenu.memberList.add(updated);
                activityMainMenu.sendToInsertMemberToServer(updated);
            }
        }
    }

    private void startMemberSettingsActivity(Member member) {
        Intent intent = new Intent(rootView.getContext(), ActivityUserSettings.class);
        intent.putExtra("Member", member);
        startActivityForResult(intent, request_code_update_member);
    }

}
