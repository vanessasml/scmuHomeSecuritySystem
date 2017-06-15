package com.p1.scmu.home_security_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vanessa on 5/23/2017.
 */

public class MembersListAdapter extends ArrayAdapter<Member> {

    private final int request_code_update_member = 1;

    public MembersListAdapter(Context context, List<Member> members) { super(context, 0, members); }

       @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Member member = (Member) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
        }
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText(member.fullName);

        TextView userTime = (TextView) convertView.findViewById(R.id.user_time);
        userTime.setText("");

        ImageView img = (ImageView) convertView.findViewById(R.id.user_img);
        img.setImageResource(R.mipmap.item_user_image);

        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.imageButton);

        return convertView;
    }

}
