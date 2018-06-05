package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.view.View;

import com.hyphenate.easeui.ui.EaseChatFragment;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.ChatFragment;

import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity {

    private EaseChatFragment chatFragment;
    private String chatUserId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        chatUserId = getIntent().getExtras().getString("userId");
        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
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
}
