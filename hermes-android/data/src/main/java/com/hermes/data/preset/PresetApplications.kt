package com.hermes.data.preset

import com.hermes.domain.model.Application
import com.hermes.domain.valueobject.ApplicationType
import java.time.Instant

/**
 * 预置应用清单数据
 * 包含常用应用平台分类
 */
object PresetApplications {

    fun getPresetApplications(): List<Application> {
        val now = Instant.now()
        return listOf(
            // 社交类
            createApplication("微信", ApplicationType.MOBILE_APP, "社交", "https://weixin.qq.com"),
            createApplication("QQ", ApplicationType.MOBILE_APP, "社交", "https://im.qq.com"),
            createApplication("微博", ApplicationType.BOTH, "社交", "https://weibo.com"),
            createApplication("抖音", ApplicationType.MOBILE_APP, "社交", "https://www.douyin.com"),
            createApplication("小红书", ApplicationType.MOBILE_APP, "社交", "https://www.xiaohongshu.com"),
            createApplication("快手", ApplicationType.MOBILE_APP, "社交", "https://www.kuaishou.com"),
            createApplication("B站", ApplicationType.BOTH, "社交", "https://www.bilibili.com"),
            createApplication("知乎", ApplicationType.BOTH, "社交", "https://www.zhihu.com"),
            createApplication("Twitter", ApplicationType.BOTH, "社交", "https://twitter.com"),
            createApplication("Instagram", ApplicationType.MOBILE_APP, "社交", "https://www.instagram.com"),

            // 金融类
            createApplication("支付宝", ApplicationType.MOBILE_APP, "金融", "https://www.alipay.com"),
            createApplication("微信支付", ApplicationType.MOBILE_APP, "金融", "https://pay.weixin.qq.com"),
            createApplication("工商银行", ApplicationType.BOTH, "金融", "https://www.icbc.com.cn"),
            createApplication("建设银行", ApplicationType.BOTH, "金融", "https://www.ccb.com"),
            createApplication("招商银行", ApplicationType.BOTH, "金融", "https://www.cmbchina.com"),
            createApplication("农业银行", ApplicationType.BOTH, "金融", "https://www.abchina.com"),
            createApplication("中国银行", ApplicationType.BOTH, "金融", "https://www.boc.cn"),
            createApplication("交通银行", ApplicationType.BOTH, "金融", "https://www.bankcomm.com"),
            createApplication("浦发银行", ApplicationType.BOTH, "金融", "https://www.spdb.com.cn"),
            createApplication("邮储银行", ApplicationType.BOTH, "金融", "https://www.psbc.com"),

            // 购物类
            createApplication("淘宝", ApplicationType.BOTH, "购物", "https://www.taobao.com"),
            createApplication("京东", ApplicationType.BOTH, "购物", "https://www.jd.com"),
            createApplication("拼多多", ApplicationType.MOBILE_APP, "购物", "https://www.pinduoduo.com"),
            createApplication("天猫", ApplicationType.BOTH, "购物", "https://www.tmall.com"),
            createApplication("唯品会", ApplicationType.MOBILE_APP, "购物", "https://www.vip.com"),
            createApplication("得物", ApplicationType.MOBILE_APP, "购物", "https://www.poizon.com"),
            createApplication("美团", ApplicationType.MOBILE_APP, "购物", "https://www.meituan.com"),
            createApplication("饿了么", ApplicationType.MOBILE_APP, "购物", "https://www.ele.me"),
            createApplication("大众点评", ApplicationType.MOBILE_APP, "购物", "https://www.dianping.com"),
            createApplication("Amazon", ApplicationType.BOTH, "购物", "https://www.amazon.com"),

            // 工具类
            createApplication("百度", ApplicationType.BOTH, "工具", "https://www.baidu.com"),
            createApplication("高德地图", ApplicationType.MOBILE_APP, "工具", "https://www.amap.com"),
            createApplication("腾讯地图", ApplicationType.MOBILE_APP, "工具", "https://map.qq.com"),
            createApplication("滴滴出行", ApplicationType.MOBILE_APP, "工具", "https://www.didiglobal.com"),
            createApplication("哈啰出行", ApplicationType.MOBILE_APP, "工具", "https://www.hellobike.com"),
            createApplication("WPS", ApplicationType.BOTH, "工具", "https://www.wps.cn"),
            createApplication("QQ音乐", ApplicationType.MOBILE_APP, "工具", "https://y.qq.com"),
            createApplication("网易云音乐", ApplicationType.MOBILE_APP, "工具", "https://music.163.com"),
            createApplication("酷狗音乐", ApplicationType.MOBILE_APP, "工具", "https://www.kugou.com"),
            createApplication("酷我音乐", ApplicationType.MOBILE_APP, "工具", "https://www.kuwo.cn"),

            // 游戏类
            createApplication("王者荣耀", ApplicationType.MOBILE_APP, "游戏", "https://pvp.qq.com"),
            createApplication("和平精英", ApplicationType.MOBILE_APP, "游戏", "https://gp.qq.com"),
            createApplication("英雄联盟", ApplicationType.BOTH, "游戏", "https://lol.qq.com"),
            createApplication("Steam", ApplicationType.WEB_SITE, "游戏", "https://store.steampowered.com"),
            createApplication("原神", ApplicationType.MOBILE_APP, "游戏", "https://ys.mihoyo.com"),
            createApplication("网易游戏", ApplicationType.BOTH, "游戏", "https://game.163.com"),

            // 出行类
            createApplication("携程", ApplicationType.BOTH, "出行", "https://www.ctrip.com"),
            createApplication("去哪儿", ApplicationType.BOTH, "出行", "https://www.qunar.com"),
            createApplication("飞猪", ApplicationType.BOTH, "出行", "https://www.fliggy.com"),
            createApplication("12306", ApplicationType.WEB_SITE, "出行", "https://www.12306.cn"),
            createApplication("航旅纵横", ApplicationType.MOBILE_APP, "出行", "https://www.umetrip.com"),

            // 学习类
            createApplication("钉钉", ApplicationType.BOTH, "学习", "https://www.dingtalk.com"),
            createApplication("企业微信", ApplicationType.MOBILE_APP, "学习", "https://work.weixin.qq.com"),
            createApplication("飞书", ApplicationType.BOTH, "学习", "https://www.feishu.cn"),
            createApplication("腾讯会议", ApplicationType.BOTH, "学习", "https://meeting.tencent.com"),
            createApplication("慕课网", ApplicationType.WEB_SITE, "学习", "https://www.imooc.com"),
            createApplication("网易云课堂", ApplicationType.WEB_SITE, "学习", "https://study.163.com"),

            // 其他类
            createApplication("Google", ApplicationType.WEB_SITE, "其他", "https://www.google.com"),
            createApplication("GitHub", ApplicationType.WEB_SITE, "其他", "https://github.com"),
            createApplication("Gitee", ApplicationType.WEB_SITE, "其他", "https://gitee.com"),
            createApplication("领英", ApplicationType.WEB_SITE, "其他", "https://www.linkedin.com")
        )
    }

    fun getCategories(): List<String> {
        return listOf("社交", "金融", "购物", "工具", "游戏", "出行", "学习", "其他")
    }

    private fun createApplication(
        name: String,
        type: ApplicationType,
        category: String,
        officialUrl: String
    ): Application {
        val now = Instant.now()
        return Application(
            id = null,
            name = name,
            type = type,
            officialUrl = officialUrl,
            iconUrl = null,
            category = category,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )
    }
}