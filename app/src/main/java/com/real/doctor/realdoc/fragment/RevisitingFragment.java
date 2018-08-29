package com.real.doctor.realdoc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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
    private String roleChangeId;
    private int mPageNum = 1;
    private List<DoctorBean> doctors;
    @BindView(R.id.revisiting_recycler)
    RecyclerView revisitingRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private DividerItemDecoration divider;
    private Dialog mProgressDialog;
    DocPayListAdapter docPayListAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.revisiting_frag;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        mProgressDialog = DocUtils.getProgressDialog(getActivity(), "正在加载数据....");
    }

    @Override
    public void doBusiness(Context mContext) {
        //获得后台正在进行咨询的数据
        mobile = (String) SPUtils.get(getActivity(), Constants.MOBILE, "");
        userId = (String) SPUtils.get(getActivity(), Constants.USER_KEY, "");
        roleChangeId = (String) SPUtils.get(getActivity(), Constants.ROLE_CHANGE_ID, "");
        doctors = new ArrayList<>();
        revisitingRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //添加分割线
        divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.disease_divider));
        revisitingRv.addItemDecoration(divider);
        getRevisitingList("1");
        swipeRefresh();
    }

    private void getRevisitingList(final String pageNum) {
        mProgressDialog.show();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("pageNum", pageNum);
        param.put("pageSize", "10");
        param.put("status", "1");
        param.put("roleId", roleChangeId);
        param.put("userId", userId);
        HttpRequestClient.getInstance(getActivity()).createBaseApi().get("askQuestion/reply/doctorList"
                , param, new BaseObserver<ResponseBody>(getActivity()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mPageNum == 1) {
                            refreshLayout.finishRefresh();
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                        ToastUtil.showLong(getActivity(), "获取咨询列表失败!");
                        mProgressDialog.dismiss();
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
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
                                            if (EmptyUtils.isEmpty(docPayListAdapter)) {
                                                docPayListAdapter = new DocPayListAdapter(getActivity(), R.layout.doc_pay_list_item, doctors, true);
                                                revisitingRv.setAdapter(docPayListAdapter);
                                            } else {
                                                docPayListAdapter.notifyDataSetChanged();
                                            }
                                            initEvent();
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(getActivity(), "获取咨询列表失败!");
                                }
                                if (mPageNum == 1) {
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishRefresh();
                                    refreshLayout.finishLoadmore();
                                }
                                mProgressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    private void swipeRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //处理刷新列表逻辑
                getRevisitingList("1");
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getRevisitingList(String.valueOf(++mPageNum));
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
                intent.putExtra("questionId", doctors.get(position).getQuestionId());
                intent.putExtra("retryNum", doctors.get(position).getRetryNum());
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
