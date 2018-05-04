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
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DocDetailActivity extends BaseActivity {

    @BindView(R.id.right_title)
    TextView rightTitle;
    DocDetailAdapter docDetailAdapter;
    @BindView(R.id.doc_detail_recycler)
    RecyclerView docDetailRecycleView;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    private int num;
    private List<SaveDocBean> mList = new ArrayList<>();
    private List<SaveDocBean> list = null;
    private static Integer pageNum = 1;
    private int length = 0;

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
        rightTitle.setVisibility(View.VISIBLE);
        rightTitle.setText("商城");
        SaveDocManager instance = SaveDocManager.getInstance(DocDetailActivity.this);
        list = instance.querySaveDocList(DocDetailActivity.this);
        docDetailAdapter = new DocDetailAdapter(DocDetailActivity.this, R.layout.doc_detail_item, list);
        //给RecyclerView设置适配器
        docDetailRecycleView.setAdapter(docDetailAdapter);
        //创建布局管理
        docDetailRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加Android自带的分割线
        docDetailRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void postData(String pageSize, String mobilePhone) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobilePhone", "13777850036");
        map.put("pageSize", pageSize);
        map.put("pageNum", String.valueOf(pageNum));
        HttpRequestClient.getInstance(DocDetailActivity.this).createBaseApi().get("patient/"
                , map, new BaseObserver<ResponseBody>(DocDetailActivity.this) {

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
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    if (DocUtils.hasValue(object, "data")) {
                                        JSONObject jsonData = object.getJSONObject("data");
                                        if (DocUtils.hasValue(jsonData, "pageSize")) {
                                            String pageSize = jsonData.getString("pageSize");
                                        }
                                        if (DocUtils.hasValue(jsonData, "pageNum")) {
                                            String pageNum = jsonData.getString("pageNum");
                                        }
                                        if (DocUtils.hasValue(jsonData, "pages")) {
                                            String pages = jsonData.getString("pages");
                                        }
                                        if (DocUtils.hasValue(jsonData, "total")) {
                                            String total = jsonData.getString("total");
                                        }
                                        List<SaveDocBean> bean = GsonUtil.GsonToList(jsonData.getJSONArray("list").toString(), SaveDocBean.class);
                                        System.out.print(bean);
                                        if (bean.size() > 0) {
                                            mList.addAll(bean);
                                            //创建适配器
                                            docDetailAdapter = new DocDetailAdapter(DocDetailActivity.this, R.layout.doc_detail_item, mList);
                                            //给RecyclerView设置适配器
                                            docDetailRecycleView.setAdapter(docDetailAdapter);
                                            docDetailAdapter.loadMoreComplete();
                                        } else {
                                            docDetailAdapter.loadMoreComplete();
                                        }
                                        initEvent();
                                    }
                                } else {
                                    ToastUtil.showLong(DocDetailActivity.this, "病历数据请求失败!");
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
    @OnClick({R.id.finish_back, R.id.right_title})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.right_title:
                actionStart(this, ProductShowByCategoryActivity.class);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

}
