/*
 * *********************************************************
 *   author   colin
 *   company  fosung
 *   email    wanglin2046@126.com
 *   date     17-4-7 上午11:56
 * ********************************************************
 */

package com.zcolin.tpartydemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.baidu.location.BDLocation;

import com.tyr.libbaidumaplocation.BaiduMapLocationUtil;
import com.zcolin.frame.app.BaseFrameActivity;
import com.zcolin.frame.app.FramePathConst;
import com.zcolin.frame.permission.PermissionHelper;
import com.zcolin.frame.permission.PermissionsResultAction;
import com.zcolin.frame.util.FileUtil;
import com.zcolin.frame.util.ToastUtil;
import com.zcolin.gui.ZAlert;
import com.zcolin.gui.ZConfirm;
import com.zcolin.gui.ZDialog;
import com.zcolin.libamaplocation.LocationUtil;

import java.util.ArrayList;

import cn.sharesdk.util.ShareSocial;
import yilong.cn.com.jpushparty.ExampleUtil;
import yilong.cn.com.unionpay.UnionPayClassUtils;

public class MainActivity extends BaseFrameActivity implements  View.OnClickListener {

    public static boolean isForeground = false;
    private LinearLayout llContent;
    private ArrayList<Button> listButton = new ArrayList<>();
    private MainActivity mActivity;
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.zolin.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String ACTION_NOTIFICATION_OPENED = "com.zolin.jpushdemo.NOTIFICATION_OPENED";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        init();
        registerMessageReceiver();
    }
    private void init() {
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        listButton.add(addButton("高德地图定位"));
        listButton.add(addButton("百度地图定位"));
//        listButton.add(addButton("腾讯地图定位"));
        listButton.add(addButton("分享"));
        listButton.add(addButton("分享图片"));
        listButton.add(addButton("银联卡绑定支付"));
        for (Button btn : listButton) {
            btn.setOnClickListener(this);
        }
    }


    private void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    /**
     *  内容接收者
     */
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
                else if(ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
                    Toast.makeText(context,"打开链接",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context, TestActivity.class);
                    i.putExtras(intent.getExtras());
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
					context.startActivity(i);
                }
            } catch (Exception e){
            }
        }
    }


    private void setCostomMsg(String msg){
        if (null != msg) {
            Toast.makeText(this,"接收到："+msg,Toast.LENGTH_SHORT).show();
        }
    }

    private Button addButton(String text) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button button = new Button(mActivity);
        button.setText(text);
        button.setGravity(Gravity.CENTER);
        button.setAllCaps(false);
        llContent.addView(button, params);
        return button;
    }

    private void amapLocation() {
        PermissionHelper.requestLocationPermission(mActivity, new PermissionsResultAction() {
            @Override
            public void onGranted() {

                final LocationUtil location = new LocationUtil(mActivity);
                location.startLocation(new LocationUtil.OnGetLocation() {

                    @Override
                    public void getLocation(AMapLocation location) {
				        /*设置位置描述*/
                        String desc = null;
                        Bundle locBundle = location.getExtras();
                        if (locBundle != null) {
                            desc = locBundle.getString("desc");
                        }
                        new ZAlert(mActivity).setMessage(locBundle == null ? location.getCity() + location.getDistrict() : desc)
                                             .show();
                    }

                    @Override
                    public void locationFail() {
                        ZConfirm dlg = new ZConfirm(mActivity);
                        dlg.setTitle("定位失败, 是否尝试再次定位？")
                           .addSubmitListener(new ZDialog.ZDialogSubmitInterface() {

                               @Override
                               public boolean submit() {
                                   amapLocation();
                                   return true;
                               }
                           });
                        dlg.addCancelListener(new ZDialog.ZDialogCancelInterface() {
                            @Override
                            public boolean cancel() {
                                return true;
                            }
                        });
                        dlg.show();
                    }
                });
            }
            @Override
            public void onDenied(String permission) {
                ToastUtil.toastShort("请授予本程序[定位]和[写SD卡权限]");
            }
        });
    }

    private void baiduMaplocation() {
        PermissionHelper.requestPermission(mActivity, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                final BaiduMapLocationUtil location = new BaiduMapLocationUtil(mActivity);
                location.startLocation(new BaiduMapLocationUtil.OnGetLocation() {
                    @Override
                    public void getLocation(BDLocation location) {
                        if (location != null) {
                            new ZAlert(mActivity).setMessage(location.getAddrStr())
                                                 .show();
                        }
                    }
                    @Override
                    public void locationFail() {
                        ZConfirm dlg = new ZConfirm(mActivity);
                        dlg.setTitle("定位失败, 是否尝试再次定位？")
                           .addSubmitListener(new ZDialog.ZDialogSubmitInterface() {

                               @Override
                               public boolean submit() {
                                   baiduMaplocation();
                                   return true;
                               }
                           });
                        dlg.addCancelListener(new ZDialog.ZDialogCancelInterface() {
                            @Override
                            public boolean cancel() {
                                return true;
                            }
                        });
                        dlg.show();
                    }
                });
            }

            @Override
            public void onDenied(String permission) {
                ToastUtil.toastShort("请授予本程序[读取手机状态]、[定位]和[写SD卡权限]");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == listButton.get(0)) {
            amapLocation();
        } else if (v == listButton.get(1)) {
            baiduMaplocation();
        }
        else if (v == listButton.get(2)) {
            ShareSocial.instance()
                       .setTitle("分享")
                       .setContent("分享内容")
                      // .setTargetUrl("http://www.baidu.com")
                       .setImgUrl("http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg")
                       .share(mActivity);
        } else if (v == listButton.get(3)) {
            PermissionHelper.requestReadWriteSdCardPermission(mActivity, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    String targetPath = FramePathConst.getInstance()
                                                      .getPathImgCache() + "shar_ic.png";
                    FileUtil.copyFileFromAssets(mActivity, "ic_launcher.png", targetPath);
                    ShareSocial.instance()
                               .setImgPath(targetPath)
                               .share(mActivity);
                }
                @Override
                public void onDenied(String permission) {

                }
            });
        }else if(v==listButton.get(4)){ //发起支付的方法

            UnionPayClassUtils unionPayClassUtils = new UnionPayClassUtils(this);
            unionPayClassUtils.initPay(Config.servUrl,Config.curMode);
        }

    }



    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
//        if(null!=jPushSystemMsgUtils){
//            jPushSystemMsgUtils.stopPush();
//        }
        super.onDestroy();
    }

}