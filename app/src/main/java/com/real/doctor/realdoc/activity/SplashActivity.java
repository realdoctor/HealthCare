package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EasyUtils;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.widget.HuanXinHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/18.
 */

public class SplashActivity extends BaseActivity {

    public static final String TAG = "SplashActivity";
    @BindView(R.id.company_logo)
    ImageView companyLogo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void startActivity() {
//        actionStart(SplashActivity.this, LoginActivity.class);
        actionStart(SplashActivity.this, RealDocActivity.class);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                if (HuanXinHelper.getInstance().isLoggedIn()) {
                    // auto login mode, make sure all group and conversation is loaed before enter the main screen
                    EMClient.getInstance().chatManager().loadAllConversations();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    String topActivityName = EasyUtils.getTopActivityName(EMClient.getInstance().getContext());
                    if (topActivityName != null && (topActivityName.equals(VideoCallActivity.class.getName()) || topActivityName.equals(VoiceCallActivity.class.getName()))) {
                        // nop
                        // avoid main screen overlap Calling Activity
                    } else {
                        //enter main screen
                        ScaleAnimation scaleAnim = new ScaleAnimation(
                                1.0f,
                                1.2f,
                                1.0f,
                                1.2f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f
                        );

                        scaleAnim.setFillAfter(true);
                        scaleAnim.setDuration(3000);
                        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //跳转到指定的Activity
                                startActivity();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        companyLogo.startAnimation(scaleAnim);
                    }
                    finish();
                } else {
                    //enter main screen
                    ScaleAnimation scaleAnim = new ScaleAnimation(
                            1.0f,
                            1.2f,
                            1.0f,
                            1.2f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f
                    );

                    scaleAnim.setFillAfter(true);
                    scaleAnim.setDuration(3000);
                    scaleAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //跳转到指定的Activity
//                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            startActivity();
                            finish();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    companyLogo.startAnimation(scaleAnim);
                }
            }
        }).start();

    }
}
