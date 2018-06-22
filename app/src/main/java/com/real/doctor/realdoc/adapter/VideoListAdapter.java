package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.VideoListBean;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.widget.play.ListPlayLogic;

import java.util.List;

/**
 * Created by Taurus on 2018/4/15.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoItemHolder>{

    private Context mContext;
    private List<VideoListBean> mItems;
    private OnListListener onListListener;

    private ListPlayLogic mListPlayLogic;

    private int mScreenUseW;

    public VideoListAdapter(Context context, RecyclerView recyclerView, List<VideoListBean> list){
        this.mContext = context;
        this.mItems = list;
        mScreenUseW = ScreenUtil.getScreenWidth(context) - SizeUtils.dip2px(context, 6*2);
        mListPlayLogic = new ListPlayLogic(context, recyclerView, this);
    }

    public ListPlayLogic getListPlayLogic(){
        return mListPlayLogic;
    }

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    @Override
    public VideoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoItemHolder(View.inflate(mContext, R.layout.item_video, null));
    }

    @Override
    public void onBindViewHolder(final VideoItemHolder holder, final int position) {
        ViewCompat.setElevation(holder.card, SizeUtils.dip2px(mContext, 3));
        updateWH(holder);
        final VideoListBean item = getItem(position);
        if (TextUtils.isEmpty(item.getCover())) {
            GlideUtils.loadImageViewSize(mContext, item.getFilePath(), 1500, 1000,
                    holder.albumImage);
        } else {
            GlideUtils.loadImageViewLoding(mContext, item.getCover(), holder.albumImage,
                    R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        }
        holder.title.setText(item.getFileName());
        holder.layoutContainer.removeAllViews();
        holder.albumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayPosition(position);
                mListPlayLogic.playPosition(position);
            }
        });
    }

    private void updateWH(VideoItemHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.layoutBox.getLayoutParams();
        layoutParams.width = mScreenUseW;
        layoutParams.height = mScreenUseW * 9/16;
        holder.layoutBox.setLayoutParams(layoutParams);
    }

    public void updatePlayPosition(int position){
        mListPlayLogic.updatePlayPosition(position);
    }

    public VideoListBean getItem(int position){
        if(mItems==null)
            return null;
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        if(mItems==null)
            return 0;
        return mItems.size();
    }

    public static class VideoItemHolder extends RecyclerView.ViewHolder{

        View card;
        public FrameLayout layoutContainer;
        public RelativeLayout layoutBox;
        View albumLayout;
        ImageView albumImage;
        TextView title;

        public VideoItemHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            layoutBox = itemView.findViewById(R.id.layBox);
            albumLayout = itemView.findViewById(R.id.album_layout);
            albumImage = itemView.findViewById(R.id.albumImage);
            title = itemView.findViewById(R.id.tv_title);
        }

    }

    public interface OnListListener{
//        void onTitleClick(VideoBean item, int position);
    }

}
