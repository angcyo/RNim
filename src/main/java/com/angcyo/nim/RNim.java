package com.angcyo.nim;

import android.app.Application;
import android.text.TextUtils;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：网易云信 API:http://dev.netease.im/docs/interface/%E5%8D%B3%E6%97%B6%E9%80%9A%E8%AE%AFAndroid%E7%AB%AF/IM_Android/index.html
 * 类的描述：接入文档:http://dev.netease.im/docs/product/IM%E5%8D%B3%E6%97%B6%E9%80%9A%E8%AE%AF/SDK%E5%BC%80%E5%8F%91%E9%9B%86%E6%88%90/Android%E5%BC%80%E5%8F%91%E9%9B%86%E6%88%90/%E6%A6%82%E8%A6%81%E4%BB%8B%E7%BB%8D
 * 创建人员：Robi
 * 创建时间：2018/04/25 17:16
 * 修改人员：Robi
 * 修改时间：2018/04/25 17:16
 * 修改备注：
 * Version: 1.0.0
 */
public class RNim {

    public static void init(Application application, String account, String token, SDKOptions options) {
        LoginInfo loginInfo = null;
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            loginInfo = new LoginInfo(account, token);
        }

        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(application, loginInfo, options);

        // ... your codes
        if (NIMUtil.isMainProcess(application)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用
        }
    }
}
