package com.daikuanchaoshi.daikuanguowai.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.daikuanchaoshi.daikuanguowai.R;
import com.daikuanchaoshi.daikuanguowai.commt.BaseAct;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.getSmsVerifyBean;
import com.daikuanchaoshi.daikuanguowai.ui.act.bean.publicForgetBean;
import com.daikuanchaoshi.daikuanguowai.ui.http.HttpHelper;
import com.daikuanchaoshi.daikuanguowai.ui.view.MyTimerText;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.lykj.aextreme.afinal.utils.MyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 找回密码
 */
public class Act_FindPassword extends BaseAct {
    @BindView(R.id.et_phonenumber)
    EditText etPhonenumber;
    @BindView(R.id.et_validatecode)
    EditText etValidatecode;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirmpassword)
    EditText etConfirmpassword;
    @BindView(R.id.btn_getcode)
    MyTimerText btnGetcode;

    @Override
    public int initLayoutId() {
        return R.layout.act_findpassword;
    }

    @Override
    public void initView() {
        hideHeader();
        //绑定初始化ButterKnife
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
    }

    @Override
    public void initData() {

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onNoInterNet() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_exit, R.id.btn_getcode, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                finish();
                break;
            case R.id.btn_getcode:
                getCertificateByOldPhone();
                break;
            case R.id.btn_register:
                publicForget();
                break;
        }
    }

    /**
     * 验证码
     */
    public void getCertificateByOldPhone() {
        if (etPhonenumber.getText().toString().length() != 11) {
            MyToast.show(this, "Masukkan 11 digit nomor ponsel");
            return;
        }
        loding.show();
        HttpHelper.getSmsVerify(this, etPhonenumber.getText().toString(), "3", new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                getSmsVerifyBean getSmsVerifyBean = gson.fromJson(succeed, com.daikuanchaoshi.daikuanguowai.ui.act.bean.getSmsVerifyBean.class);
                if (getSmsVerifyBean.getStatus() == 1) {
                    MyToast.show(Act_FindPassword.this, "Kode verifikasi sukses dikirim");
                    btnGetcode.setBackEndTime(() -> {
                        btnGetcode.setSelected(true);
                    });
                    btnGetcode.setSelected(false);
                    btnGetcode.start();
                }
            }

            @Override
            public void onError(String error) {
                loding.dismiss();
                MyToast.show(context, error);
            }
        });
    }

    /**
     * 找回密码
     */
    public void publicForget() {
        if (etPhonenumber.getText().toString().length() != 11) {
            MyToast.show(this, "请输入11位手机号码！");
            return;
        }
        if (etValidatecode.getText().toString().length() != 6) {
            MyToast.show(this, "Silahkan masukkan kode verifikasi yang benar");
            return;
        }
        if (!etPassword.getText().toString().equals(etConfirmpassword.getText().toString())) {
            MyToast.show(this, "Dua kali inkonsistensi input password");
            return;
        }
        loding.show();
        HttpHelper.publicForget(this, etPhonenumber.getText().toString(), etPassword.getText().toString(), etValidatecode.getText().toString(), new HttpHelper.HttpUtilsCallBack<String>() {
            @Override
            public void onFailure(String failure) {
                MyToast.show(context, failure);
                loding.dismiss();
            }

            @Override
            public void onSucceed(String succeed) {
                loding.dismiss();
                Gson gson = new Gson();
                publicForgetBean getSmsVerifyBean = gson.fromJson(succeed, publicForgetBean.class);
                if (getSmsVerifyBean.getStatus() == 1) {
                    MyToast.show(Act_FindPassword.this, "Pengubahan sandi berhasil. ");
                    Intent intent = new Intent();
                    intent.putExtra("phone", etPhonenumber.getText().toString());
                    intent.putExtra("psd", etPassword.getText().toString());
                    setResult(10, intent);
                    finish();
                }
            }
            @Override
            public void onError(String error) {
                loding.dismiss();
                MyToast.show(context, error);
            }
        });
    }


}
