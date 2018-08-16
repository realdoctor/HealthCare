package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.DeptBean;

import java.util.List;


public class RightAdapter extends RdBaseAdapter<DeptBean> {

    public RightAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeptBean bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.dept_item_layout, parent, false);
            holder.deptName = convertView.findViewById(R.id.tv_show);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.deptName.setText(bean.deptName);
        return convertView;
    }

    public class Holder {
        private TextView deptName;
    }

}
