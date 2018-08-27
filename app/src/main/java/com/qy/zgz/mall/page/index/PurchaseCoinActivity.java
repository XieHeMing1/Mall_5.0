package com.qy.zgz.mall.page.index;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.BaseActivity;
import com.qy.zgz.mall.Dbsql.DBDao;
import com.qy.zgz.mall.Dbsql.DBOutCoinRecord;
import com.qy.zgz.mall.Dbsql.DBReceiveMoneyRecord;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.SerialPortListener;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.Model.BuyCoins;
import com.qy.zgz.mall.Model.Cranemaapi;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.Model.PurchaseRecord;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.CoinTypeAdapter;
import com.qy.zgz.mall.adapter.ConsumptionListAdapter;
import com.qy.zgz.mall.adapter.PackageListAdapter;
import com.qy.zgz.mall.entities.CoinInfo;
import com.qy.zgz.mall.entities.GMSinfo;
import com.qy.zgz.mall.lcb_game.NumDanceActivity;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index_function.CustomerServiceDialog;
import com.qy.zgz.mall.page.money_purchase.purchase_record.PurchaseRecordAdapter;
import com.qy.zgz.mall.slot_machines.game.SlotMachinesActivity;
import com.qy.zgz.mall.utils.FileManager;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.QRBitmapUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.TimeUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.utils.UnityDialog;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.qy.zgz.mall.widget.MyTextView;
import com.qy.zgz.mall.widget.TisCashPayDialog;
import com.qy.zgz.mall.widget.TisDialog;
import com.qy.zgz.mall.widget.TisEditDialog;
import com.qy.zgz.mall.widget.TisOutCoinsDialog;
import com.youth.banner.Banner;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.common.Callback;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 */
public class PurchaseCoinActivity extends BaseActivity implements SerialPortListener {
    public static final String TAG = "PurchaseCoinActivity";
    @BindView(R.id.ban_main_banner)
    Banner mBanner;

    @BindView(R.id.iv_wx_qrcode)
    ImageView mIvWxQRCode;
    @BindView(R.id.rv_coin_type)
    RecyclerView mRvCoinList;
    //    @BindView(R.id.sdv_setting)
//    SimpleDraweeView mSdvSetting;
    @BindView(R.id.rl_purchase_coin)
    ImageView mRlPurchaseCoin;
    @BindView(R.id.rl_exchange_coin)
    ImageView mRlExchangeCoin;
    @BindView(R.id.rl_consumption_records)
    ImageView mRlConSumptionRecords;
    @BindView(R.id.rl_modify_password)
    ImageView mRlModifyPwd;
    @BindView(R.id.iv_logout)
    ImageView mIvLogout;
    @BindView(R.id.txt_timer_count)
    TextView mTvTimerCount;
    @BindView(R.id.sdv_customor_enquiry)
    ImageView mSdvCustomer;
    @BindView(R.id.btn_logout)
    Button mBtnLogout;
    ;

    /* 底部信息栏 */
    @BindView(R.id.tv_login_please)
    TextView mTvLoginPlease;
    @BindView(R.id.tv_vip_name)
    MyTextView mTvVipName;
    @BindView(R.id.tv_lottery)
    MyTextView mTvlottery;
    @BindView(R.id.tv_lottery_count)
    MyTextView mTvLotteryCount;
    @BindView(R.id.tv_game_coin)
    MyTextView mTvGameCoin;
    @BindView(R.id.tv_game_coin_count)
    MyTextView mTvGameCoinCount;


    @BindView(R.id.tv_purchase_coin_type)
    TextView mTvPurchaseTitle;
    @BindView(R.id.ll_consumption_record_layout)
    AutoLinearLayout mLlConsumptionRecordLayout;
    @BindView(R.id.rv_consumption)
    RecyclerView mRvConsumption;
    @BindView(R.id.v_buy_coins_clean_error)
    View mViewCleanError;

    /**
     * 修改密码布局
     */
    @BindView(R.id.ll_modify_pwd_layout)
    LinearLayout mLlModifyPwd;
    @BindView(R.id.et_pwdupdate_oldpwd)
    EditText mEtOldPwd;
    @BindView(R.id.et_pwdupdate_npwd)
    EditText mEtNewPwd;
    @BindView(R.id.et_pwdupdate_cpwd)
    EditText mEtConfirmPwd;
    @BindView(R.id.btn_pwdupdate_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_pwdupdate_confirm)
    Button mBtnConfirm;
    @BindView(R.id.tv_fg_buy_coins_notaocan_tis)
    TextView mTvNoPackageTip;
    @BindView(R.id.iv_setting)
    ImageView mIvSetting;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.iv_shopping_cart)
    ImageView mIvShoppingCart;


    private String mCinemaType;
    private String mCinemaid;
    private Cranemaapi mCranemaApi;
    private PackageListAdapter mAdapter;
    private List<BuyCoins> mCoinListData = new ArrayList<>();
    private ShoppingTimeCount mTimeCount;
    //检查微信登录handle
    private Handler wx_handle = new Handler();
    public static String wx_qrcode = "";

    ConsumptionListAdapter mConSumptionListAdapter;
    List<PurchaseRecord> mConsumptionRecordList;

    private String mConsumptionData = null;

    //判断是否可以收现金
    private boolean mCanReceiveMoney = false;
    //是否成功打开串口
    public boolean mIsSuccessOpenSerial = false;
    private List<BuyCoins> lastCashBuyCoins = new ArrayList<BuyCoins>(); //（现金购买时）需要更新的套餐信息

    private boolean isShowAutoBuy = false;//是否显示自由购买 ,1--显示，0--不显示

    @Override
    public void createView() {
        setContentView(R.layout.activity_purchase_coin);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState, @Nullable Intent intent) {
        mCinemaType = (String) SharePerferenceUtil.getInstance().getValue("typeId", "");
        mCinemaid = (String) SharePerferenceUtil.getInstance().getValue("cinemaid", "");
//        mSdvSetting.setImageURI(LocalDefines.getImgUriHead(this) + R.drawable.ic_setting);
        mTimeCount = new ShoppingTimeCount(PurchaseCoinActivity.this, 30000, 1000);
        mTimeCount.start();
        mTvShopName.setText(SharePerferenceUtil.getInstance().getValue("type_shop_name","").toString());
        initBanner();

        mViewCleanError.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new UnityDialog(PurchaseCoinActivity.this).setHint("是否清除出币故障？")
                        .setCancel("取消", null)
                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                            @Override
                            public void confirm(UnityDialog unityDialog, String content) {
                                kd.sp().bdCleanError();
                                kd.sp().bdCoinOuted();
                                kd.sp().setIsSuccessOutCoin(true);
                            }
                        });
                return false;
            }
        });
//        initCoinList();
//        initConsumptionRecordList();

        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {
            LocalDefines.sIsLogin = false;
        } else {
            LocalDefines.sIsLogin = true;
        }
        getPackageList();
        viewLongClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoginInfo();
        kd.sp().go(this);
        UpdateServerByLocalData();
        getGMSSettingsInfoList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止扫描
        VbarUtils.getInstance(this).stopScan();
        mTimeCount.cancel();
        mBaseActivityHandler.removeCallbacksAndMessages(null);
        wx_handle.removeCallbacksAndMessages(null);
    }

    //用户操作监听
    @Override
    public void onUserInteraction() {
        Constance.lastTouchTime = System.currentTimeMillis();
        //重新倒计时
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {
            mTimeCount.cancel();
        } else {
            mTimeCount.cancel();
            mTimeCount.start();
        }

        super.onUserInteraction();
    }

    private void initCoinList() {
        //测试用
//        List<CoinInfo> list = new ArrayList<CoinInfo>();
//        CoinInfo info = new CoinInfo();
//        info.setPrice(-1);
//        list.add(info);
//
//        info = new CoinInfo();
//        info.setPrice(10);
//        list.add(info);
//
//        info = new CoinInfo();
//        info.setPrice(20);
//        list.add(info);
//
//        info = new CoinInfo();
//        info.setPrice(50);
//        list.add(info);
//
//        info = new CoinInfo();
//        info.setPrice(100);
//        list.add(info);
//
//        info = new CoinInfo();
//        info.setPrice(150);
//        list.add(info);
//
//        info = new CoinInfo();
//        info.setPrice(5);
//        list.add(info);
//
//        info = new CoinInfo();
//        info.setPrice(10);
//        list.add(info);

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        mRvCoinList.setLayoutManager(manager);
//        mAdapter = new CoinTypeAdapter(this, mCoinListData, list);
        mAdapter = new PackageListAdapter(this, mCoinListData);
        mAdapter.OnClickListener(new PackageListAdapter.OnClickListener() {
            @Override
            public void OnClickListener(int position) {
//                ToastUtil.showToast(PurchaseCoinActivity.this, mCoinListData.get(position).getPrice() + "元");
                String typeid = SharePerferenceUtil.getInstance().getValue("typeId", "").toString();
                //欢乐熊版本
                if (typeid == "25" && !LocalDefines.sIsLogin) {
                    TisDialog dialog = new TisDialog(PurchaseCoinActivity.this).create().setMessage("请先登录!").show();
                    return;
                }

                boolean isSuccessOutCoin = kd.sp().getIsSuccessOutCoin();
                if (isSuccessOutCoin) {
                    TisDialog dialog = new TisDialog(PurchaseCoinActivity.this).create().setMessage("设备没币,请移步到其他机器!!").show();
                    return;
                }

                if (mIsSuccessOpenSerial) {
                    kd.sp().bdCoinOuted();
                    kd.sp().bdCleanError();
                    if (LocalDefines.sIsLogin && mCoinListData.get(position).getIsMember()) {
                        new TisDialog(PurchaseCoinActivity.this).create().setMessage("需要会员才能购买!").show();
                    }

                    switch (mCoinListData.get(position).getId()) {
                        case "-1":
                            new TisEditDialog(PurchaseCoinActivity.this).create().setEditType(InputType.TYPE_CLASS_NUMBER)
                                    .setMessage("请输入购买金额")
                                    .setNegativeButton(new TisEditDialog.NegativeButtonListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setPositiveButton(new TisEditDialog.PositiveButtonListener() {
                                @Override
                                public void onClick(View v, String input) {
                                    if (input != null && Integer.valueOf(input) > 0) {
                                        autoMathPackageListNoType(input);
                                    } else {
                                        new TisDialog(PurchaseCoinActivity.this).create().setMessage("金额不能为0或者空")
                                                .show();
                                    }
                                }
                            });
                            break;
                        default:
                            getPackageInfo(mCoinListData.get(position).getId(), position);
                            break;

                    }
                } else {
                    new TisDialog(PurchaseCoinActivity.this).create().setMessage("设备故障,请联系管理员!").show();
                }
            }
        });
        mRvCoinList.setAdapter(mAdapter);
    }

    private void initConsumptionRecordList() {
        if (mConSumptionListAdapter != null) {
            mConSumptionListAdapter.notifyDataSetChanged();
        } else {
            mConSumptionListAdapter = new ConsumptionListAdapter(PurchaseCoinActivity.this, mConsumptionRecordList);
            mRvConsumption.setLayoutManager(new LinearLayoutManager(PurchaseCoinActivity.this, LinearLayoutManager.VERTICAL, false));
            mRvConsumption.setAdapter(mConSumptionListAdapter);
        }
    }

    private void initBanner() {
        ArrayList<Integer> banList = new ArrayList<>();
        banList.add(R.drawable.banner);
        //banner设置
        mBanner.setDelayTime(10000);//设置轮播时间
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setImages(banList).start();

        long time = 0L;
        long lastGetBannerTime = (long)SharePerferenceUtil.getInstance().getValue(LocalDefines.LAST_GET_BANNER_TIME, time);
        Log.i(TAG, "time lastTime = " + lastGetBannerTime + " System.currentTimeMillis() = " + System.currentTimeMillis() + " System.currentTimeMillis() - lastGetBannerTime = " + (System.currentTimeMillis() - lastGetBannerTime)) ;

        String json =  SharePerferenceUtil.getInstance().getValue("cranemaapi", "").toString();
        Log.i(TAG, "local json = " + json);
        Gson gson = new Gson();
        if (json != null && !json.equals("")) {
            mCranemaApi = gson.fromJson(json, Cranemaapi.class);
            if (null != mCranemaApi.getImages() && !mCranemaApi.getImages().isEmpty() || System.currentTimeMillis() - lastGetBannerTime < 43200000) {
//                Log.i(TAG, "mCranemaApi.getVideodata() = " + mCranemaApi.getVideodata().get(0).getImages());
                if (mCranemaApi.getImages() != null) {
                    Log.i(TAG, "local has Images data");
//                    Log.i(TAG, "mCranemaApi.getVideodata().get(0).getImages() != null = " + mCranemaApi.getVideodata().get(0).getImages());
                    mBanner.update(mCranemaApi.getImages());
                    return;
                }else {
                    Log.i(TAG, "local has not Images data");
                    getCranemaFromServer();
                }
            }
        }else {
//            Log.i(TAG, "local has not data");
            getCranemaFromServer();
        }
    }

    private void viewLongClickListener() {
        mIvSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new UnityDialog(PurchaseCoinActivity.this).setHint("是否更换店铺")
                        .setCancel("取消", null)
                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                            @Override
                            public void confirm(UnityDialog unityDialog, String content) {
                                SharePerferenceUtil.getInstance().setValue("typeId", "");
                                SharePerferenceUtil.getInstance().setValue("cinemaid", "");
                                SharePerferenceUtil.getInstance().setValue("type_shop_name", "");
                                //清除机器场地BranchID
                                SharePerferenceUtil.getInstance().setValue(Constance.BranchID, "");
                                //清除机器ID
                                SharePerferenceUtil.getInstance().setValue(Constance.MachineID, "");
                                //清除机器VPN
                                SharePerferenceUtil.getInstance().setValue(Constance.Vpn, "");
                                //清除会员登录信息
                                SharePerferenceUtil.getInstance().setValue(Constance.member_Info, "");
                                //清除商城会员登录accessToken
                                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, "");
                                //清除商城会员登录shop_id
                                SharePerferenceUtil.getInstance().setValue(Constance.shop_id, "");
                                MyApplication.getInstance().restartApp();
                            }
                        });
                return false;
            }
        });
    }

    private void getCranemaFromServer() {
        Log.i(TAG, "getCranemaFromServer = " + mCinemaType + " mCinemaid = " + mCinemaid);
        NetworkRequest.getInstance().getCranemaapi(mCinemaType, mCinemaid, new NetworkCallback<Cranemaapi>() {
            @Override
            public void onSuccess(Cranemaapi data) {
                Log.i(TAG, "getCranemaFromServer data = " + data);
                mCranemaApi = data;
                Gson gson = new Gson();
                String json = gson.toJson(data);
                Log.i(TAG, "getCranemaFromServer json = " + json);
                SharePerferenceUtil.getInstance().setValue("cranemaapi", json);
                SharePerferenceUtil.getInstance().setValue(LocalDefines.LAST_GET_BANNER_TIME, System.currentTimeMillis());
//                initDot();
//                showData();
                if (TimeUtil.getInstance().isExceedTime()) {
                    FileManager.getInstance().deleteSvaeFile();
                    FileManager.getInstance().deleteAPKSvaeFile();
                }
                //下载图片
//                getImageList();
//                handler.removeCallbacks(getCranRunnable);
                if (null != mCranemaApi.getImages() && !mCranemaApi.getImages().isEmpty()) {
                    if (mCranemaApi.getImages() != null) {
                        mBanner.update(mCranemaApi.getImages());
                    } else {
                        Log.i(TAG, "服务器无VideoData返回");
                    }
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG, "getCranemaFromServer onFailure");
                ToastUtil.showToast(PurchaseCoinActivity.this, msg);
//                handler.postDelayed(getCranRunnable, 5000);
//                showData();
            }

            @Override
            public void onCompleted() {
//                showData();
                super.onCompleted();
                Log.i(TAG, "getCranemaFromServer onCompleted");
            }
        });
    }

    @OnClick({R.id.rl_purchase_coin, R.id.rl_exchange_coin, R.id.rl_consumption_records,
            R.id.rl_modify_password, R.id.btn_logout, R.id.iv_new_game, R.id.iv_vip_center,
            R.id.iv_purchase_coin, R.id.iv_exchange_mall, R.id.iv_lucky_lottery,
            R.id.sdv_customor_enquiry, R.id.btn_pwdupdate_cancel, R.id.btn_pwdupdate_confirm,
            R.id.iv_logout, R.id.tv_main_page})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_purchase_coin:
                showPurchaseCoinLayout();
                getPackageList();
                break;
            case R.id.rl_exchange_coin:
                showExchangeCoinLayout();
                getPackageList();
                break;
            case R.id.rl_consumption_records:
                showConsumptionLayout();
                break;
            case R.id.rl_modify_password:
                showModifyPwdLayout();
                break;
            case R.id.iv_logout:
            case R.id.btn_logout:
//                logout(this);
                VipLogout();
                showLoginInfo();
                break;
            case R.id.iv_new_game:
                intent.setClass(this, NumDanceActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_vip_center:
                intent.setClass(this, VIPCenterActivity.class);
//                intent.putExtra("cinemaType", mCinemaType);
//                intent.putExtra("cinemaid", mCinemaid);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_purchase_coin:
                ToastUtil.showToast(this, "已达该页");
                break;
            case R.id.iv_exchange_mall:
                intent.setClass(this, MallActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_lucky_lottery:
                intent.setClass(this, SlotMachinesActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.sdv_customor_enquiry:
                new CustomerServiceDialog(this).create().show();
                break;
            case R.id.btn_pwdupdate_cancel:
                clearEditText();
                break;
            case R.id.btn_pwdupdate_confirm:
                updatePwd(mEtOldPwd.getText().toString(), mEtNewPwd.getText().toString(), mEtConfirmPwd.getText().toString());
                break;
            case R.id.tv_main_page:
                intent.setClass(PurchaseCoinActivity.this, HomePageActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void showPurchaseCoinLayout() {
        mTvPurchaseTitle.setText("选择套餐");
        mRvCoinList.setVisibility(View.VISIBLE);
        mLlConsumptionRecordLayout.setVisibility(View.GONE);
        mLlModifyPwd.setVisibility(View.GONE);
    }

    private void showConsumptionLayout() {
        if (LocalDefines.sIsLogin) {
            mTvPurchaseTitle.setText("消费记录查询（一个月内）");
            mRvCoinList.setVisibility(View.GONE);
            mLlConsumptionRecordLayout.setVisibility(View.VISIBLE);
            getConsumptionRecord();
        } else {
            ToastUtil.showToast(this, "请先登录");
        }
    }

    private void showExchangeCoinLayout() {
        mTvPurchaseTitle.setText("提取游戏币");
        mRvCoinList.setVisibility(View.VISIBLE);
        mLlConsumptionRecordLayout.setVisibility(View.GONE);
        mLlModifyPwd.setVisibility(View.GONE);
    }

    private void showModifyPwdLayout() {
        if (LocalDefines.sIsLogin) {
            mTvPurchaseTitle.setText("修改密码");
            mRvCoinList.setVisibility(View.GONE);
            mLlConsumptionRecordLayout.setVisibility(View.GONE);
            mLlModifyPwd.setVisibility(View.VISIBLE);
        } else {
            ToastUtil.showToast(this, "请先登录");
        }
    }

    // 购物计时器
    static class ShoppingTimeCount extends CountDownTimer {
        WeakReference<PurchaseCoinActivity> mWeakReference;

        public ShoppingTimeCount(PurchaseCoinActivity purchaseCoinActivity1, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            mWeakReference = new WeakReference<>(purchaseCoinActivity1);
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            PurchaseCoinActivity purchaseCoinActivity = mWeakReference.get();
            if (purchaseCoinActivity != null) {
                purchaseCoinActivity.mTvPurchaseTitle.setText("选择套餐");
                purchaseCoinActivity.mRvCoinList.setVisibility(View.VISIBLE);
                purchaseCoinActivity.mLlConsumptionRecordLayout.setVisibility(View.GONE);
                purchaseCoinActivity.mTvTimerCount.setText("倒计时：00:00");
                //清除会员登录信息
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.member_Info, "");
                //清除商城会员登录accessToken
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.user_accessToken, "");
                //清除商城会员登录shop_id
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.shop_id, "");
                //重新初始化
                purchaseCoinActivity.onResume();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) { // 计时过程显示
            PurchaseCoinActivity purchaseCoinActivity = mWeakReference.get();
            if (purchaseCoinActivity != null) {
                if (millisUntilFinished < 10000) {
                    purchaseCoinActivity.mTvTimerCount.setText("倒计时：00:0" + millisUntilFinished / 1000);
                } else {
                    purchaseCoinActivity.mTvTimerCount.setText("倒计时：00:" + millisUntilFinished / 1000);
                }
            }
        }
    }

    /**
     * 开启登录识别扫描器
     */
    private void startLoginRecognitionScan() {
        try {
            //开启扫描器识别
            VbarUtils.getInstance(this)
                    .setScanResultExecListener(new VbarUtils.ScanResultExecListener() {
                        @Override
                        public void scanResultExec(String result) {
                            if (!TextUtils.isEmpty(result)
                                    && TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
//                                Log.i(TAG, "startLoginRecognitionScan result = " + result);
                                scanCardLogin(result);
                            }
                        }

                    }).getScanResult();

        } catch (Exception e) {

        }
    }

    /**
     * 会员扫卡登录
     */
    public void scanCardLogin(String scan_result) {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("CardSN", scan_result);
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        KProgressHUD dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show();
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetMemberInfoByCardNo, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "scanCardLogin onSuccessData result = " + result);
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200") &&
                        jsonResult.getAsJsonObject("Data").get("Status").toString().equals("0")) {
                    JsonObject data = jsonResult.getAsJsonObject("Data");

                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info, data.toString());

                    String Wid = data.get("WechatID").getAsString();
                    String Bid = SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
                    String Vpn = SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();

                    //显示登录信息
                    showLoginInfo();

                    if (!TextUtils.isEmpty(Bid)
                            && !TextUtils.isEmpty(Vpn)) {
                        //执行商城会员登录
                        userLogin(Wid, Bid, Vpn);
                    }

                    //登录提示
                    new TisDialog(PurchaseCoinActivity.this).create()
                            .setMessage("登录成功!").show();


                } else {
                    Toast.makeText(PurchaseCoinActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    startLoginRecognitionScan();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "scanCardLogin onError Throwable = " + ex);
                startLoginRecognitionScan();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }
        });
    }

    /**
     * 商城会员登录接口
     */
    private void userLogin(String wxopen_id, String branch_id, String vpn) {
        String MacineId = SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString();
        String Bname = SharePerferenceUtil.getInstance().getValue(Constance.BranchName, "").toString();
        if (TextUtils.isEmpty(MacineId) || TextUtils.isEmpty(Bname)) {
            return;
        }
        HashMap<String, String> map = new HashMap();
//        map.put("open_id","o4hYLwyyF2D0NDjO4aoSjvI47lL8");
//        map.put("branch_id","c5d96d6b-c8ae-48a0-a8a3-ad88edbcc2ab");
//        map.put("child_url","12341");
//        map.put("deviceid","1341234123");
//        map.put("branch_name","1234123");
        map.put("open_id", wxopen_id);
        map.put("branch_id", branch_id);
        map.put("child_url", vpn);
        map.put("deviceid", MacineId);
        map.put("branch_name", Bname);

        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString(), MemberInfo.class);

        if (null != memberInfo) {
            map.put("cust_id", memberInfo.getId());
            map.put("mobile", memberInfo.getPhone());
        } else {
            map.put("cust_id", "");
            map.put("mobile", "");
        }

        NetworkRequest.getInstance().userLogin(map, new NetworkCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject data) {
                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, data.get("accessToken").getAsString());
                SharePerferenceUtil.getInstance().setValue(Constance.shop_id, data.get("shop_id").getAsString());

            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    //显示或隐藏登录(登录了机台信息)
    private void showLoginInfo() {
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {
            LocalDefines.sIsLogin = false;
            mTimeCount.cancel();
            //未登录状态
            mTvLoginPlease.setVisibility(View.VISIBLE);
            mTvVipName.setVisibility(View.INVISIBLE);
            mTvlottery.setVisibility(View.INVISIBLE);
            mTvLotteryCount.setVisibility(View.INVISIBLE);
            mTvGameCoin.setVisibility(View.INVISIBLE);
            mTvGameCoinCount.setVisibility(View.GONE);
            mBtnLogout.setVisibility(View.GONE);
            mIvShoppingCart.setVisibility(View.GONE);

            showExchangeCoinLayout();
            mBaseActivityHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //开启登录识别扫描器
                    startLoginRecognitionScan();
                    //创建新的微信授权二维码
                    CreateScanCode(SharePerferenceUtil.getInstance()
                            .getValue(Constance.MachineID, "").toString());
                }
            }, 500);
        } else {
            LocalDefines.sIsLogin = true;
            mTimeCount.cancel();
            mTimeCount.start();
            //初始化登录信息
            String logininfo = SharePerferenceUtil.getInstance()
                    .getValue(Constance.member_Info, "").toString();
            MemberInfo loginJson = GsonUtil.Companion.jsonToObject(logininfo, MemberInfo.class);

            if (loginJson != null) {
                mTvLoginPlease.setVisibility(View.GONE);
                mTvVipName.setVisibility(View.VISIBLE);
                mTvlottery.setVisibility(View.VISIBLE);
                mTvLotteryCount.setVisibility(View.VISIBLE);
                mTvGameCoin.setVisibility(View.VISIBLE);
                mTvGameCoinCount.setVisibility(View.VISIBLE);
                mBtnLogout.setVisibility(View.VISIBLE);
                mIvShoppingCart.setVisibility(View.VISIBLE);
                try {
                    mTvLotteryCount.setText(loginJson.getTickets().substring(0, loginJson.getTickets().indexOf(".")));
                    mTvGameCoinCount.setText(loginJson.getCoins().substring(0, loginJson.getCoins().indexOf(".")));

                } catch (Exception e) {
                    mTvLotteryCount.setText(loginJson.getCoins());
                    mTvGameCoinCount.setText(loginJson.getTickets());
                }
                mTvVipName.setText(loginJson.getCustName());
            }

        }
    }

    /**
     * 生成授权微信登陆二维码
     */
    public void CreateScanCode(String MachineID) {
        if (TextUtils.isEmpty(MachineID)) {
            return;
        }
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("MachineID", MachineID);
        hashmap.put("MenuName", "扫码登录");
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.CreateScanCode, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200")) {
                    Log.i(TAG, "CreateScanCode result = " + result);
                    wx_qrcode = jsonResult.get("Data").getAsString();
                    if (!TextUtils.isEmpty(wx_qrcode)) {
                        try {
                            mIvWxQRCode.setImageBitmap(QRBitmapUtils.createQRCode(wx_qrcode, 450));
                        } catch (Exception e) {
                            Log.i(TAG, "CreateScanCode Exception = " + e.toString());
                        }

                    }
                    Log.i(TAG, "wx_qrcode result = " + wx_qrcode);
                    String TmpGuid = jsonResult.get("Data2").getAsString();
                    wx_handle.removeCallbacksAndMessages(null);
                    //开始循环接口，查看是否登录
                    wx_handle.post(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
                                authorizedLogin(TmpGuid);
                            }
                        }

                    });

                    //刷新会员界面
//                    manage.beginTransaction().replace(R.id.main_fragment_content,new MemberCenterFragment()).commitAllowingStateLoss();


                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 微信授权扫码登录
     */
    private void authorizedLogin(String TmpGuid) {
        if (!TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
            return;
        }
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("TempGuid", TmpGuid);
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetCustomerScanData, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200") &&
                        jsonResult.getAsJsonObject("Data").get("Status").toString().equals("0")) {
                    wx_qrcode = "";
                    JsonObject data = jsonResult.getAsJsonObject("Data");
                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info, data.toString());

                    String Wid = data.get("WechatID").getAsString();
                    String Bid = SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
                    String Vpn = SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();

                    //显示登录信息
                    showLoginInfo();

                    //登录商城
//                    userLogin(Wid,Bid,Vpn);
                    if (!TextUtils.isEmpty(Bid)
                            && !TextUtils.isEmpty(Vpn)) {
                        //执行商城会员登录
                        userLogin(Wid, Bid, Vpn);
                    }

                    //登录提示
                    new TisDialog(PurchaseCoinActivity.this).create()
                            .setMessage("登录成功!").show();


                } else {
                    wx_handle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            authorizedLogin(TmpGuid);
                        }

                    }, 2500);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                wx_handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        authorizedLogin(TmpGuid);
                    }

                }, 2500);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取会员消费记录
     */
    private void getConsumptionRecord() {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);

        if (memberInfo != null) {
            //本地有数据，网络获取
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("IntMonth", "1");
            hashmap.put("PageNum", "1");
            hashmap.put("PageRows", "20");
            hashmap.put("MemberID", memberInfo.getId());
            hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
            Log.i(TAG, "getConsumptionRecord memberInfo.getId() = " + memberInfo.getId());
            //临时数据，避免多次发送请求
            if (mConsumptionData == null) {
                HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetCustomerConsume, hashmap, new XutilsCallback<String>() {
                    @Override
                    public void onSuccessData(String result) {
                        JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                        if (jsonResult != null && jsonResult.has("return_Code") &&
                                jsonResult.get("return_Code").toString().equals("200")) {
                            Log.i(TAG, "getConsumptionRecord result = " + result);
                            //临时保存数据
                            mConsumptionData = jsonResult.get("Data").getAsJsonArray().toString();
                            mConsumptionRecordList = GsonUtil.Companion.jsonToList(jsonResult.get("Data").getAsJsonArray().toString(), PurchaseRecord.class);
                            initConsumptionRecordList();
                        } else {
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            } else {

            }
        } else {
            //本地没有数据
        }

    }

    /**
     * 修改密码
     *
     * @param oldPwd
     * @param newPwd
     * @param confirmPwd
     */
    private void updatePwd(String oldPwd, String newPwd, String confirmPwd) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        if (memberInfo != null) {
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("NewPassword", newPwd);
            hashmap.put("ConfirmPassword", confirmPwd);
            hashmap.put("OldPassword", oldPwd);
            hashmap.put("CustID", memberInfo.getId());
            hashmap.put("UserID", Constance.machineUserID);
            hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
            HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.UpdateCustomerPwd, hashmap, new XutilsCallback<String>() {
                @Override
                public void onSuccessData(String result) {
                    JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                    if (jsonResult != null && jsonResult.has("return_Code") &&
                            jsonResult.get("return_Code").toString().equals("200")) {
                        ToastUtil.showToast(PurchaseCoinActivity.this, "修改成功");
                    } else {
                        ToastUtil.showToast(PurchaseCoinActivity.this, jsonResult.get("result_Msg").toString());
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private void clearEditText() {
        mEtConfirmPwd.setText("");
        mEtNewPwd.setText("");
        mEtOldPwd.setText("");
    }

    private String StockBillID = "";

    /**
     * 本地出币记录更新数据库记录
     */
    private void UpdateServerByLocalData() {
        List<DBOutCoinRecord> outCoinRecordList = DBDao.getInstance().queryErrorBill();
        if (outCoinRecordList == null || outCoinRecordList.isEmpty()) {
            return;
        }
        ArrayList<HashMap<String, Object>> outList = new ArrayList<HashMap<String, Object>>();
        for (DBOutCoinRecord dbOutCoinRecord : outCoinRecordList) {
            HashMap<String, Object> outHashmap = new HashMap<String, Object>();
            outHashmap.put("StockBillID", dbOutCoinRecord.getStockBillID());
            outHashmap.put("OutCoins", dbOutCoinRecord.getOutcount());
            outList.add(outHashmap);
        }

        HashMap<String, String> hashmap = new HashMap<String, String>();

        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashmap.put("LocalData", GsonUtil.Companion.objectToJson(outList));
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.UpdateServerByLocalData, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "UpdateServerByLocalData result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    //修改本地出币数据状态
                    ArrayList<String> stockBillIDList = new ArrayList<>();
                    for (HashMap<String, Object> hashmap : outList) {
                        stockBillIDList.add(hashmap.get("StockBillID").toString());
                    }

                    DBDao.getInstance().updateStateOutCoinsRecord(stockBillIDList);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "UpdateServerByLocalData onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 存入本地数据库（现金数据）
     */
    private int saveLocalCashRecord(double money, int localId) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        DBReceiveMoneyRecord moneyRecord = new DBReceiveMoneyRecord();
        moneyRecord.setId(localId);
        moneyRecord.setMoney(money);
        moneyRecord.setIsError(1);
        moneyRecord.setClassId(SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "").toString());
        moneyRecord.setClassTime(SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "").toString());
        if (memberInfo != null) {
            moneyRecord.setCustID(memberInfo.getId());
            moneyRecord.setCustName(memberInfo.getCustName());
        } else {
            moneyRecord.setCustID(Constance.machineFLTUserID);
            moneyRecord.setCustName("大众会员");
        }
        return DBDao.getInstance().saveOrUpdateCashRecord(moneyRecord);
    }

    /**
     * 获取一体机参数
     */
    private void getGMSSettingsInfoList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetGMSSettingsInfoList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "getGMSSettingsInfoList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    Log.i(TAG, "getGMSSettingsInfoList onSuccess = ");
                    String data = jsonObject.get("Data").getAsJsonArray().toString();
                    Log.i(TAG, "getGMSSettingsInfoList data = " + data);
                    List<GMSinfo> gmSinfos = GsonUtil.Companion.jsonToList(data, GMSinfo.class);
                    Log.i(TAG, "getGMSSettingsInfoList gmSinfos size = " + gmSinfos.size());
                    for (GMSinfo gmSinfo : gmSinfos) {
                        if (gmSinfo.getSettingKey().equals("GMSGetCoinLimit")) {
                            if (TextUtils.isEmpty(gmSinfo.getValue())) {
                                Constance.maxOutCoinValue = 200;
                            } else {
                                Constance.maxOutCoinValue = Integer.valueOf(gmSinfo.getValue());
                            }
                        }

                        if (gmSinfo.getSettingKey().equals("GMSAutoSale")) {
                            if (TextUtils.isEmpty(gmSinfo.getValue())) {
                                isShowAutoBuy = false;
                            } else {
                                if (gmSinfo.getValue().equals("1")) {
                                    isShowAutoBuy = true;
                                } else {
                                    isShowAutoBuy = false;
                                }
                            }
                        }

                    }
                } else {
                    Constance.maxOutCoinValue = 200;
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "getGMSSettingsInfoList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 存入本地数据库（出币数据）
     */
    private void saveLocalOutCoinRecord(int count) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        //存入本地数据库
        DBOutCoinRecord outcointRecord = new DBOutCoinRecord();
        outcointRecord.setOutcount(count);
        outcointRecord.setIsError(1);
        outcointRecord.setStockBillID(StockBillID);
        if (memberInfo != null) {
            outcointRecord.setCustID(memberInfo.getId());
            outcointRecord.setCustName(memberInfo.getCustName());
        } else {
            outcointRecord.setCustID(Constance.machineFLTUserID);
            outcointRecord.setCustName("大众会员");
        }
        outcointRecord.setClassId(SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "").toString());
        outcointRecord.setClassTime(SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "").toString());
        DBDao.getInstance().saveOutCoinsRecord(outcointRecord);
    }

    /**
     * SerialPortListener 接口方法
     *
     * @param count
     */
    @Override
    public void onCoinOuting(int count) {
        //存入本地数据
        saveLocalOutCoinRecord(count);
        if (mDialogOutCoins != null) {
            mDialogOutCoins.dismiss();
        }
    }

    @Override
    public void onCoinOutSuccess(int count) {
        //存入本地数据
        saveLocalOutCoinRecord(count);
        if (mDialogOutCoins != null) {
            mDialogOutCoins.showContiune(View.GONE);
        }
        takeUpdateOutCoinLog(count);
        closeOutCoinDialog();
    }

    @Override
    public void onCoinOutFail(int outCount, int count, String errorCode) {
        if (count >= 0) {
            //存入本地数据
            saveLocalOutCoinRecord(count);
            takeFailUpdateOutCoinLog(outCount);
            if (mDialogOutCoins != null) {
                mDialogOutCoins.showContiune(View.GONE);
                mDialogOutCoins.showBug(View.VISIBLE);
            }
        }
    }

    @Override
    public void onReceivedMomey(int amount, String macType) {
        if (mCanReceiveMoney && mIsSuccessOpenSerial && kd.sp().getIsSuccessOutCoin()) {
            if (mDialogCash != null && mDialogCash.isShowing()) {
                lastCashBuyCoins.clear();

                try {
                    if (Double.valueOf(mDialogCash.getShouldPrice()) <= 0) {
                        kd.sp().sendOutMomeyCmd(macType);
                        mDialogCash.dismiss();
                        return;
                    }
                } catch (Exception e) {
                    kd.sp().sendOutMomeyCmd(macType);
                    mDialogCash.dismiss();
                    return;
                }

                mDialogCash.freshCountDown();
                //判断是否超出应收金额
                Double count = Double.valueOf(mDialogCash.getHadPrice()) + amount;
                if (count > Double.valueOf(mDialogCash.getShouldPrice())) {
                    autoMathPackageList(count.toString(), macType);
                } else {
                    //收钱指令
                    kd.sp().sendGetMomeyCmd(macType);
                }
            } else {
                //退钱指令
                kd.sp().sendOutMomeyCmd(macType);
            }
        } else {
            if (!mCanReceiveMoney) {
                new TisDialog(this).create()
                        .setMessage("纸钞机异常，请联系管理员").show();
            } else {
                new TisDialog(this).create()
                        .setMessage("币斗没币或异常，请联系管理员").show();
            }
        }
    }

    @Override
    public void onReceivedMomeySuccess(int amount, String macType) {
        Log.e(TAG, "onReceivedMomeySuccess");
        if (mDialogCash != null && mDialogCash.isShowing()) {

        }
        if (lastCashBuyCoins != null && !lastCashBuyCoins.isEmpty()) {
            Log.e("TAG", "onReceivedMomeySuccess 超出刷新");
            ArrayList<BuyCoins> list = new ArrayList<>();
            list.addAll(lastCashBuyCoins);
            mDialogCash.setInfo(list);
        }

        Double count = Double.valueOf(mDialogCash.getHadPrice()) + amount;
        try {
            mDialogCash.setHadPrice(count.toString());
        } catch (Exception e) {
            mDialogCash.setHadPrice(String.valueOf(amount));
        }

        //存入本地数据

        int id = saveLocalCashRecord(Double.valueOf(mDialogCash.getHadPrice()), mDialogCash.getLocalId());
        mDialogCash.setLocalId(id);

        if (Double.valueOf(mDialogCash.getHadPrice()) >= Double.valueOf(mDialogCash.getShouldPrice())) {
            mDialogCash.showOutSaveButton();
        }
    }

    @Override
    public void onReceivedMomeyFail(String macType) {
        if (mDialogCash != null && mDialogCash.isShowing()) {
            mDialogCash.freshCountDown();
        }
    }

    @Override
    public void onSendCompleteData(byte[] bytes) {

    }

    @Override
    public void onSerialPortOpenFail(File file) {

    }

    @Override
    public void onSerialPortOpenSuccess(File file) {

    }

    @Override
    public void onMachieConnectedSuccess(String device) {
        //币斗
        Log.e("MADV", " onMachieConnectedSuccess device " + device.toString());
        if (device.contains(kd.sp().getDevice("3"))) {
            Log.e("MA", "su");
            mIsSuccessOpenSerial = true;
        }
        //纸币机
        if (device.contains(kd.sp().getDevice("1")) || device.contains(kd.sp().getDevice("2"))) {
            mCanReceiveMoney = true;
            try {
                kd.sp().colseBanknote();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onMachieCommectedFail(String device) {
        //币斗
        Log.e("MADV", " onMachieCommectedFail device " + device.toString());
        if (device.contains(kd.sp().getDevice("3"))) {
            mIsSuccessOpenSerial = false;
            Log.e("MA", "FA");
        }
        //纸币机
        if (device.contains(kd.sp().getDevice("1")) || device.contains(kd.sp().getDevice("2"))) {
            mCanReceiveMoney = false;
        }
    }

    private TisOutCoinsDialog mDialogOutCoins;

    /**
     * 显示出票界面
     */
    private void showOutCoinsDialog(int num) {
        if (num > 200) {
            mDialogOutCoins = new TisOutCoinsDialog(this).create().setTotalNum(num + "")
                    .setNum("0").showContiune(View.VISIBLE).show();
        } else {
            mDialogOutCoins = new TisOutCoinsDialog(this).create().setTotalNum(num + "")
                    .setNum("0").show();
        }

        kd.sp().bdSendOutCoin(num, kd.sp().getDevice("3"), 1);
    }

    /**
     * 关闭显示出币界面
     */
    private void closeOutCoinDialog() {
//        exitMember()
        mBaseActivityHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDialogOutCoins != null) {
                    mDialogOutCoins.dismiss();
                }
            }
        }, 3000);
    }


    /**
     * 提币更新数据接口
     *
     * @param num
     */
    private void takeUpdateOutCoinLog(int num) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("OutCoins", num + "");
        hashMap.put("StockBillID", StockBillID);

        if (null == memberInfo) {
            hashMap.put("CustID", Constance.machineFLTUserID);
            hashMap.put("IsSaveCard", String.valueOf("false"));
        } else {
            hashMap.put("CustID", memberInfo.getId());
            hashMap.put("IsSaveCard", String.valueOf("true"));
        }
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.UpdateOutCoinLog, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "takeUpdateOutCoinLog result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    ArrayList<String> sbillList = new ArrayList<String>();
                    sbillList.add(StockBillID);
                    DBDao.getInstance().updateStateOutCoinsRecord(sbillList);
                } else {
                    Constance.maxOutCoinValue = 200;
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "takeUpdateOutCoinLog onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {
                StockBillID = "";
            }
        });
    }

    /**
     * 提币失败，更新数据接口
     *
     * @param num
     */
    private void takeFailUpdateOutCoinLog(int num) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("OutCoins", num + "");
        hashMap.put("StockBillID", StockBillID);

        if (null == memberInfo) {
            hashMap.put("CustID", Constance.machineFLTUserID);
            hashMap.put("IsSaveCard", String.valueOf("false"));
        } else {
            hashMap.put("CustID", memberInfo.getId());
            hashMap.put("IsSaveCard", String.valueOf("true"));
        }
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.UpdateOutCoinLog, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "takeFailUpdateOutCoinLog result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    ArrayList<String> sbillList = new ArrayList<String>();
                    sbillList.add(StockBillID);
                    DBDao.getInstance().updateStateOutCoinsRecord(sbillList);

                    if (LocalDefines.sIsLogin) {
                        kd.sp().bdCleanError();
                        if (mDialogOutCoins != null) {
                            mDialogOutCoins.setBugText("机器没币，未出的币已经返还卡中！");
                        }
                        VipLogout();
                        showLoginInfo();

                        mBaseActivityHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mDialogOutCoins != null) {
                                    mDialogOutCoins.dismiss();
                                }
                            }
                        }, 8000);
                    } else {
                        if (mDialogOutCoins != null && !mDialogOutCoins.isShowing()) {
                            mDialogOutCoins.setBugText("机器没币，请联系管理员补币");
                            mDialogOutCoins.showClose();
                        }
                    }
                } else {
                    if (mDialogOutCoins != null && !mDialogOutCoins.isShowing()) {
                        mDialogOutCoins.setBugText("机器故障或没币，请联系管理员补币");
                        mDialogOutCoins.showClose();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "takeFailUpdateOutCoinLog onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {
                StockBillID = "";
            }
        });
    }

    private TisCashPayDialog mDialogCash;

    //显示纸币付款提示窗
    private void showCashPayDialog(ArrayList<BuyCoins> buycoinsList) {
        mDialogCash = new TisCashPayDialog(this).create()
                .setInfo(buycoinsList)
                .show();
    }

    /**
     * 自动匹配套餐
     *
     * @param price
     * @param macType
     */
    private void autoMathPackageList(String price, String macType) {
        KProgressHUD dialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PackageType", "Pa01");
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());

        if (null == memberInfo) {
            hashMap.put("CustID", Constance.machineFLTUserID);
        } else {
            hashMap.put("CustID", memberInfo.getId());
        }

        hashMap.put("Amount", price);
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.AutoMathPackageList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "autoMathPackageList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    lastCashBuyCoins = GsonUtil.Companion.jsonToList(jsonObject.get("Data").getAsJsonArray().toString(), BuyCoins.class);
                    //收钱指令
                    kd.sp().sendGetMomeyCmd(macType);
                } else {
                    //退钱指令
                    kd.sp().sendOutMomeyCmd(macType);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "autoMathPackageList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 自动匹配套餐
     *
     * @param price
     */
    public void autoMathPackageListNoType(String price) {
        KProgressHUD dialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PackageType", "Pa01");
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());

        if (null == memberInfo) {
            hashMap.put("CustID", Constance.machineFLTUserID);
        } else {
            hashMap.put("CustID", memberInfo.getId());
        }

        hashMap.put("Amount", price);
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.AutoMathPackageList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "autoMathPackageList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    lastCashBuyCoins = GsonUtil.Companion.jsonToList(jsonObject.get("Data").getAsJsonArray().toString(), BuyCoins.class);
                    Log.i(TAG, "getPackageList mCoinListData size = " + mCoinListData.size());
                    //收钱指令
//                    kd.sp().sendGetMomeyCmd(macType);
                    //TODO 跳转至 BuyCoinsDetailFragment，参考里面的代码

                } else {
                    //退钱指令
//                    kd.sp().sendOutMomeyCmd(macType);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "autoMathPackageList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 获取套餐列表
     */
    private void getPackageList() {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PackageType", "Pa01");
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashMap.put("PageIndex", "1");

        if (null == memberInfo || TextUtils.isEmpty(memberInfo.getId())) {
//            hashMap.put("CustID", Constance.machineFLTUserID);
        } else {
            hashMap.put("CustID", memberInfo.getId());
        }

        hashMap.put("PageNum", "10");
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetPackageList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "getPackageList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    mCoinListData = GsonUtil.Companion.jsonToList(jsonObject.get("Data").getAsJsonArray().toString(), BuyCoins.class);
                    Log.i(TAG, "getPackageList mCoinListData size = " + mCoinListData.size());
                    //自由购买
                    if (mCoinListData != null && mCoinListData.size() > 0) {
                        initCoinList();
                        mTvNoPackageTip.setVisibility(View.GONE);
                    } else {
                        mTvNoPackageTip.setVisibility(View.VISIBLE);
                    }

                } else {

                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "getPackageList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取套餐信息
     */
    public void getPackageInfo(String pid, int position) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PackageID", pid);

        if (null == memberInfo /*|| TextUtils.isEmpty(memberInfo.getId())*/) {
//            hashMap.put("CustID", Constance.machineFLTUserID);
            if (LocalDefines.sIsLogin) {
                hashMap.put("CustID", memberInfo.getId());
            } else {
                hashMap.put("CustID", Constance.machineFLTUserID);
            }
        }

        hashMap.put("packageQty", "1");
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetPackageSaleInfo, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "getPackageList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    mCoinListData = GsonUtil.Companion.jsonToList(jsonObject.get("Data").getAsJsonArray().toString(), BuyCoins.class);
                    Log.i(TAG, "getPackageList mCoinListData size = " + mCoinListData.size());

                    //TODO 跳转至 BuyCoinsDetailFragment，参考里面的代码
                } else {

                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "getPackageList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
