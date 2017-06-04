package com.p1.scmu.home_security_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Vanessa on 5/22/2017.
 */

public class ActivityTabAtHome extends Fragment{

    private MembersListAdapter membersAdapter;
    private View rootView;
    private ActivityMainMenu activityMainMenu;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_at_home, container, false);
        activityMainMenu = (ActivityMainMenu) getActivity();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
try {
    ListView listView = (ListView) rootView.findViewById(R.id.list_at_home_users);

    activityMainMenu.refreshMembersLists();
    membersAdapter = new MembersListAdapter(rootView.getContext(), activityMainMenu.memberListI);
    listView.setAdapter(membersAdapter);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView adapterView, View view, int position, long l) {
            Member member = (Member) adapterView.getItemAtPosition(position);
            startMemberSettingsActivity(member);
        }
    });
}catch(Exception e){}
    }

    private void startMemberSettingsActivity(Member member) {
        Intent intent = new Intent(rootView.getContext(), ActivityUserSettings.class);
        intent.putExtra("Member", member);
        startActivityForResult(intent, 1);
    }

}
