package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author zhujiabin
 * @package com.real.doctor.rdsurvey.adapter
 * @fileName ${Name}
 * @Date 2018-2-7 0007
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

abstract class RdBaseAdapter<T> extends BaseAdapter {

    protected final LayoutInflater mInflater;
    protected final List<T> mInfos;
    protected Context mContext;

    public RdBaseAdapter(Context context, List<T> infos) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInfos = infos;
        mContext = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mInfos.size();
    }

    @Override
    public T getItem(int position) {
        // TODO Auto-generated method stub
        return mInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @SuppressWarnings("需要测试")
    public final void releaseAdatper() {
        mInfos.clear();
        notifyDataSetChanged();
        mContext = null;
    }
}
