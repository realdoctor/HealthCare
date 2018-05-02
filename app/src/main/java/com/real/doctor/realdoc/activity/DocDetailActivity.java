package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.RecyclerViewSpacesItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DocDetailActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {

    DocDetailAdapter docDetailAdapter;
    @BindView(R.id.doc_detail_recycler)
    RecyclerView docDetailRecycleView;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    private int num;
    private List<SaveDocBean> mList = new ArrayList<>();
    private List<SaveDocBean> list = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doc_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

        SaveDocManager instance = SaveDocManager.getInstance(DocDetailActivity.this);
        list = instance.querySaveDocList(DocDetailActivity.this);
        docDetailAdapter = new DocDetailAdapter(DocDetailActivity.this, R.layout.doc_detail_item, list);
        //给RecyclerView设置适配器
        docDetailRecycleView.setAdapter(docDetailAdapter);
        docDetailRecycleView = findViewById(R.id.doc_detail_recycler);
        //创建布局管理
        docDetailRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加Android自带的分割线
        docDetailRecycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        int length = list.size();
        num = length / 10;
//        if (length > 10) {
//        mList.addAll(list.subList(0, 10));
        //创建适配器
        docDetailAdapter = new DocDetailAdapter(DocDetailActivity.this, R.layout.doc_detail_item, list);
        //给RecyclerView设置适配器
        docDetailRecycleView.setAdapter(docDetailAdapter);
//        } else {
//            int pageNum = 10 - length;
//            //将网页中的数据加进去
//            //从网络中获取数据
//            postData(String.valueOf(pageNum));
//        }
    }

    private void postData(String pageNum) {
        JSONObject json = null;
        if (EmptyUtils.isNotEmpty(pageNum)) {
            json = new JSONObject();
            try {
                json.put("pageNum", pageNum);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showLong(DocDetailActivity.this, "密码不能为空!");
            return;
        }
        HttpRequestClient.getInstance(DocDetailActivity.this).createBaseApi().get("patient/medicalRecord/"
                , null, new BaseObserver<ResponseBody>(DocDetailActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(DocDetailActivity.this, e.getMessage());
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
//                                if (DocUtils.hasValue(object, "msg")) {
//                                    msg = object.getString("msg");
//                                }
//                                if (DocUtils.hasValue(object, "code")) {
//                                    code = object.getString("code");
//                                }
//                                if (msg.equals("ok") && code.equals("0")) {
//                                    ToastUtil.showLong(LoginActivity.this, "用户登录成功!");
//                                    if (DocUtils.hasValue(object, "data")) {
//                                        actionStart(LoginActivity.this, RealDocActivity.class);
//                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                                        finish();
//                                    }
//                                } else {
//                                    ToastUtil.showLong(LoginActivity.this, "用户登录失败!");
//                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    @Override
    public void initEvent() {
        docDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SaveDocBean bean = (SaveDocBean) adapter.getItem(position);
                Intent intent = new Intent(DocDetailActivity.this, DocContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("SaveDocBean", bean);
                intent.putExtras(mBundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onLoadMoreRequested() {

    }
}
