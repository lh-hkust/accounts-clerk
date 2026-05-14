package com.hermes.presentation.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.presentation.ui.theme.HermesColors
import kotlin.math.roundToInt

/**
 * 滑动方向枚举
 */
enum class SwipeDirection {
    LEFT,   // 左滑（向左拖动）
    RIGHT,  // 右滑（向右拖动）
    NONE    // 无滑动
}

/**
 * 可滑动卡片组件
 *
 * 支持左滑和右滑手势，提供背景渐变视觉反馈
 *
 * 设计规范：
 * - 右滑背景：主色渐变 (#3b82f6)，从右侧向左展开
 * - 左滑背景：危险色渐变 (#ef4444)，从左侧向右展开
 * - 滑动阈值：卡片宽度30%触发按钮显示
 * - 按钮文字：白色，16sp，粗体
 *
 * @param content 卡片内容
 * @param onRightSwipe 右滑动作
 * @param onLeftSwipe 左滑动作
 * @param rightSwipeLabel 右滑按钮文字
 * @param leftSwipeLabel 左滑按钮文字
 * @param rightSwipeEnabled 是否启用右滑
 * @param leftSwipeEnabled 是否启用左滑
 * @param onClick 点击事件
 * @param onLongPress 长按事件
 * @param modifier Modifier
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableCard(
    content: @Composable () -> Unit,
    onRightSwipe: () -> Unit,
    onLeftSwipe: () -> Unit,
    rightSwipeLabel: String = "操作",
    leftSwipeLabel: String = "删除",
    rightSwipeEnabled: Boolean = true,
    leftSwipeEnabled: Boolean = true,
    onClick: () -> Unit = {},
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // 滑动阈值：30% 卡片宽度（设计规范）
    // 由于卡片宽度是 fillMaxWidth，我们使用一个固定值作为阈值估算
    // 假设屏幕宽度约 360dp，30% = 108dp
    val swipeThreshold = with(density) { 108.dp.toPx() }
    val maxSwipeDistance = with(density) { 180.dp.toPx() } // 最大滑动距离

    var swipeOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // 计算滑动方向
    val swipeDirection = when {
        swipeOffset > swipeThreshold -> SwipeDirection.RIGHT
        swipeOffset < -swipeThreshold -> SwipeDirection.LEFT
        else -> SwipeDirection.NONE
    }

    // 判断是否达到阈值
    val thresholdReached = swipeDirection != SwipeDirection.NONE

    // 背景渐变色动画
    val backgroundAlpha by animateFloatAsState(
        targetValue = when {
            swipeDirection == SwipeDirection.RIGHT -> 1f
            swipeDirection == SwipeDirection.LEFT -> 1f
            swipeOffset.absoluteValue > 0 -> (swipeOffset.absoluteValue / swipeThreshold).coerceIn(0f, 1f)
            else -> 0f
        },
        animationSpec = tween(100),
        label = "bg_alpha"
    )

    // 滑动偏移动画（拖动时直接跟随，释放时动画复位）
    val animatedOffset by animateFloatAsState(
        targetValue = if (isDragging) swipeOffset else 0f,
        animationSpec = tween(300),
        label = "swipe_offset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // 滑动背景层（渐变色）
        if (animatedOffset.absoluteValue > 0 || swipeDirection != SwipeDirection.NONE) {
            // 右滑背景（从右侧展开）
            if (animatedOffset > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    HermesColors.Primary.copy(alpha = 0.6f),
                                    HermesColors.Primary.copy(alpha = backgroundAlpha)
                                ),
                                startX = Float.MAX_VALUE,
                                endX = 0f
                            )
                        )
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (thresholdReached && rightSwipeEnabled) {
                        Row(
                            modifier = Modifier.padding(end = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rightSwipeLabel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HermesColors.TextPrimary
                            )
                        }
                    }
                }
            }

            // 左滑背景（从左侧展开）
            if (animatedOffset < 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    HermesColors.Danger.copy(alpha = backgroundAlpha),
                                    HermesColors.Danger.copy(alpha = 0.6f)
                                ),
                                startX = 0f,
                                endX = Float.MAX_VALUE
                            )
                        )
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (thresholdReached && leftSwipeEnabled) {
                        Row(
                            modifier = Modifier.padding(start = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = leftSwipeLabel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HermesColors.TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // 卡片内容层
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .combinedClickable(
                    onClick = {
                        // 点击事件（只有在没有滑动时才触发）
                        if (animatedOffset.absoluteValue < 10f) {
                            onClick()
                        }
                    },
                    onLongClick = {
                        // 长按事件（只有在没有滑动时才触发）
                        if (animatedOffset.absoluteValue < 10f) {
                            onLongPress()
                        }
                    }
                )
                .pointerInput(rightSwipeEnabled, leftSwipeEnabled) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = swipeOffset + dragAmount

                            // 根据启用状态限制滑动方向
                            swipeOffset = when {
                                !rightSwipeEnabled && newOffset > 0 -> 0f // 右滑禁用，向右滑动时阻止
                                !leftSwipeEnabled && newOffset < 0 -> 0f // 左滑禁用，向左滑动时阻止
                                newOffset > maxSwipeDistance -> maxSwipeDistance // 最大距离限制
                                newOffset < -maxSwipeDistance -> -maxSwipeDistance
                                else -> newOffset
                            }
                        },
                        onDragEnd = {
                            isDragging = false
                            // 判断是否触发动作
                            if (swipeDirection == SwipeDirection.RIGHT && rightSwipeEnabled) {
                                onRightSwipe()
                            } else if (swipeDirection == SwipeDirection.LEFT && leftSwipeEnabled) {
                                onLeftSwipe()
                            }
                            // 重置偏移
                            swipeOffset = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            swipeOffset = 0f
                        }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 8.dp else 2.dp)
        ) {
            content()
        }
    }
}

/**
 * 扩展属性：获取绝对值
 */
private val Float.absoluteValue: Float
    get() = kotlin.math.abs(this)