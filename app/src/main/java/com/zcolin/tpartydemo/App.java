/*
 * *********************************************************
 *   author   colin
 *   company  fosung
 *   email    wanglin2046@126.com
 *   date     17-6-13 上午11:16
 * ********************************************************
 */

package com.zcolin.tpartydemo;

import com.zcolin.frame.app.BaseApp;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.util.ShareSocial;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import yilong.cn.com.jpushparty.JPushSystemMsgUtils;

/**
 *
 */

public class App extends BaseApp {

   public static JPushSystemMsgUtils jPushSystemMsgUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        ShareSocial.setHiddenPlatForms(SinaWeibo.NAME, TencentWeibo.NAME, WechatFavorite.NAME);
        jPushSystemMsgUtils  = new JPushSystemMsgUtils(this);
        jPushSystemMsgUtils.initJPushInfo();
    }
}
