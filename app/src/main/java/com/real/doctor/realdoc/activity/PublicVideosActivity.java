package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.VideoAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.model.VideoListBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class PublicVideosActivity extends BaseActivity {
    @BindView(R.id.video_recycle_view)
    RecyclerView videoRecycleView;
    @BindView(R.id.right_title)
    TextView right_title;
    @BindView(R.id.page_title)
    TextView page_title;
    @BindView(R.id.record_img_edit)
    EditText recordImgEdit;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    private List<VideoBean> videoList=new ArrayList<>();
    private VideoAdapter videoAdapter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_public_video;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(PublicVideosActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        page_title.setText("视频");
        right_title.setVisibility(View.VISIBLE);
        right_title.setText("上传");
        // 从数据库中取出视频列表,并展示
        String folder=getIntent().getStringExtra("folder");
        VideoManager videoInstance = VideoManager.getInstance(PublicVideosActivity.this);
        videoList = videoInstance.queryVideoWithFolder(PublicVideosActivity.this, folder);
        if (EmptyUtils.isNotEmpty(videoList) && videoList.size() > 0) {
            videoRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            //notifyDataSetChanged()
            videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
            videoRecycleView.setAdapter(videoAdapter);
            videoAdapter.notifyDataSetChanged();
        }
//        videoList = new ArrayList<>();
//        videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
//        videoRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        videoRecycleView.setAdapter(videoAdapter);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back,R.id.right_title})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.right_title:
                String inputStr= recordImgEdit.getText().toString();
                if(inputStr==null||inputStr.length()==0){
                    Toast.makeText(PublicVideosActivity.this,"请输入描述信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                upload(videoList,inputStr);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    private void upload(List<VideoBean> beans, String inputStr) {
        if (NetworkUtil.isNetworkAvailable(PublicVideosActivity.this)) {
            Map<String, RequestBody> maps = new HashMap<>();
            maps.put("content", DocUtils.toRequestBodyOfText(inputStr));
            for(VideoBean bean:beans) {
                File file = new File(String.valueOf(bean.filePath));
                if (file.exists()) {
                    RequestBody requestBody = DocUtils.toRequestBodyOfVideo(file);
                    maps.put("attach\"; filename=\"" + file.getName() + "", requestBody);//head_img图片key
                }
            }

            HttpRequestClient.getInstance(PublicVideosActivity.this).createBaseApi().uploads("upload/uploadFiles/", maps, new BaseObserver<ResponseBody>(PublicVideosActivity.this) {
                @Override
                public void onSubscribe(Disposable d) {

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
                                ToastUtil.showLong(RealDocApplication.getContext(), "图片上传成功!");
                                PublicVideosActivity.this.finish();
                            } else {
                                ToastUtil.showLong(RealDocApplication.getContext(), "图片上传失败!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.showLong(PublicVideosActivity.this, e.getMessage());
                    Log.d(TAG, e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }
}
