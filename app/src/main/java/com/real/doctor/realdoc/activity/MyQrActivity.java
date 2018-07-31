package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.util.ZXingUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MyQrActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.my_qr)
    ImageView ivQrCode;
    @BindView(R.id.my_real_name)
    TextView myRealName;
    private String userId;
    private String realName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_qr;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(MyQrActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        pageTitle.setText("我的二维码");
        userId = (String) SPUtils.get(MyQrActivity.this, Constants.USER_KEY, "");
        realName = (String) SPUtils.get(MyQrActivity.this, "realName", "");
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {
        StringBuffer sb = new StringBuffer();
        sb.append("doctorId");
        sb.append("=");
        sb.append(userId);
        String url = sb.toString();
        Bitmap bitmap = ZXingUtils.createQRImage(url, SizeUtils.dp2px(this, 150), SizeUtils.dp2px(this, 150));
        ivQrCode.setImageBitmap(bitmap);
        if (EmptyUtils.isNotEmpty(realName)) {
            myRealName.setText("我是" + realName + "医生, 这是我的二维码");
        }
    }
}
