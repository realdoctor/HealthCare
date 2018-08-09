package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.SearchInfoBean;

import java.util.List;

public class InfoSearchAdapter extends RdBaseAdapter<SearchInfoBean> {

    public InfoSearchAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchInfoBean bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.search_list_item, parent, false);
            holder.tv_show = (TextView) convertView.findViewById(R.id.search_text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_show.setText(bean.getValue());
        return convertView;
    }

    public class Holder {
        public TextView tv_show;
    }
}
