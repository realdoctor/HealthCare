package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;


public class QueryItemAdapter extends RdBaseAdapter<String>{
    public QueryItemAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder.tv_show = (TextView) convertView.findViewById(android.R.id.text1 );
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_show.setText(bean);

        return convertView;
    }



    public class Holder {
       public TextView tv_show;
    }


}
