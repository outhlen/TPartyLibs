package yilong.cn.com.jpushparty;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017\8\1 0001.
 */

public class App extends Application {

    private static final String TAG = "JIGUANG-Example";
     private JPushSystemMsgUtils jPushSystemMsgUtils;
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
       // JPushInterface.init(this);     		// 初始化 JPush
        jPushSystemMsgUtils  = new JPushSystemMsgUtils(this);
        jPushSystemMsgUtils.initJPushInfo();
    }

}
