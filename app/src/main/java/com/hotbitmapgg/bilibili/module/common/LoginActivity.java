package com.hotbitmapgg.bilibili.module.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hotbitmapgg.bilibili.base.RxBaseActivity;
import com.hotbitmapgg.bilibili.network.RetrofitHelper;
import com.hotbitmapgg.bilibili.network.auxiliary.ApiConstants;
import com.hotbitmapgg.bilibili.utils.CommonUtil;
import com.hotbitmapgg.bilibili.utils.ConstantUtil;
import com.hotbitmapgg.bilibili.utils.PreferenceUtil;
import com.hotbitmapgg.bilibili.utils.ToastUtil;
import com.hotbitmapgg.ohmybilibili.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/7 14:12
 * 100332338@qq.com
 * <p/>
 * 登录界面
 */
public class LoginActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_icon_left)
    ImageView mLeftLogo;
    @BindView(R.id.iv_icon_right)
    ImageView mRightLogo;
    @BindView(R.id.delete_username)
    ImageView mDeleteUserName;
    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_password)
    EditText et_password;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }


    @Override
    public void initViews(Bundle savedInstanceState) {
        et_username.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && et_username.getText().length() > 0) {
                mDeleteUserName.setVisibility(View.VISIBLE);
            } else {
                mDeleteUserName.setVisibility(View.GONE);
            }
            mLeftLogo.setImageResource(R.drawable.ic_22);
            mRightLogo.setImageResource(R.drawable.ic_33);
        });
        et_password.setOnFocusChangeListener((v, hasFocus) -> {
            mLeftLogo.setImageResource(R.drawable.ic_22_hide);
            mRightLogo.setImageResource(R.drawable.ic_33_hide);
        });
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 如果用户名清空了 清空密码 清空记住密码选项
                et_password.setText("");
                if (s.length() > 0) {
                    // 如果用户名有内容时候 显示删除按钮
                    mDeleteUserName.setVisibility(View.VISIBLE);
                } else {
                    // 如果用户名有内容时候 显示删除按钮
                    mDeleteUserName.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    public void initToolBar() {
        mToolbar.setNavigationIcon(R.drawable.ic_cancle);
        mToolbar.setTitle("登录");
        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    @OnClick({R.id.btn_login, R.id.delete_username})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //登录
                boolean isNetConnected = CommonUtil.isNetworkAvailable(this);
                if (!isNetConnected) {
                    ToastUtil.ShortToast("当前网络不可用,请检查网络设置");
                    return;
                }
                login();
                break;
            case R.id.delete_username:
                // 清空用户名以及密码
                et_username.setText("");
                et_password.setText("");
                mDeleteUserName.setVisibility(View.GONE);
                et_username.setFocusable(true);
                et_username.setFocusableInTouchMode(true);
                et_username.requestFocus();
                break;
        }
    }


    private void login() {
        String name = et_username.getText().toString();
        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.ShortToast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.ShortToast("密码不能为空");
            return;
        }
        HashMap<String, Object> keys = new HashMap<>();
//        appkey=1d8b6e7d45233436&build=509000&mobi_app=android&platform=android&ts=1515334398&sign=57b9335a6e680b761a4719387b153931
        keys.put("appkey", ApiConstants.INSTANCE.getAPP_KEY());
        keys.put("build", ApiConstants.INSTANCE.getBUILD());
        keys.put("mobi_app", "android");
        keys.put("platform", "android");
        keys.put("ts", System.currentTimeMillis() / 1000);
        RetrofitHelper.INSTANCE.getAuthAPI().getKey(keys)
                .compose(bindToLifecycle())
                .flatMap(key -> {
                    //保存auth key到本地或仅在内存中
                    PreferenceUtil.put("auth_key", new Gson().toJson(key.getData()));
                    HashMap<String, Object> loginMap = new HashMap<>();
                    loginMap.put("appkey", ApiConstants.INSTANCE.getAPP_KEY());
                    loginMap.put("build", ApiConstants.INSTANCE.getBUILD());
                    loginMap.put("mobi_app", "android");
                    loginMap.put("password", "");
                    loginMap.put("platform", "android");
                    loginMap.put("ts", System.currentTimeMillis() / 1000);
                    loginMap.put("username", name);
                    return RetrofitHelper.INSTANCE.getAuthAPI().login(loginMap);
                })
                .doOnNext(oauthToken -> {
                    //save token
                    PreferenceUtil.put("auth_token", new Gson().toJson(oauthToken.getData()));
                })
                .doOnError(Throwable::printStackTrace)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(oauthToken -> {
                    //登录成功，go!
                    Log.v("TAG", "response == " + new Gson().toJson(oauthToken));
                    PreferenceUtil.putBoolean(ConstantUtil.KEY, true);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                });
    }
}
