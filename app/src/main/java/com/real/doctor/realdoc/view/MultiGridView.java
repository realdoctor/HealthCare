package com.real.doctor.realdoc.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.MultiGridAdapter;
import com.real.doctor.realdoc.model.ExpertPostionalBean;
import com.real.doctor.realdoc.model.FilterBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.SortBean;
import com.real.doctor.realdoc.util.OnFilterDoneListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 多个girdView的筛选
 * <p>
 * 本app只做了3个list的适配，若是更多，可根据样式自行修改
 */
public class MultiGridView<FirstBean, SeconBean, ThirdBean> extends LinearLayout {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Context mcontext;
    private MultiGridAdapter multiGridAdapter;
    //总数据
    private FilterBean filterBean;
    //分数据
    private List<HospitalLevelBean> hospital_level_list;//医院等级
    private List<SortBean> sort_list;//排序规则
    private List<ExpertPostionalBean> expert_positional_list;//医生职称

    //返回参数 默认初始值为0
    private HospitalLevelBean hospitalLevelBean;
    private SortBean sortBean;
    private ExpertPostionalBean expertPostionalBean;

    //构造
    public MultiGridView(Context context) {
        this(context, null);
        mcontext = context;
    }

    public MultiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        init(context);
    }

    public MultiGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext = context;
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiGridView(Context context, AttributeSet attrs, int defStyleAttr,
                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mcontext = context;
        init(context);
    }


    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        //布局
        inflate(context, R.layout.act_filter_double_grid, this);
        ButterKnife.bind(this, this);
    }

    //01设置 筛选视图收起 回调
    private OnFilterDoneListener mOnFilterDoneListener;

    public MultiGridView<FirstBean, SeconBean, ThirdBean> setOnFilterDoneListener(OnFilterDoneListener listener) {
        mOnFilterDoneListener = listener;
        return this;
    }

    //02添加筛选回调
    private OnMultiGridViewCallbackListener onMultiGridViewCallbackListener;

    public interface OnMultiGridViewCallbackListener {
        void onSureClickListener(HospitalLevelBean objId, SortBean propertyId, ExpertPostionalBean bedId);
        //        void onResetClickListener(int position);
    }

    public MultiGridView<FirstBean, SeconBean, ThirdBean>
    setOnMultiGridViewClick(OnMultiGridViewCallbackListener onMultiGridViewCallbackListener) {
        this.onMultiGridViewCallbackListener = onMultiGridViewCallbackListener;
        return this;
    }

    //获取总数据
    public MultiGridView<FirstBean, SeconBean, ThirdBean> setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;

        //获取分数据
        hospital_level_list = filterBean.getHospitalLevelBeans();
        sort_list = filterBean.getSortList();
        expert_positional_list = filterBean.getExpertPostionalBeans();
        return this;
    }

    //最终创建视图
    public MultiGridView<FirstBean, SeconBean, ThirdBean> build() {

        FocusGridLayoutManager gridLayoutManager = new FocusGridLayoutManager(this.getContext(), 20);

        //多个gridView合并设置
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                //标题设置
                if (position == 0
                        || position == (hospital_level_list.size() + 1)//机构性质标题位置
                        || position == ((hospital_level_list.size() + 1) + (sort_list.size() + 1))//机构床位标题位置
                        //机构特色标题位置
                        ) {
                    return 20;//

                }
                //第一个布局设置
                if (position > 0 && position < (hospital_level_list.size() + 1)) {//第一个布局5条数据，一行显示设置
                    return 4;
                }
                //其他布局统一设置
                return 5;//
            }
        });

        //初始化adpater
        multiGridAdapter = new MultiGridAdapter(getContext());

        //递默认选中值
        multiGridAdapter.setBed_list(expert_positional_list, 0);
        multiGridAdapter.setObj_list(hospital_level_list, 0);
        multiGridAdapter.setProperty_list(sort_list, 0);

        //关联设置
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(multiGridAdapter);

        //获取回调数据，若一个都不选，值不会获取，只传默认的初始值
        multiGridAdapter.setAdpaterCallback(new MultiGridAdapter.OnAdpaterCallbackListener() {
            @Override
            public void onItemClickListener(HospitalLevelBean hospitalLevelBean, SortBean sortBean, ExpertPostionalBean expertPostionalBean) {
                //MLog.e("MultiGridAdapter的回调结果==", objID, propertyID, bedID, typeID, serviceID);
                MultiGridView.this.hospitalLevelBean = hospitalLevelBean;
                MultiGridView.this.sortBean = sortBean;
                MultiGridView.this.expertPostionalBean = expertPostionalBean;
            }
        });
        return this;
    }

    //设置 选中条件 返回监听

    @OnClick({R.id.bt_confirm, R.id.bt_reset})
    public void clickDone(View view) {
        switch (view.getId()) {
            case R.id.bt_confirm://确定

                if (mOnFilterDoneListener != null) {
                    mOnFilterDoneListener.onFilterDone(1, "", "");//1是数据显示在位置1的tab的上
                }
                //若只点 确定 按钮，则只传初始值给回调
                if (onMultiGridViewCallbackListener != null) {
                    onMultiGridViewCallbackListener.onSureClickListener(hospitalLevelBean, sortBean, expertPostionalBean);
                }
                break;

            case R.id.bt_reset://重置

                if (expert_positional_list.get(0).isSelect()
                        && hospital_level_list.get(0).isSelect()
                        && sort_list.get(0).isSelect()
                        ) {
                    //ToastUtil.ToastShort(mcontext, "已经是重置状态啦！大人");
                } else {

                    //利用遍历修改状态
                    for (ExpertPostionalBean bean : expert_positional_list) {
                        if (bean.isSelect()) {
                            bean.setSelect(false);
                        }
                    }

                    for (SortBean bean : sort_list) {
                        if (bean.isSelect()) {
                            bean.setSelect(false);
                        }
                    }
                    for (HospitalLevelBean bean : hospital_level_list) {
                        if (bean.isSelect()) {
                            bean.setSelect(false);
                        }
                    }



                    //递默认选中值
                    multiGridAdapter.setObj_list(hospital_level_list, 0);
                    multiGridAdapter.setProperty_list(sort_list, 0);
                    multiGridAdapter.setBed_list(expert_positional_list, 0);

                    //回复默认选中值
                    hospitalLevelBean = hospital_level_list.get(0);
                    sortBean = sort_list.get(0);
                    expertPostionalBean = expert_positional_list.get(0) ;

                    //更新adpater
                    multiGridAdapter.notifyDataSetChanged();
                }


                break;
        }


    }


}
