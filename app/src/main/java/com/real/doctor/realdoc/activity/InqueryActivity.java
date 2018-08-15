package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDeleteAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.SwipeItemLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class InqueryActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.inquery_edit)
    EditText inqueryEdit;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.add_record_btn)
    Button addRecordBtn;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.check_detail_rv)
    RecyclerView checkDetailRv;
    DocDeleteAdapter checkDeleteAdapter;
    private String doctorUserId;
    private String patientRecordId;
    private String desease;
    private String questionId;
    private boolean detail;
    private String inqueryEditContent;
    private List<SaveDocBean> list;
    private SaveDocManager instance;
    private static final int REQUEST_SEND_RECORDS = 0x100;

    @Override
    public int getLayoutId() {
        return R.layout.activity_inquery;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(InqueryActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("咨询内容");
    }

    @Override
    public void initData() {
        doctorUserId = getIntent().getStringExtra("doctorUserId");
        desease = getIntent().getStringExtra("desease");
        questionId = getIntent().getStringExtra("questionId");
        detail = getIntent().getBooleanExtra("detail", false);
        patientRecordId = getIntent().getStringExtra("patientRecordId");
        //创建布局管理
        checkDetailRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        checkDetailRv.addItemDecoration(divider);
        instance = SaveDocManager.getInstance(this);
        //初始化数据库中isSelect=false
        List<SaveDocBean> mList = instance.querySaveDocList(this);
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setIsSelect(false);
        }
        instance.updateRecordList(mList);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.send, R.id.finish_back, R.id.add_record_btn})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.add_record_btn:
                //进入病历列表页面
                Intent intent = new Intent(InqueryActivity.this, CheckDocActivity.class);
                intent.putParcelableArrayListExtra("mList", (ArrayList<? extends Parcelable>) list);
                intent.putExtra("desease", desease);
                startActivityForResult(intent, REQUEST_SEND_RECORDS);
                break;
            case R.id.send:
                inqueryEditContent = inqueryEdit.getText().toString().trim();
                if (EmptyUtils.isNotEmpty(inqueryEditContent)) {
                    if (inqueryEditContent.length() > 10) {
                        if (EmptyUtils.isNotEmpty(list) && list.size() > 0) {
                            intent = new Intent(InqueryActivity.this, ProgressBarActivity.class);
                            intent.putExtra("inquery", inqueryEditContent);
                            intent.putExtra("desease", desease);
                            intent.putParcelableArrayListExtra("mList", (ArrayList<? extends Parcelable>) list);
                            intent.putExtra("doctorUserId", doctorUserId);
                            intent.putExtra("questionId", questionId);
                            intent.putExtra("detail", detail);
                            intent.putExtra("patientRecordId", patientRecordId);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            postInquery();
                        }
                    } else {
                        ToastUtil.showLong(InqueryActivity.this, "咨询内容不能小于10个字符!");
                    }
                } else {
                    ToastUtil.showLong(InqueryActivity.this, "咨询内容不能为空!");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_SEND_RECORDS) {
            //回调list
            if (data != null) {
                list = data.getParcelableArrayListExtra("records");
                //倒序排列
                Collections.reverse(list);
                checkDeleteAdapter = new DocDeleteAdapter(InqueryActivity.this, R.layout.doc_delete_item, list);
                //给RecyclerView设置适配器
                checkDetailRv.setAdapter(checkDeleteAdapter);
                initCheckedEvent();
            }
        }
    }

    public void initCheckedEvent() {
        checkDetailRv.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        checkDeleteAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SaveDocBean bean = (SaveDocBean) adapter.getItem(position);
                Intent intent = new Intent(InqueryActivity.this, DocContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("SaveDocBean", bean);
                intent.putExtras(mBundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void postInquery() {
        if (NetworkUtil.isNetworkAvailable(InqueryActivity.this)) {
            Map<String, RequestBody> maps = new HashMap<>();
            maps.put("content", DocUtils.toRequestBodyOfText(inqueryEditContent));
            maps.put("title", DocUtils.toRequestBodyOfText(desease));
            maps.put("receiveUserId", DocUtils.toRequestBodyOfText(doctorUserId));
            maps.put("patientRecordId", DocUtils.toRequestBodyOfText(patientRecordId));
            if (EmptyUtils.isNotEmpty(questionId)) {
                maps.put("questionId", DocUtils.toRequestBodyOfText(questionId));
            }
            HttpRequestClient.getInstance(InqueryActivity.this).createBaseApi().uploads("upload/uploadPatient/", maps, new BaseObserver<ResponseBody>(InqueryActivity.this) {
                protected Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                @Override
                protected void onHandleSuccess(ResponseBody responseBody) {
                    //上传文件成功
                    String data = null;
                    String msg = null;
                    String code = null;
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
                                ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传成功!");
                            } else {
                                ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传失败!");
                            }
                            Intent intent;
                            if (detail) {
                                intent = new Intent(InqueryActivity.this, DoctorsDetailActivity.class);
                                intent.putExtra("doctorUserId", doctorUserId);
                            } else {
                                intent = new Intent(InqueryActivity.this, DoctorsListActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传失败!");
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
            });
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
