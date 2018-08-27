package com.qy.zgz.mall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.HomePageActivity;
import com.qy.zgz.mall.page.login.LoginActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.qy.zgz.mall.widget.TisDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends RxAppCompatActivity {

    protected Unbinder mUnbinder;

    protected Handler mBaseActivityHandler;

    /**
     * 初始化ContentView
     */
    public abstract void createView();

    public abstract void afterCreate(@Nullable Bundle savedInstanceState,
                                     @Nullable Intent intent);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityManager.getActivityManager().addActivity(this);
        MyApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.i(this.getClass().getSimpleName(), " onCreate invoke");
        createView();
        mUnbinder = ButterKnife.bind(this);
        mBaseActivityHandler = new BaseActivityHandler(this);
        afterCreate(savedInstanceState, getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startLoginRecognitionScan();
    }

    public void logout(Context context) {
//        ActivityManager.getActivityManager().finishAllActivity();
        MyApplication.getInstance().finishActivity();
        SharePerferenceUtil.getInstance().setValue("typeId", "");
        SharePerferenceUtil.getInstance().setValue("cinemaid", "");
        Intent intent = new Intent(context, LoginActivity.class);

        context.startActivity(intent);
    }

    private static class BaseActivityHandler extends Handler {
        private WeakReference<BaseActivity> mActivityWeakReference;

        public BaseActivityHandler(BaseActivity baseActivity) {
            mActivityWeakReference = new WeakReference<BaseActivity>(baseActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity baseActivity = mActivityWeakReference.get();
            if (baseActivity != null && !baseActivity.isFinishing()) {
                baseActivity.handleMessage(msg);
            }
        }
    }

    /**
     * 处理 Handler 发送过来的消息
     *
     * @param msg
     */
    protected void handleMessage(Message msg) {

    }

    public void VipLogout() {
        SharePerferenceUtil.getInstance().setValue(Constance.member_Info, "");
        //清除商城会员登录accessToken
        SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, "");
        //清除商城会员登录shop_id
        SharePerferenceUtil.getInstance().setValue(Constance.shop_id, "");
        LocalDefines.sIsLogin = false;

        ToastUtil.showToast(this, "用户退出");
    }

    /**
     * 开启登录识别扫描器
     */
//    public void startLoginRecognitionScan() {
//        Log.i(this.getClass().getSimpleName(), "startLoginRecognitionScan");
//        try {
//            //开启扫描器识别
//            VbarUtils.getInstance(this)
//                    .setScanResultExecListener(new VbarUtils.ScanResultExecListener() {
//                        @Override
//                        public void scanResultExec(String result) {
//                            if (!TextUtils.isEmpty(result)
//                                    && TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
//                                Log.i(this.getClass().getSimpleName(), "startLoginRecognitionScan result = " + result);
//                                scanCardLogin(result);
//                            }
//                        }
//
//                    }).getScanResult();
//
//        } catch (Exception e) {
//
//        }
//    }
//
//    /**
//     * 会员扫卡登录
//     */
//    public void scanCardLogin(String scan_result) {
//        HashMap<String, String> hashmap = new HashMap<String, String>();
//        hashmap.put("CardSN", scan_result);
//        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
//        KProgressHUD dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("请稍后...").show();
//        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetMemberInfoByCardNo, hashmap, new XutilsCallback<String>() {
//            @Override
//            public void onSuccessData(String result) {
//                Log.i(this.getClass().getSimpleName(), "scanCardLogin onSuccessData result = " + result);
//                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
//                if (jsonResult.has("return_Code") &&
//                        jsonResult.get("return_Code").toString().equals("200") &&
//                        jsonResult.getAsJsonObject("Data").get("Status").toString().equals("0")) {
//                    JsonObject data = jsonResult.getAsJsonObject("Data");
//
//                    //临时保存会员信息
//                    SharePerferenceUtil.getInstance()
//                            .setValue(Constance.member_Info, data.toString());
//
//                    String Wid = data.get("WechatID").getAsString();
//                    String Bid = SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
//                    String Vpn = SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();
//
//                    //显示登录信息
//                    initLoginInfo();
//
//                    if (!TextUtils.isEmpty(Bid)
//                            && !TextUtils.isEmpty(Vpn)) {
//                        //执行商城会员登录
//                        userLogin(Wid, Bid, Vpn);
//                    }
//
//                    //登录提示
//                    new TisDialog(BaseActivity.this).create()
//                            .setMessage("登录成功!").show();
//
//                } else {
//                    Toast.makeText(BaseActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                    startLoginRecognitionScan();
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                Log.i(this.getClass().getSimpleName(), "scanCardLogin onError Throwable = " + ex);
//                startLoginRecognitionScan();
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//                if (dia != null && dia.isShowing()) {
//                    dia.dismiss();
//                }
//            }
//        });
//    }
//
//    /**
//     * 商城会员登录接口
//     */
//    public void userLogin(String wxopen_id, String branch_id, String vpn) {
//        String MacineId = SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString();
//        String Bname = SharePerferenceUtil.getInstance().getValue(Constance.BranchName, "").toString();
//        if (TextUtils.isEmpty(MacineId) || TextUtils.isEmpty(Bname)) {
//            return;
//        }
//        HashMap<String, String> map = new HashMap();
////        map.put("open_id","o4hYLwyyF2D0NDjO4aoSjvI47lL8");
////        map.put("branch_id","c5d96d6b-c8ae-48a0-a8a3-ad88edbcc2ab");
////        map.put("child_url","12341");
////        map.put("deviceid","1341234123");
////        map.put("branch_name","1234123");
//        map.put("open_id", wxopen_id);
//        map.put("branch_id", branch_id);
//        map.put("child_url", vpn);
//        map.put("deviceid", MacineId);
//        map.put("branch_name", Bname);
//
//        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance()
//                .getValue(Constance.member_Info, "").toString(), MemberInfo.class);
//
//        if (null != memberInfo) {
//            map.put("cust_id", memberInfo.getId());
//            map.put("mobile", memberInfo.getPhone());
//        } else {
//            map.put("cust_id", "");
//            map.put("mobile", "");
//        }
//
//        NetworkRequest.getInstance().userLogin(map, new NetworkCallback<JsonObject>() {
//
//            @Override
//            public void onSuccess(JsonObject data) {
//                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, data.get("accessToken").getAsString());
//                SharePerferenceUtil.getInstance().setValue(Constance.shop_id, data.get("shop_id").getAsString());
//
//            }
//
//            @Override
//            public void onFailure(int code, String msg) {
//
//            }
//        });
//    }

}
