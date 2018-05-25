package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.real.doctor.realdoc.model.ExpertPostionalBean;
import com.real.doctor.realdoc.model.FilterBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.SortBean;
import com.real.doctor.realdoc.util.DpUtils;
import com.real.doctor.realdoc.util.FilterUtils;
import com.real.doctor.realdoc.util.OnFilterDoneListener;
import com.real.doctor.realdoc.view.FilterCheckedTextView;
import com.real.doctor.realdoc.view.MultiGridView;
import com.real.doctor.realdoc.view.SingleListView;

/**
 * 筛选 适配
 */

public class DropMenuAdapterForResult implements MenuAdapter {
    private final Context mContext;
    private OnFilterDoneListener onFilterDoneListener;
    private String[] titles;//筛选 标题

    private FilterBean filterBean;//筛选 总数据源


    public DropMenuAdapterForResult(Context context, String[] titles, OnFilterDoneListener onFilterDoneListener) {
        this.mContext = context;
        this.titles = titles;
        this.onFilterDoneListener = onFilterDoneListener;
    }

    @Override
    public int getMenuCount() {
        return titles.length;
    }

    @Override
    public String getMenuTitle(int position) {
        return titles[position];
    }

    @Override
    public int getBottomMargin(int position) {
        if (position == 3) {
            return 0;
        }
        return DpUtils.dpToPx(mContext, 140);
    }

    @Override
    public View getView(int position, FrameLayout parentContainer) {
        View view = parentContainer.getChildAt(position);

        switch (position) {
            case 0://排序
                view = createSingleListView();
                break;
            case 1://医院等级
                view = createMultiView();
                break;

        }

        return view;
    }

    /**
     * 适配传值
     */
    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
    }


    /**
     * 回调监听
     */
    //医院等级筛选
    public OnLevelCallbackListener onLevelCallbackListener;

    public interface OnLevelCallbackListener {
        void onLevelCallbackListener(HospitalLevelBean item);
    }

    public void setOnLevelCallbackListener(OnLevelCallbackListener onLevelCallbackListener) {
        this.onLevelCallbackListener = onLevelCallbackListener;
    }
    //排序筛选
    public OnSortCallbackListener onSortCallbackListener;

    public interface OnSortCallbackListener {
        void onSortCallbackListener(SortBean item);
    }

    public void setOnSortCallbackListener(OnSortCallbackListener onSortCallbackListener) {
        this.onSortCallbackListener = onSortCallbackListener;
    }

    //排序筛选
    public OnMultiCallbackListener onMultiCallbackListener;

    public interface OnMultiCallbackListener {
        void onMultiCallbackListener(HospitalLevelBean bean,SortBean bean2,ExpertPostionalBean bean3);
    }

    public void setOnMultiCallbackListener(OnMultiCallbackListener onMultiCallbackListener) {
        this.onMultiCallbackListener = onMultiCallbackListener;
    }
    private View createSingleListView() {

        SingleListView<SortBean> singleListView = new SingleListView<SortBean>(mContext)
                .setSingleListAdapter(new SimpleTextAdapter<SortBean>(null, mContext) {
                    @Override
                    public String provideText(SortBean bean) {
                        return bean.sortName;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        int dp = DpUtils.dpToPx(mContext, 15);
                        checkedTextView.setPadding(dp, dp, 0, dp);
                    }
                })
                .onSingleListClick(new SingleListView.OnSingleListClickListener<SortBean>() {
                    @Override
                    public void onSingleListCallback(SortBean item) {
                        FilterUtils.instance().sortTitle=item.sortName;
                        onSortCallbackListener.onSortCallbackListener(item);//回调到act中操作
                        onFilterDone(0);
                    }
                });

        //设置默认选项
        singleListView.setList(filterBean.getSortList(), -1);
        return singleListView;
    }
    private View createSingleListLevelView() {

        SingleListView<HospitalLevelBean> singleListView = new SingleListView<HospitalLevelBean>(mContext)
                .setSingleListAdapter(new SimpleTextAdapter<HospitalLevelBean>(null, mContext) {
                    @Override
                    public String provideText(HospitalLevelBean bean) {
                        return bean.LevelName;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        int dp = DpUtils.dpToPx(mContext, 15);
                        checkedTextView.setPadding(dp, dp, 0, dp);
                    }
                })
                .onSingleListClick(new SingleListView.OnSingleListClickListener<HospitalLevelBean>() {
                    @Override
                    public void onSingleListCallback(HospitalLevelBean item) {
                        FilterUtils.instance().HosptialLevelTitle=item.LevelName;
                        onLevelCallbackListener.onLevelCallbackListener(item);//回调到act中操作
                        onFilterDone(1);
                    }
                });

        //设置默认选项
        singleListView.setList(filterBean.getHospitalLevelBeans(), -1);
        return singleListView;
    }
    private View createMultiView() {

//        SingleListView<HospitalLevelBean> singleListView = new SingleListView<HospitalLevelBean>(mContext)
//                .setSingleListAdapter(new SimpleTextAdapter<HospitalLevelBean>(null, mContext) {
//                    @Override
//                    public String provideText(HospitalLevelBean bean) {
//                        return bean.LevelName;
//                    }
//
//                    @Override
//                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
//                        int dp = DpUtils.dpToPx(mContext, 15);
//                        checkedTextView.setPadding(dp, dp, 0, dp);
//                    }
//                })
//                .onSingleListClick(new SingleListView.OnSingleListClickListener<HospitalLevelBean>() {
//                    @Override
//                    public void onSingleListCallback(HospitalLevelBean item) {
//                        FilterUtils.instance().HosptialLevelTitle=item.LevelName;
//                        onLevelCallbackListener.onLevelCallbackListener(item);//回调到act中操作
//                        onFilterDone(1);
//                    }
//                });
//
//        singleListView.setList(filterBean.getHospitalLevelBeans(), -1);
//        return singleListView;
        MultiGridView<HospitalLevelBean,SortBean,ExpertPostionalBean> multiGridView=new MultiGridView<HospitalLevelBean, SortBean, ExpertPostionalBean>(mContext).setOnMultiGridViewClick(new MultiGridView.OnMultiGridViewCallbackListener() {
            @Override
            public void onSureClickListener(HospitalLevelBean objId, SortBean propertyId, ExpertPostionalBean bedId) {

                        FilterUtils.instance().multiGirdOne=objId;
                        FilterUtils.instance().multiGirdTwo=propertyId;
                        FilterUtils.instance().multiGirdThree=bedId;
                        onMultiCallbackListener.onMultiCallbackListener(objId,propertyId,bedId);//回调到act中操作
                        onFilterDone(1);
            }
        });
        multiGridView.setFilterBean(filterBean);
        return multiGridView.build();
    }


    /**
     * 每一块筛选按钮的act回调
     *
     * @param tabPosition
     */
    private void onFilterDone(int tabPosition) {
        if (onFilterDoneListener != null) {
            if (tabPosition == 0) {
                onFilterDoneListener.onFilterDone(0, FilterUtils.instance().sortTitle, "");
            } else if (tabPosition == 1) {
                onFilterDoneListener.onFilterDone(1, "筛选", "");
            }else {
                onFilterDoneListener.onFilterDone(tabPosition, "代码错误", "");
            }
        } else {
        }
    }
}
