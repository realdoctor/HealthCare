package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.API.APIClient;
import com.real.doctor.realdoc.API.NotificationsPOJO.Data;
import com.real.doctor.realdoc.API.NotificationsPOJO.NotificationBody;
import com.real.doctor.realdoc.API.NotificationsPOJO.NotificationMessage;
import com.real.doctor.realdoc.API.NotificationsPOJO.NotificationObject;
import com.real.doctor.realdoc.API.ServerInterface;
import com.real.doctor.realdoc.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationListFragment extends Fragment{

    @BindView(R.id.rv_notifications)
    RecyclerView rv_notifications;


    // API:
    ServerInterface serverInterface;
    List<NotificationBody> all_notifications;
    String notificationId;
    int pageNum = 1;
    int pageSize = 10;
    int userId = 7;
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMzc3Nzg1MDAzNiIsImlhdCI6MTUyNjM3Nzc1MCwic3ViIjoie1wibW9iaWxlUGhvbmVcIjpcIjEzNzc3ODUwMDM2XCIsXCJyZWZyZXNoVG9rZW5cIjpmYWxzZSxcInVzZXJJZFwiOjd9IiwiaXNzIjoiT25saW5lIEpXVCBCdWlsZGVyIiwiYXVkIjoia2FuZ2xpYW4iLCJleHAiOjE1MjY5ODI1NTAsIm5iZiI6MTUyNjM3Nzc1MH0.Ldhx4u-9OGH-2iWua-t403ZpMNsXUdaytVEBMPL2IpQ";


    LinearLayoutManager linearLayoutManager;
    NotificationListAdapter mNotificationListAdapter;
    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        fetchDataFromServer();

        // All notifications recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_notifications.setLayoutManager(layoutManager);

        // add top margin between notifications recyclerview items:
        RecyclerView.ItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(getActivity(), Color.parseColor("#F8FCFA"), 10.0f);
        rv_notifications.addItemDecoration(dividerItemDecoration);

        return view;

    }

    public void fetchDataFromServer(){

        Map<String, String> map = new HashMap<>();
        map.put("userId", "7");


        serverInterface = APIClient.createService(ServerInterface.class, map);

        // get all the Notifications
        // send parameters: token, pageNum, pageSize, userId
        Call<NotificationObject> call = serverInterface.getNotifications(token, pageNum, pageSize, userId);
        call.enqueue(new Callback<NotificationObject>() {
            @Override
            public void onResponse(Call<NotificationObject> call, Response<NotificationObject> response) {
                // fixes the okhttp bug with the strict mode
                TrafficStats.setThreadStatsTag(1000);

                NotificationObject getNotificationJSON = response.body();
                Data data = getNotificationJSON.getData();

                // get all the notifications
                all_notifications = data.getList();

                mNotificationListAdapter = new NotificationListAdapter(R.layout.notification_view, all_notifications, getActivity());

                // set footer to all notifications recyclerview
                LinearLayout linearLayout = new LinearLayout(getActivity());
                View footer = getLayoutInflater().inflate(R.layout.notification_rv_footer, linearLayout);
                mNotificationListAdapter.addFooterView(footer);

                rv_notifications.setAdapter(mNotificationListAdapter);

            }

            @Override
            public void onFailure(Call<NotificationObject> call, Throwable t) {
                Log.e("RETROFIIIIT: ", "error");
                Log.e("Throwable: ", t.toString());
                Log.e("Throwable: ", t.getMessage());
            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



    public class NotificationListAdapter extends BaseQuickAdapter<NotificationBody, BaseViewHolder> {

        RecyclerView notification_data_list_rv;
        NotificationMessagesAdapter notificationMessagesAdapter;

        Context context;

        public NotificationListAdapter(int layoutResId, @Nullable List data, Context context) {
            super(layoutResId, data);

           // notifications = data;
            this.context = context;

        }


        @Override
        protected void convert(BaseViewHolder helper, NotificationBody item) {

            int position = helper.getAdapterPosition();
            populateNotificationPrescriptionList(helper, item, position);


        }


        protected void populateNotificationPrescriptionList(BaseViewHolder helper, NotificationBody item, int position) {

            // get the  content of notification
            NotificationBody notificationBody = all_notifications.get(position);

            // format date: 2018-5-11 14:58
            SimpleDateFormat formatter = new SimpleDateFormat("YYYY-M-d H:mm", Locale.CHINA);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(notificationBody.getAddTime());
            String date =  formatter.format(calendar.getTime());

            // set text
            helper.setText(R.id.notification_type, notificationBody.getNoticeType())
                    .setText(R.id.notification_time, date)
                    .setText(R.id.notification_name, notificationBody.getContent());

            // initialize the recyclerview with the prescribed medicine and their code, make it visible
            notification_data_list_rv = helper.getView(R.id.notification_data_list);

            // populate the medicine-code recyclerview
            addNotificationMessagesRecyclerView(notificationBody);

        }

        protected void addNotificationMessagesRecyclerView(NotificationBody prescriptionBean) {

            // get the list of medicine and their codes
            List<NotificationMessage> notificationMessages = prescriptionBean.getDataList();

            // populate a recyclerview with medicines and codes
            linearLayoutManager = new LinearLayoutManager(context);
            notification_data_list_rv.setLayoutManager(linearLayoutManager);

            notificationId = prescriptionBean.getNoticeTypeId();

            // set adapter to the prescribed medicine recycler view
            notificationMessagesAdapter = new NotificationMessagesAdapter(R.layout.notification_data_list_view, notificationMessages);
            notification_data_list_rv.setAdapter(notificationMessagesAdapter);

        }
    }


        // RecyclerView adapter
        public class NotificationMessagesAdapter extends BaseQuickAdapter<NotificationMessage, BaseViewHolder>{

            List<NotificationMessage> notificationMessages;

            public NotificationMessagesAdapter(int layoutResId, @Nullable List<NotificationMessage> data) {
                super(layoutResId, data);

                notificationMessages = data;
            }

            @Override
            protected void convert(BaseViewHolder helper, NotificationMessage item) {

                int position = helper.getLayoutPosition();

                // get the prescribed medicine and its code
                NotificationMessage notificationMessage =  notificationMessages.get(position);


                if(!notificationId.equals("3")) {
                    helper.setText(R.id.notification_std_name, notificationMessage.getStdName())
                            .setText(R.id.notification_std_code, notificationMessage.getStdCode());
                }
                // don't display the code for the notice of type 新的回复消息
                else {
                    helper.setText(R.id.notification_std_name, notificationMessage.getStdName() + " " + notificationMessage.getStdCode())
                            .setText(R.id.notification_std_code, "");
                }
            }
        }


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
