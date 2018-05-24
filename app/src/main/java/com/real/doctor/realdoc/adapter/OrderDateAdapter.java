package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.WeekModel;
import java.util.List;


public class OrderDateAdapter extends RdBaseAdapter<WeekModel> {
    private int selectedPosition = -1;
    public OrderDateAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeekModel bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.week_item, parent, false);
            holder.ll_bg=(LinearLayout)convertView.findViewById(R.id.ll_bg);
            holder.week=(TextView) convertView.findViewById(R.id.tv_week);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        // 设置选中效果
        if(selectedPosition == position)
        {
            holder.ll_bg.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        } else {
            holder.ll_bg.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
        holder.week.setText(bean.worktimeWeek.replace("|","\n"));
        return convertView;
    }

    public class Holder {
       public TextView week;
       public LinearLayout ll_bg;
    }
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
}
