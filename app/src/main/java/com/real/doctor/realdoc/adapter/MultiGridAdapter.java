package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.real.doctor.realdoc.model.ExpertPostionalBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.SortBean;
import com.real.doctor.realdoc.util.FilterUtils;
import com.real.doctor.realdoc.view.grid_holder.MultiItemHolder;
import com.real.doctor.realdoc.view.grid_holder.TitleViewHolder;

import java.util.List;

/**
 * 多个gridView的自定义控件
 */
public class MultiGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TITLE = 0;//设置title的地方
    private static final int TYPE_ITEM = 1;//设置item的地方

    private Context mContext;
    private LayoutInflater inflater;

    //数据
    private List<HospitalLevelBean> hospitalLevelBeans;//收住对象
    private List<SortBean> sortBeans;//机构性质
    private List<ExpertPostionalBean> expertPostionalBeans;//机构性质

    //返回参数 重置时，该处的值也要清零
    private HospitalLevelBean hospitalLevelBean;
    private SortBean sortBean;
    private ExpertPostionalBean expertPostionalBean;

    //默认 选中的数值 标记
    private int default_obj_position;
    private int default_property_position;
    private int default_bed_position;

    public MultiGridAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    //set值
    public void setHospitalLevelBeans(List<HospitalLevelBean> hospitalLevelBeans) {
        this.hospitalLevelBeans = hospitalLevelBeans;
    }

    public void setSortBeans(List<SortBean> sortBeans) {
        this.sortBeans = sortBeans;
    }

    public void setExpertPostionalBeans(List<ExpertPostionalBean> expertPostionalBeans) {
        this.expertPostionalBeans = expertPostionalBeans;
    }



    /**
     * set值 带默认初始化值的赋值
     * <p>
     * 由于recyclerView的item复用问题，导致item状态混乱
     * <p>
     * 本app的默认选中 (1)以修改数据源参数状态，(2)设置额外标记，已达到选中要求
     * 其实这里只做标记就能避免item混乱，但是筛选器有个重置功能，所以做了修改数据源状态处理
     */

    public void setObj_list(List<HospitalLevelBean> hospitalLevelBeans, int default_objId) {
        this.hospitalLevelBeans = hospitalLevelBeans;
        this.default_obj_position = default_objId;
        hospitalLevelBeans.get(default_objId).setSelect(true);
    }

    public void setProperty_list(List<SortBean> sortBeans, int default_propertyId) {
        this.sortBeans = sortBeans;
        this.default_property_position = default_propertyId;
        sortBeans.get(default_propertyId).setSelect(true);
    }

    public void setBed_list(List<ExpertPostionalBean> expertPostionalBeans, int default_bedId) {
        this.expertPostionalBeans = expertPostionalBeans;
        this.default_bed_position = default_bedId;
        expertPostionalBeans.get(default_bedId).setSelect(true);
    }


    /**
     * 设置 适配的回调
     */

    //02添加筛选回调
    private OnAdpaterCallbackListener onAdpaterCallbackListener;

    public interface OnAdpaterCallbackListener {
        void onItemClickListener(HospitalLevelBean hospitalLevelBean, SortBean sortBean, ExpertPostionalBean expertPostionalBean);
    }

    public void setAdpaterCallback(OnAdpaterCallbackListener onAdpaterCallbackListener) {
        this.onAdpaterCallbackListener = onAdpaterCallbackListener;
    }

    /**
     * 获取类型标记
     * <p>
     * 这里用了两种布局，标题位置一种布局，数据显示一种布局
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0//收住对象的标题位置
                || position == (hospitalLevelBeans.size() + 1)//机构性质标题位置
                || position == ((hospitalLevelBeans.size() + 1) + (sortBeans.size() + 1))//机构床位标题位置
                ) {
            return TYPE_TITLE;
        }
        return TYPE_ITEM;//显示数据的标记
    }

    /**
     * 根据标记 创建不同的布局
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

        switch (viewType) {
            case TYPE_TITLE:
                holder = new TitleViewHolder(mContext, parent);
                break;
            case TYPE_ITEM:
                holder = new MultiItemHolder(mContext, parent);
                break;
        }
        return holder;

    }

    /**
     * 该处是recyclerView的item复用的地方，注意保存状态
     * <p>
     * 该处有默认选中的item,也有手动选中item，如果冲突，优先选手动的结果
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);

        switch (itemViewType) {
            case TYPE_TITLE:// 设置标题
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                if (position == 0) {
                    titleViewHolder.bind("医院等级");
                } else if (position == (hospitalLevelBeans.size() + 1)) {
                    titleViewHolder.bind("排序属性");
                } else if (position == ((hospitalLevelBeans.size() + 1) + (sortBeans.size() + 1))) {
                    titleViewHolder.bind("医生职称");
                }

                break;

            case TYPE_ITEM:

                MultiItemHolder itemViewHolder = (MultiItemHolder) holder;

                if (position > 0
                        && position < hospitalLevelBeans.size() + 1) {

                    //数据的位置 0-list.size
                    int current_firstPosition = position - 1;
                    //obj_list的数据
                    HospitalLevelBean firstBean = hospitalLevelBeans.get(position - 1);
                    //绑定数据
                    itemViewHolder.bind(hospitalLevelBeans.get(current_firstPosition).LevelName, firstBean);


                    //绑定选中，手动选中优先
                    if (firstBean.isSelect()) {
                        itemViewHolder.textView.setSelected(true);
                        //由于我的筛选，每次点击都会new，所以上一个标记很容易被GC，所以再new出来的时候，把之前状态保留只有在这里设置
                        multiSelectOne = itemViewHolder.textView;
                        seletpositionOne = position;
                    } else {
                        //绑定默认选中
                        if (current_firstPosition == default_obj_position) {
                            itemViewHolder.textView.setSelected(true);
                            multiSelectOne = itemViewHolder.textView;
                            seletpositionOne = position;
                        } else {
                            itemViewHolder.textView.setSelected(false);
                        }
                    }

                    //绑定状态
                    onItemClick(holder, position);

                } else if (position > hospitalLevelBeans.size() + 1
                        && position < ((hospitalLevelBeans.size() + 1) + (sortBeans.size() + 1))) {

                    //数据的位置 0-list.size
                    int current_secondPosition = position - hospitalLevelBeans.size() - 2;
                    //property_list的数据
                    SortBean secondBean = sortBeans.get(position - hospitalLevelBeans.size() - 2);
                    //绑定数据
                    itemViewHolder.bind(sortBeans.get(position - hospitalLevelBeans.size() - 2).sortName, secondBean);

                    //绑定选中，手动选中优先
                    if (secondBean.isSelect()) {
                        itemViewHolder.textView.setSelected(true);
                        multiSelectTwo = itemViewHolder.textView;
                        seletpositionTwo = position;
                    } else {
                        //绑定默认选中
                        if (current_secondPosition == default_property_position) {
                            itemViewHolder.textView.setSelected(true);
                            multiSelectTwo = itemViewHolder.textView;
                            seletpositionTwo = position;
                        } else {
                            itemViewHolder.textView.setSelected(false);
                        }
                    }

                    //绑定状态
                    onItemClick(holder, position);

                } else if (position > ((hospitalLevelBeans.size() + 1) + (sortBeans.size() + 1))
                        && position < ((hospitalLevelBeans.size() + 1) + (sortBeans.size() + 1) + (expertPostionalBeans.size() + 1))) {

                    //数据的位置 0-list.size
                    int current_thirdPosition = position - hospitalLevelBeans.size() - sortBeans.size() - 3;
                    //bed_list数据
                    ExpertPostionalBean thirdBean = expertPostionalBeans.get(position - hospitalLevelBeans.size() - sortBeans.size() - 3);
                    //绑定数据
                    itemViewHolder.bind(expertPostionalBeans.get(position - hospitalLevelBeans.size() - sortBeans.size() - 3).postional, thirdBean);

                    //绑定选中，手动选中优先
                    if (thirdBean.isSelect()) {
                        itemViewHolder.textView.setSelected(true);
                        multiSelectThree = itemViewHolder.textView;
                        seletpositionThree = position;
                    } else {
                        //绑定默认选中
                        if (current_thirdPosition == default_bed_position) {
                            itemViewHolder.textView.setSelected(true);
                            multiSelectThree = itemViewHolder.textView;
                            seletpositionThree = position;
                        } else {
                            itemViewHolder.textView.setSelected(false);
                        }
                    }
                    //绑定状态
                    onItemClick(holder, position);

                }  else {

                }
                break;

        }
    }

    //还原上一个状态使用的变量
    private TextView multiSelectOne;
    private TextView multiSelectTwo;
    private TextView multiSelectThree;

    private int seletpositionOne = -1;
    private int seletpositionTwo = -1;
    private int seletpositionThree = -1;

    /**
     * 状态监听
     *
     * @param holder
     * @param position
     */
    private void onItemClick(RecyclerView.ViewHolder holder, final int position) {

        final MultiItemHolder itemViewHolder = (MultiItemHolder) holder;

        itemViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position > 0
                        && position < hospitalLevelBeans.size() + 1) {//first

                    //复原 上一个位置状态
                    if (multiSelectOne != null && seletpositionOne != -1) {
                        multiSelectOne.setSelected(false);
                        hospitalLevelBeans.get(seletpositionOne - 1).setSelect(false);
                    }
                    //统一保存选中数据
                    FilterUtils.instance().multiGirdOne = multiSelectOne == null ? null : (HospitalLevelBean) multiSelectOne.getTag();

                    //设置新位置
                    itemViewHolder.textView.setSelected(true);

                    seletpositionOne = position;
                    multiSelectOne = itemViewHolder.textView;

                    //修改数据源状态，避免item状态混乱
                    hospitalLevelBeans.get(position - 1).setSelect(true);
                    //修改标记，避免item状态混乱
                    default_obj_position = -1;

                    //筛选参数-id
                    hospitalLevelBean = hospitalLevelBeans.get(position - 1);

                } else if (position > hospitalLevelBeans.size() + 1
                        && position < ((hospitalLevelBeans.size() + 1) + (sortBeans.size() + 1))) {//second

                    //复原 上一个位置状态
                    if (multiSelectTwo != null && seletpositionTwo != -1) {
                        multiSelectTwo.setSelected(false);
                        sortBeans.get(seletpositionTwo - hospitalLevelBeans.size() - 2).setSelect(false);
                    }
                    //统一保存选中数据
                    FilterUtils.instance().multiGirdTwo = multiSelectTwo == null ? null : (SortBean) multiSelectTwo.getTag();

                    //设置新位置
                    itemViewHolder.textView.setSelected(true);

                    seletpositionTwo = position;
                    multiSelectTwo = itemViewHolder.textView;

                    //修改数据源状态，避免item状态混乱
                    sortBeans.get(position - hospitalLevelBeans.size() - 2).setSelect(true);
                    //修改标记，避免item状态混乱
                    default_property_position = -1;

                    //筛选参数-id
                    sortBean = sortBeans.get(position - hospitalLevelBeans.size() - 2);

                } else if (position > ((hospitalLevelBeans.size() + 1) + (sortBeans.size() + 1))
                        && position < ((hospitalLevelBeans.size() + 1) + (sortBeans.size() + 1) + (expertPostionalBeans.size() + 1))) {//third

                    //复原 上一个位置状态
                    if (multiSelectThree != null && seletpositionThree != -1) {
                        multiSelectThree.setSelected(false);
                        expertPostionalBeans.get(seletpositionThree - hospitalLevelBeans.size() - sortBeans.size() - 3).setSelect(false);
                    }
                    //统一保存选中数据
                    FilterUtils.instance().multiGirdThree = multiSelectThree == null ? null : (ExpertPostionalBean) multiSelectThree.getTag();

                    //设置新位置
                    itemViewHolder.textView.setSelected(true);

                    seletpositionThree = position;
                    multiSelectThree = itemViewHolder.textView;

                    //修改数据源状态，避免item状态混乱
                    expertPostionalBeans.get(position - hospitalLevelBeans.size() - sortBeans.size() - 3).setSelect(true);
                    //修改标记，避免item状态混乱
                    default_bed_position = -1;
                    //筛选参数-id
                    expertPostionalBean = expertPostionalBeans.get(position - hospitalLevelBeans.size() - sortBeans.size() - 3);

                }

                //添加回调，把筛选结果返回
                onAdpaterCallbackListener.onItemClickListener(hospitalLevelBean, sortBean, expertPostionalBean);
            }
        });
    }

    /**
     * 数据的总长度
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return hospitalLevelBeans.size() + sortBeans.size() + expertPostionalBeans.size()  + 3;
    }
}
