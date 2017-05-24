package com.p1.scmu.home_security_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vanessa on 5/23/2017.
 */

public class MembersListAdapter extends ArrayAdapter<Member> {

    public MembersListAdapter(Context context, ArrayList<Member> members) { super(context, 0, members);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Member member = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
        }
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText(member.fullName);

        TextView userTime = (TextView) convertView.findViewById(R.id.user_time);
        userTime.setText("");

        ImageView img = (ImageView) convertView.findViewById(R.id.user_img);
        if (member.imageUrl != 0)
            img.setImageResource((int)member.imageUrl);
        else
            img.setImageResource(R.mipmap.item_user_image);

        /*ViewGroup.LayoutParams lp = convertView.getLayoutParams();
        lp.height= 250;*/

        return convertView;
    }
}
