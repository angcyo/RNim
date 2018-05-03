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

/**
 * SDK接口	说明
 * AuthService      	用户认证服务接口，提供登录注销接口。
 * AuthServiceObserver      	用户认证服务观察者接口。
 * MsgService	        消息服务接口，用于发送消息，管理消息记录等。同时还提供了发送自定义通知的接口。
 * MsgServiceObserve        	接收消息，消息状态变化等观察者接口。
 * LuceneService        	聊天消息全文检索接口。
 * TeamService      	群组服务接口，用于发送群组消息，管理群组和群成员资料等。
 * TeamServiceObserve       	群组和群成员资料变化观察者。
 * SystemMessageService	        系统通知观察者。
 * FriendService        	好友关系托管接口，目前支持添加、删除好友、获取好友列表、黑名单、设置消息提醒。
 * FriendServiceObserve	        好友关系变更、黑名单变更通知观察者。
 * UserService	        用户资料托管接口，提供获取用户资料、修改个人资料等。
 * UserServiceObserve	        用户资料托管接口，提供获取用户资料、修改个人资料等。
 * AVChatManager        	语音视频通话接口。
 * RTSManager	        实时会话接口。
 * NosService	        网易云存储服务，提供文件上传和下载。
 * NosServiceObserve        	网易云存储传输进度观察者接口。
 * MixPushService	        第三方推送接口，提供第三方推送服务。
 * EventSubscribeService	        事件订阅服务接口，提供事件订阅等服务
 * EventSubscribeServiceObserver        	事件状态变更观察者接口。
 * RedPacketService	        红包接口。提供获取红包sdk token等功能。
 * RobotService     	机器人操作相关接口，提供获取机器人、获取机器人信息、判断是否是机器人等功能。
 * RobotServiceObserve      	机器人数据变更观察者接口。
 * SettingsService      	系统设置接口。提供多端推送、免打扰配置
 * SettingsServiceObserver      	系统设置变更观察者接口。
 */