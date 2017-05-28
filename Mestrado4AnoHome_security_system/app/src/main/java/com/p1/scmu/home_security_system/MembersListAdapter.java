package com.p1.scmu.home_security_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vanessa on 5/23/2017.
 */

public class MembersListAdapter extends BaseAdapter {

    private Map<String, Member> mData = new HashMap();
    private Context context;
    public MembersListAdapter(Context context, Map<String, Member> members) {
        this.context = context;
        mData=members;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Member member = (Member) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false);
        }
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText(member.fullName);

        TextView userTime = (TextView) convertView.findViewById(R.id.user_time);
        userTime.setText("");

        ImageView img = (ImageView) convertView.findViewById(R.id.user_img);
        img.setImageResource(R.mipmap.item_user_image);

        return convertView;
    }
}
