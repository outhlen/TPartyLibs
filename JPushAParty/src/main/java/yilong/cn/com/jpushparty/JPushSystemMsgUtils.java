package yilong.cn.com.jpushparty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017\8\1 0001.
 */

public class JPushSystemMsgUtils {

    private static final String TAG = "JIGUANG-JPushSystemMsgUtils";
    private Context mContext;
    private MessageReceiver mMessageReceiver;
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public  final String MESSAGE_RECEIVED_ACTION = "cn.jpush.android.intent.MESSAGE_RECEIVED";
    public  final String ACTION_REGISTRATION_ID = "cn.jpush.android.intent.REGISTRATION";
    public  final String ACTION_NOTIFICATION_RECEIVED = "cn.jpush.android.intent.NOTIFICATION_RECEIVED";
    public  final String ACTION_NOTIFICATION_OPENED = "cn.jpush.android.intent.NOTIFICATION_OPENED";

    public  JPushSystemMsgUtils(Context context){
        this.mContext = context;
    }

    public void initJPushInfo() {
        JPushInterface.init(mContext);
        //registerMessageReceiver();
    }

    private void registerMessageReceiver() {

        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
      //  filter.addAction(ACTION_NOTIFICATION_OPENED);
//        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, filter);
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
                }else if(ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
                    Toast.makeText(mContext,"打开链接",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
            }
        }
    }

    // 打印所有的 intent extra 数据
    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it =  json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " +json.optString(myKey) + "]");
                    }
                    setCostomMsg(sb.toString());
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(KEY_EXTRAS, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        }
    }

    private void setCostomMsg(String msg){
        if (null != msg) {
            Toast.makeText(mContext,"接收到："+msg,Toast.LENGTH_SHORT).show();
        }
    }
    /**
     *  停止接收消息
     */
    public void stopPush(){
        JPushInterface.stopPush(mContext);
    }

/**
 *  销毁方法
 */
    public void destoryPush(){

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
    }

}
