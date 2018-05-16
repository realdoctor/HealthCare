package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.Notifications.NotificationBean;
import com.real.doctor.realdoc.model.Notifications.NotificationMessageBean;
import com.real.doctor.realdoc.model.Notifications.NotificationPrescriptionBean;
import com.real.doctor.realdoc.model.Notifications.NotificationRecordBean;
import com.real.doctor.realdoc.model.Notifications.PrescribedMedicineBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class NotificationListFragment extends Fragment{


    NotificationListAdapter mNotificationListAdapter;
    Unbinder unbinder;

    @BindView(R.id.rv_notifications)
    RecyclerView rv_notifications;

    List<PrescribedMedicineBean> prescriptionList;

    NotificationPrescriptionBean mNotificationPrescriptionBean;
    NotificationRecordBean mNotificationRecordBean;
    NotificationMessageBean mNotificationMessageBean;

    List<NotificationBean> all_notifications;

    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        initData();

        // All notifications recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_notifications.setLayoutManager(layoutManager);

        // add top margin between notifications recyclerview items:
        RecyclerView.ItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(getActivity(), Color.parseColor("#F8FCFA"), 10.0f);
        rv_notifications.addItemDecoration(dividerItemDecoration);

        // create adapter for all notifications
        mNotificationListAdapter = new NotificationListAdapter(R.layout.notification_view, all_notifications, getActivity());

        // set footer to all notifications recyclerview
        LinearLayout linearLayout = new LinearLayout(getActivity());
        View footer = getLayoutInflater().inflate(R.layout.notification_rv_footer, linearLayout);
        mNotificationListAdapter.addFooterView(footer);

        rv_notifications.setAdapter(mNotificationListAdapter);

        return view;

    }

    public void initData(){

        all_notifications = new ArrayList<>();

        for(int i = 0; i < 10; i ++){

            // Prescription:
            prescriptionList = new ArrayList<>();

            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)1", "1020200004"));
            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)2", "2010654322"));
            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)3", "1020200021"));
            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)4", "2010608643"));
            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)5", "2010605322"));
            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)3", "1020200065"));
            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)1", "2010606842"));
            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)2", "2010605441"));
            prescriptionList.add(new PrescribedMedicineBean("复方甘草(片剂)3", "10202000084"));

            mNotificationPrescriptionBean = new NotificationPrescriptionBean();
            mNotificationPrescriptionBean.setType("就诊用药提醒");
            mNotificationPrescriptionBean.setTime("4:13");
            mNotificationPrescriptionBean.setTitle("根据就诊情况，医生给您开了药方");
            mNotificationPrescriptionBean.setPrescriptions(prescriptionList);

            all_notifications.add(mNotificationPrescriptionBean);


            // Records:
            mNotificationRecordBean = new NotificationRecordBean();
            mNotificationRecordBean.setType("病历更新");
            mNotificationRecordBean.setTime("4:08");
            mNotificationRecordBean.setTitle("就诊结束，病历更新啦");
            mNotificationRecordBean.setNotice("上呼吸道疾病");
            mNotificationRecordBean.setCode("J39.900");

            all_notifications.add(mNotificationRecordBean);

            // Messages:
            mNotificationMessageBean = new NotificationMessageBean();
            mNotificationMessageBean.setType("新的回复消息");
            mNotificationMessageBean.setTime("2018-5-9 10:31");
            mNotificationMessageBean.setTitle("有人回复了您的评论");
            mNotificationMessageBean.setQuestion("小罗伯特唐尼：推荐你使用小儿肺咳颗粒，药到病…");
            mNotificationMessageBean.setAnswer("我儿子生病一直咳，应该怎么办，…");

            all_notifications.add(mNotificationMessageBean);

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class NotificationListAdapter extends BaseQuickAdapter<NotificationBean, BaseViewHolder> {

        List<NotificationBean> notifications;

        RecyclerView notification_prescribed_medicine_list_rv;
        PrescribedMedicineAdapter prescribedMedicineAdapter;

       // ListView mListView;
        //ArrayAdapter<PrescribedMedicineBean> prescribedMedicineAdapter;

        Context context;

        public NotificationListAdapter(int layoutResId, @Nullable List data, Context context) {
            super(layoutResId, data);

            notifications = data;
            this.context = context;

        }


        @Override
        protected void convert(BaseViewHolder helper, NotificationBean item) {

            int position = helper.getAdapterPosition();

            switch(item.getType()){
                // prescription
                case "就诊用药提醒": populateNotificationPrescriptionList(helper, item, position);
                    break;
                // record
                case "病历更新": populateNotificationRecordList(helper, item, position);
                    break;
                // message
                case "新的回复消息": populateNotificationMessageList(helper, item, position);
                    break;
            }
        }


        protected void populateNotificationPrescriptionList(BaseViewHolder helper, NotificationBean item, int position){

            // get the prescription notification
            NotificationPrescriptionBean prescriptionBean = (NotificationPrescriptionBean) notifications.get(position);

            // set text
            helper.setText(R.id.notification_type, prescriptionBean.getType())
                    .setText(R.id.notification_time, prescriptionBean.getTime())
                    .setText(R.id.notification_name, prescriptionBean.getTitle());


            // initialize the recyclerview with the prescribed medicine and their code, make it visible
            notification_prescribed_medicine_list_rv =  helper.getView(R.id.notification_medicine_list);
            notification_prescribed_medicine_list_rv.setVisibility(View.VISIBLE);

            // get rid of the view that are not needed
            helper.getView(R.id.notification_msg_title1).setVisibility(View.GONE);
            helper.getView(R.id.notification_msg_title2).setVisibility(View.GONE);
            helper.getView(R.id.notification_msg_code1).setVisibility(View.GONE);
            helper.getView(R.id.notification_msg_code2).setVisibility(View.GONE);


           // mListView = helper.getView (R.id.notification_medicine_list);
            //mListView.setVisibility(View.VISIBLE);


            // populate the medicine-code recyclerview
            addPrescribedMedicineRecyclerView(prescriptionBean);

        }

        protected void addPrescribedMedicineRecyclerView(NotificationPrescriptionBean prescriptionBean){

            // get the list of medicine and their codes
            List<PrescribedMedicineBean> prescribedMedicineBeans = prescriptionBean.getPrescriptions();

            // populate a recyclerview with medicines and codes
            linearLayoutManager = new LinearLayoutManager(context);
            notification_prescribed_medicine_list_rv.setLayoutManager(linearLayoutManager);

            // set adapter to the prescribed medicine recycler view
            prescribedMedicineAdapter = new PrescribedMedicineAdapter(R.layout.prescribed_medicine_view, prescribedMedicineBeans);
            notification_prescribed_medicine_list_rv.setAdapter(prescribedMedicineAdapter);

            /**  listview **/
            //prescribedMedicineAdapter = new PrescribedMedicineAdapter(context, R.layout.prescribed_medicine_view, prescribedMedicineBeans);
           // mListView.setAdapter(prescribedMedicineAdapter);

        }

        protected void populateNotificationRecordList(BaseViewHolder helper, NotificationBean item, int position){

            NotificationRecordBean recordBean = (NotificationRecordBean) notifications.get(position);

            // get rid of the view that are not needed
            helper.getView(R.id.notification_medicine_list).setVisibility(View.GONE);
            helper.getView(R.id.notification_msg_title2).setVisibility(View.GONE);
            helper.getView(R.id.notification_msg_code2).setVisibility(View.GONE);

            // make visible the record title and code fields
            helper.getView(R.id.notification_msg_title1).setVisibility(View.VISIBLE);
            helper.getView(R.id.notification_msg_code1).setVisibility(View.VISIBLE);

            // set text
            helper.setText(R.id.notification_type, recordBean.getType())
                    .setText(R.id.notification_time, recordBean.getTime())
                    .setText(R.id.notification_name, recordBean.getTitle())
                    .setText(R.id.notification_msg_title1, recordBean.getNotice())
                    .setText(R.id.notification_msg_code1, recordBean.getCode());


           /*  mListView.setVisibility(View.GONE); */


        }

        protected void populateNotificationMessageList(BaseViewHolder helper, NotificationBean item, int position){

            NotificationMessageBean messageBean = (NotificationMessageBean) notifications.get(position);

            // get rid of the view that are not needed
            helper.getView(R.id.notification_medicine_list).setVisibility(View.GONE);
            helper.getView(R.id.notification_msg_code1).setVisibility(View.GONE);
            helper.getView(R.id.notification_msg_code2).setVisibility(View.GONE);

            // make visible the question and answer fields
            helper.getView(R.id.notification_msg_title1).setVisibility(View.VISIBLE);
            helper.getView(R.id.notification_msg_title2).setVisibility(View.VISIBLE);

            // set text
            helper.setText(R.id.notification_type, messageBean.getType())
                    .setText(R.id.notification_time, messageBean.getTime())
                    .setText(R.id.notification_name, messageBean.getTitle())
                    .setText(R.id.notification_msg_title1, messageBean.getQuestion())
                    .setText(R.id.notification_msg_title2, "回复我的评论 : " + messageBean.getAnswer());
        }

    }


    // RecyclerView adapter
    public class PrescribedMedicineAdapter extends BaseQuickAdapter<PrescribedMedicineBean, BaseViewHolder>{

        List<PrescribedMedicineBean> prescribedMedicineList;

        public PrescribedMedicineAdapter(int layoutResId, @Nullable List<PrescribedMedicineBean> data) {
            super(layoutResId, data);

            prescribedMedicineList = data;
        }

        @Override
        protected void convert(BaseViewHolder helper, PrescribedMedicineBean item) {

            int position = helper.getLayoutPosition();

            // get the prescribed medicine and its code
            PrescribedMedicineBean prescriptionBean = (PrescribedMedicineBean) prescribedMedicineList.get(position);

            helper.setText(R.id.prescribed_medicine_title, prescriptionBean.getMedicine())
                    .setText(R.id.prescribed_medicine_code, prescriptionBean.getCode());
        }
    }


    /** WRITE HERE THE REGULAR ARRAY LIST ADAPTER **/
   /* public class PrescribedMedicineAdapter extends ArrayAdapter<PrescribedMedicineBean>{

        List<PrescribedMedicineBean>  prescribedMedicineList;
        Context mContext;
        LayoutInflater mLayoutInflater;

        public PrescribedMedicineAdapter(@NonNull Context context, int resource, @NonNull List<PrescribedMedicineBean> objects) {
            super(context, resource, objects);

            prescribedMedicineList = objects;
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.prescribed_medicine_view, parent, false);
            }


            PrescribedMedicineBean prescribedMedicine = prescribedMedicineList.get(position);
            //Log.e("");

            ((TextView) view.findViewById(R.id.prescribed_medicine_title)).setText(prescribedMedicine.getMedicine());
            ((TextView) view.findViewById(R.id.prescribed_medicine_code)).setText(prescribedMedicine.getCode());



            return view;
        }

        @Override
        public int getCount() {
            return prescribedMedicineList.size();
        }

        @Override
        public int getPosition(@Nullable PrescribedMedicineBean item) {
            return super.getPosition(item);
        }
    } */



    /** Utility functions **/

    // adding top margin between recyclerview items
    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private  Paint mPaint;

        public VerticalSpaceItemDecoration(Context context, int color, float heightDp) {

            mPaint = new Paint();
            mPaint.setColor(color);
            final float thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    heightDp, context.getResources().getDisplayMetrics());
            mPaint.setStrokeWidth(thickness);
        }


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

            // we want to retrieve the position in the list
            final int position = params.getViewAdapterPosition();

            // and add a separator to any view but the last one
            if (position < state.getItemCount()) {
                outRect.set(0, (int) mPaint.getStrokeWidth(), 0, 0); // left, top, right, bottom
            } else {
                outRect.setEmpty(); // 0, 0, 0, 0
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            // we set the stroke width before, so as to correctly draw the line we have to offset by width / 2
            final int offset = (int) (mPaint.getStrokeWidth() / 2);

            // this will iterate over every visible view
            for (int i = 0; i < parent.getChildCount(); i++) {
                // get the view
                final View view = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

                // get the position
                final int position = params.getViewAdapterPosition();

                // and finally draw the separator
                if (position < state.getItemCount()) {
                    c.drawLine(view.getLeft(), view.getTop() - offset, view.getRight(), view.getTop() - offset, mPaint);
                }
            }
        }


    }

}
