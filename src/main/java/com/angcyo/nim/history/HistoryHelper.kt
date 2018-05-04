package com.angcyo.nim.history

import com.angcyo.library.utils.L
import com.angcyo.uiview.kotlin.nowTime
import com.angcyo.uiview.kotlin.toHHmmss
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RSubscriber
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.utils.RUtils
import com.netease.nimlib.sdk.NIMSDK
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum
import java.util.concurrent.CountDownLatch

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：云信历史消息拉取, 自动拉取最近所有联系人的近期所有云端消息记录
 * 创建人员：Robi
 * 创建时间：2018/05/04 11:16
 * 修改人员：Robi
 * 修改时间：2018/05/04 11:16
 * 修改备注：
 * Version: 1.0.0
 */
object HistoryHelper {

    /**历史记录拉取数据的状态*/
    var fetchState = FetchState.NONE
        set(value) {
            field = value
            observers.forEach {
                it.onEvent(field)
            }
        }

    private var countDownLatch: CountDownLatch? = null

    /**状态回调, 调用者线程*/
    var observers = mutableListOf<Observer<FetchState>>()

    /**需要拉取消息的开始时间*/
    var fetchMessageStartTime = -1L
    /**结束时间, -1:表示默认3天前*/
    var fetchMessageEndTime = -1L

    /**开始拉取消息记录*/
    @Synchronized
    fun fetchHistory() {

        if (fetchState == FetchState.ING) {
            return
        }
        if (fetchState == FetchState.END) {
            //已经拉过一次, 是否继续拉取?
        }

        fetchState = FetchState.ING
        fetchInner()
    }

    private var fetchStartTime = 0L
    private fun fetchInner() {
        //先获取最近会话列表
        Rx.get {
            val builder = StringBuilder()
            val contactsBlock = NIMSDK.getMsgService().queryRecentContactsBlock()
            if (RUtils.isListEmpty(contactsBlock)) {
                //最近无联系人
            } else {
                countDownLatch = CountDownLatch(contactsBlock.size)
                contactsBlock.forEach { contact ->
                    if (contact.sessionType == SessionTypeEnum.Team) {
                        builder.append("群:${contact.contactId}")
                    } else {
                        builder.append(contact.contactId)
                    }
                    builder.append(",")

                    val startTime = if (fetchMessageStartTime == -1L) {
                        nowTime()
                    } else {
                        fetchMessageStartTime
                    }
                    val endTime = if (fetchMessageEndTime == -1L) {
                        startTime - 3 * 24 * 60 * 60 * 1000 //3天前的消息
                    } else {
                        fetchMessageEndTime
                    }
                    fetchMessage(contact.contactId, contact.sessionType, startTime, endTime)
                }
                countDownLatch?.await()
            }
            builder.toString()
        }.subscribe(object : RSubscriber<String>() {

            override fun onStart() {
                super.onStart()
                fetchStartTime = nowTime()
            }

            override fun onSucceed(bean: String?) {
                super.onSucceed(bean)
                L.w("HistoryHelper: 历史消息同步完成 -> 耗时:${(nowTime() - fetchStartTime).toHHmmss()} \n$bean")
                fetchState = FetchState.SUCCESS
            }

            override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                super.onEnd(isError, isNoNetwork, e)
                fetchState = FetchState.END
            }
        })
    }

    private fun fetchMessage(sessionId: String, sessionType: SessionTypeEnum,
                             startTime: Long /*ms, 从什么时间开始查询*/,
                             endTime: Long /*ms, 查询结束时间*/) {
        NIMSDK.getMsgService()
                .pullMessageHistoryEx(MessageBuilder.createEmptyMessage(sessionId, sessionType, startTime),
                        endTime, 100 /*云信最多返回100条*/, QueryDirectionEnum.QUERY_OLD, true /*是否入库*/)
                .setCallback(object : RequestCallbackWrapper<MutableList<IMMessage>>() {
                    override fun onResult(code: Int, result: MutableList<IMMessage>?, exception: Throwable?) {
                        if (RUtils.isListEmpty(result)) {
                            //没有消息
                            countDownLatch?.countDown()
                        } else {
                            if (result!!.size < 100) {
                                //没有更多的消息
                                countDownLatch?.countDown()
                            } else {
                                //继续查询
                                val firstTime = result!![0].time
                                fetchMessage(sessionId, sessionType, firstTime, endTime)
                            }
                        }
                    }
                })
    }

    enum class FetchState {
        NONE /*初始化*/, ING /*进行中*/, SUCCESS, END /*结束*/;
    }
}