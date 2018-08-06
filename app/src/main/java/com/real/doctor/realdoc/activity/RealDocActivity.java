package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.ArticleShowFragment;
import com.real.doctor.realdoc.fragment.HomeFragment;
import com.real.doctor.realdoc.fragment.MessageFragment;
import com.real.doctor.realdoc.fragment.ReadFragment;
import com.real.doctor.realdoc.fragment.ShoppintMallFragment;
import com.real.doctor.realdoc.fragment.UserFragment;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class RealDocActivity extends BaseActivity {

    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.homepage)
    RadioButton homepage;
    @BindView(R.id.read)
    RadioButton read;
    @BindView(R.id.message)
    RadioButton message;
    @BindView(R.id.user)
    RadioButton user;
    //fragments
    private ArrayList<Fragment> fragments;
    private HomeFragment homeFragment;
    private ArticleShowFragment readFragment;
    private ShoppintMallFragment shoppintMallFragment;
    private UserFragment userFragment;
    private FragmentTransaction transaction;
    private Fragment tempFragment;
    private int position = 0;
    private long exitTime = 0;
    private boolean isDeleteFolder = false;
    private PackageManager p;
    private boolean permission;

    @Override
    public int getLayoutId() {
        return R.layout.activity_real_doc;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //极光推送初始化
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(RealDocActivity.this);
        builder.statusBarDrawable = R.mipmap.icon_app;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
        builder.notificationDefaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);
    }

    @Override
    public void initData() {
        //删除从前文件夹,并重新创建新的文件夹
        p = getPackageManager();
        permission = (PackageManager.PERMISSION_GRANTED ==
                p.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.real.doctor.realdoc"));
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x0001);
            }
        } else {
            init();
        }
        fragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        readFragment = ArticleShowFragment.newInstance();
        shoppintMallFragment = ShoppintMallFragment.newInstance();
        userFragment = new UserFragment();

        fragments.add(homeFragment);
        fragments.add(readFragment);
        fragments.add(shoppintMallFragment);
        fragments.add(userFragment);
        tempFragment = homeFragment;
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.frame_layout, homeFragment).commit();
    }

    @Override
    public void initEvent() {
        radioGroup.check(R.id.homepage);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.homepage:
                        position = 0;
                        homeFragment.onShowMenu();
                        break;
                    case R.id.read:
                        position = 1;
                        homeFragment.onDestroyMenu();
                        break;
                    case R.id.message:
                        position = 2;
                        homeFragment.onDestroyMenu();
                        break;
                    case R.id.user:
                        position = 3;
                        homeFragment.onDestroyMenu();
                        break;
                    default:
                        position = 0;
                        break;
                }
                //根据位置得到相应的Fragment
                Fragment fragment = getFragment(position);
                switchFragment(tempFragment, fragment);
            }
        });
    }

    /**
     * 根据位置得到对应的 Fragment
     *
     * @param position
     * @return
     */
    private Fragment getFragment(int position) {
        if (fragments != null && fragments.size() > 0) {
            Fragment baseFragment = fragments.get(position);
            return baseFragment;
        }
        return null;
    }

    /**
     * 切换Fragment
     *
     * @param fragment
     * @param nextFragment
     */
    private void switchFragment(Fragment fragment, Fragment nextFragment) {
        if (tempFragment != nextFragment) {
            tempFragment = nextFragment;
            if (nextFragment != null) {
                transaction = getSupportFragmentManager().beginTransaction();
                //判断nextFragment是否添加成功
                if (!nextFragment.isAdded()) {
                    //隐藏当前的Fragment
                    if (fragment != null) {
                        transaction.hide(fragment);
                    }
                    //添加Fragment
                    transaction.add(R.id.frame_layout, nextFragment).commit();
                } else {
                    //隐藏当前Fragment
                    if (fragment != null) {
                        transaction.hide(fragment);
                    }
                    transaction.show(nextFragment).commit();
                }
            }
        }
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitApp();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showLong(this, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }

    /**
     * 权限成功回调函数
     *
     * @param requestCode
     */
    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        switch (requestCode) {
            case 0x0001:
                init();
                permission = (PackageManager.PERMISSION_GRANTED ==
                        p.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.real.doctor.realdoc"));
                if (!permission) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x0001);
                    }
                }
                break;
        }
    }

    private void init() {
        //存放一个变量,这个变量永远为true
        isDeleteFolder = (boolean) SPUtils.get(RealDocActivity.this, "isDeleteFolder", false);
        if (!isDeleteFolder) {
            SPUtils.put(RealDocActivity.this, "isDeleteFolder", true);
            if (FileUtils.isFileExists(SDCardUtils.getGlobalDir())) {
                //删除文件夹
                StringBuffer sb = new StringBuffer();
                sb.append(SDCardUtils.getGlobalDir());
                FileUtils.deleteDir(sb.toString());
            }
            //建立全局文件夹
            SDCardUtils.creatSDDir("RealDoc");
        } else {
            if (!FileUtils.isFileExists(SDCardUtils.getGlobalDir())) {
                SDCardUtils.creatSDDir("RealDoc");
            }
        }
    }
}
