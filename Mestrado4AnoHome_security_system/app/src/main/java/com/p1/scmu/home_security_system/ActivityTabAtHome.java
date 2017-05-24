package com.p1.scmu.home_security_system;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vanessa on 5/22/2017.
 */

public class ActivityTabAtHome extends Fragment{

    private MembersListAdapter membersAdapter;
    private View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_at_home, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        ListView listView = (ListView) rootView.findViewById(R.id.list_at_home_users);

        Calendar cl1 = Calendar.getInstance();
        cl1.set(2017, 02, 01, 10, 05);

        Calendar cl2 = Calendar.getInstance();
        cl2.set(2017, 02, 01, 11, 30);
        Map<String, Calendar> datesJOAO = new HashMap<>();
        datesJOAO.put("Arrive",cl1);
        datesJOAO.put("Departure",cl2);
        Member joao = new Member("Joao Miguel", "joao@miguel", 0, "91991", "", datesJOAO);

        Map<String, Calendar> datesMARIA = new HashMap<>();
        datesJOAO.put("Arrive",cl1);
        datesJOAO.put("Departure",cl2);
        Member maria = new Member("Maria Miguel", "maria@miguel", 0, "91991", "", datesMARIA);


        ArrayList<Member> members = new ArrayList<>(Arrays.asList(joao, maria));
        membersAdapter = new MembersListAdapter(rootView.getContext(), members);
        listView.setAdapter(membersAdapter);
    }

}
