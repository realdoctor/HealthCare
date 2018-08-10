package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.CheckDocAdapter;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
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
import com.real.doctor.realdoc.view.CommonDialog;

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
    DocDetailAdapter checkDetailAdapter;
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
                checkDetailAdapter = new DocDetailAdapter(InqueryActivity.this, R.layout.doc_detail_item, list);
                //给RecyclerView设置适配器
                checkDetailRv.setAdapter(checkDetailAdapter);
            }
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
