package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.AnswerActivity;
import com.real.doctor.realdoc.adapter.DocPayListAdapter;
import com.real.doctor.realdoc.adapter.DoctorsAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.DoctorBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class RevisitingFragment extends BaseFragment {

    private Unbinder unbinder;
    private String mobile;
    private String userId;
    private List<DoctorBean> doctors;
    @BindView(R.id.revisiting_recycler)
    RecyclerView revisitingRv;
    private DividerItemDecoration divider;
    DocPayListAdapter docPayListAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.revisiting_frag;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(Context mContext) {
        //获得后台正在进行咨询的数据
        mobile = (String) SPUtils.get(getActivity(), "mobile", "");
        userId = (String) SPUtils.get(getActivity(), Constants.USER_KEY, "");
        doctors = new ArrayList<>();
        revisitingRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //添加分割线
        divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.disease_divider));
        revisitingRv.addItemDecoration(divider);
        getRevisitingList("1");
    }

    private void getRevisitingList(String pageNum) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("pageNum", pageNum);
        param.put("pageSize", "10");
        param.put("userId", userId);
        HttpRequestClient.getInstance(getActivity()).createBaseApi().get("askQuestion/reply/list"
                , param, new BaseObserver<ResponseBody>(getActivity()) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(getActivity(), "获取咨询列表失败!");
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = "";
                        String code = "";
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    JSONObject obj = object.getJSONObject("data");
                                    if (DocUtils.hasValue(obj, "list")) {
                                        doctors = GsonUtil.GsonToList(obj.getJSONArray("list").toString(), DoctorBean.class);
                                        if (doctors.size() > 0) {
                                            docPayListAdapter = new DocPayListAdapter(getActivity(), R.layout.doc_pay_list_item, doctors, true);
                                            revisitingRv.setAdapter(docPayListAdapter);
                                            initEvent();
//                                             docPayListAdapter.notifyDataSetChanged();
                                        } else {
                                            ToastUtil.showLong(getActivity(), "获取咨询列表失败!");
                                        }
                                    }

                                } else {
                                    ToastUtil.showLong(getActivity(), "获取咨询列表失败!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    private void initEvent() {
        docPayListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击item
                Intent intent = new Intent(getActivity(), AnswerActivity.class);
                intent.putExtra("inquery", doctors.get(position).getInquery());
                intent.putExtra("answer", doctors.get(position).getAnswer());
                intent.putExtra("doctor", doctors.get(position).getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
