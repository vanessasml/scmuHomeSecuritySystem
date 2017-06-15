package com.p1.scmu.home_security_system;

/**
 * Created by Vanessa on 5/22/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import static android.app.Activity.RESULT_OK;

public class ActivityTabMembers extends Fragment{

    private static final int request_code_add_member = 2;
    private static final int request_code_update_member = 3;

    private MembersListAdapter membersAdapter;
    private View rootView;
    private ImageButton userSettingsButton;
    private ActivityMainMenu activityMainMenu;
    private Member toUpdate;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_members, container, false);
        activityMainMenu = (ActivityMainMenu) getActivity();

        FloatingActionButton btn_submit = (FloatingActionButton) rootView.findViewById(R.id.button_add_user);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityAddUser.class);
                startActivityForResult(intent, request_code_add_member);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){

            super.onActivityCreated(savedInstanceState);

            ListView listView = (ListView) rootView.findViewById(R.id.list_view_members);
            System.out.println("Main menu "+activityMainMenu);
     //       activityMainMenu.refreshMembersLists();
        //    membersAdapter = new MembersListAdapter(rootView.getContext(), activityMainMenu.memberList);
         //   listView.setAdapter(membersAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                    Member member = (Member) adapterView.getItemAtPosition(position);
                    toUpdate = member;
                    startMemberSettingsActivity(member);
                }
            });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ActivityRFIDReader", "onActivity");
        if (requestCode == request_code_add_member){
            if(resultCode == RESULT_OK) {
                Log.i("ActivityAddUser", "guardando");
                Member member = (Member) data.getExtras().get(ActivityAddUser.MEMBER);
                activityMainMenu.memberList.add(member);
                activityMainMenu.sendToInsertMemberToServer(member);
            }
        }else if(requestCode==request_code_update_member){
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
