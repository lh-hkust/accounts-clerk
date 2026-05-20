package com.hermes.presentation.ui.util

import androidx.compose.ui.graphics.Color
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 应用颜色映射工具类
 * 根据应用名称返回对应的品牌颜色
 */
object AppColorUtils {

    /**
     * 根据应用名称获取品牌颜色
     * 用于应用图标背景、卡片边框等
     */
    fun getAppColor(appName: String): Color = when {
        appName.contains("微信") -> Color(0xFF07c160) // 微信绿
        appName.contains("支付宝") -> Color(0xFF1677ff) // 支付宝蓝
        appName.contains("微博") -> Color(0xFFe6162d) // 微博红
        appName.contains("抖音") -> Color(0xFF000000) // 抖音黑
        appName.contains("淘宝") -> Color(0xFFff4400) // 淘宝橙
        appName.contains("京东") -> Color(0xFFe53935) // 京东红
        appName.contains("QQ") -> Color(0xFF12b7f5) // QQ蓝
        appName.contains("GitHub") -> Color(0xFF333333) // GitHub灰
        appName.contains("银行") || appName.contains("招商") -> Color(0xFF1677ff) // 银行蓝
        appName.contains("美团") -> Color(0xFFffd400) // 美团黄
        appName.contains("小红书") -> Color(0xFFff2442) // 小红书红
        appName.contains("拼多多") -> Color(0xFFe02e24) // 拼多多红
        appName.contains("哔哩") || appName.contains("B站") -> Color(0xFF00a1d6) // B站蓝
        appName.contains("知乎") -> Color(0xFF0066ff) // 知乎蓝
        appName.contains("快手") -> Color(0xFFff5000) // 快手橙
        appName.contains("钉钉") -> Color(0xFF0089ff) // 钉钉蓝
        appName.contains("飞书") -> Color(0xFF3370ff) // 飞书蓝
        appName.contains("腾讯会议") -> Color(0xFF2b6aff) // 腾讯会议蓝
        appName.contains("爱奇艺") -> Color(0xFF00be06) // 爱奇艺绿
        appName.contains("优酷") -> Color(0xFF1891f5) // 优酷蓝
        appName.contains("网易云音乐") -> Color(0xFFc20c0c) // 网易红
        appName.contains("QQ音乐") -> Color(0xFF31c27c) // QQ音乐绿
        appName.contains("微信支付") -> Color(0xFF09bb07) // 微信支付绿
        appName.contains("云闪付") -> Color(0xFFe6162d) // 云闪付红
        else -> HermesColors.Primary // 默认主题色
    }

    /**
     * 获取应用边框颜色（用于卡片左边框）
     * 与 getAppColor 相同，但语义更明确
     */
    fun getBorderColor(appName: String): Color = getAppColor(appName)

    /**
     * 获取应用图标颜色
     * 与 getAppColor 相同，但语义更明确
     */
    fun getIconColor(appName: String): Color = getAppColor(appName)
}