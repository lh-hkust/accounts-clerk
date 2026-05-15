# 开发典型问题总结

> 本文档记录开发过程中发现的典型问题，供后续审核agent重点审核参考。

---

## 一、需求与实现一致性

### 1.1 错误删除需求功能

**问题描述**: 开发中发现原型/UI简化后，错误地认为某功能可以删除，直接移除代码，而非调整布局以满足需求。

**典型案例**: AddAccountScreen渠道搜索功能被错误删除。开发者看到原型中没有搜索框，就删除了搜索代码。但需求规格明确要求渠道搜索功能，只是搜索框位置需要调整。

**根本原因**:
- 未严格对照需求规格验证
- 仅以原型/UI为唯一参考
- 未确认需求来源（原型 vs 规格）

**审核要点**:
1. 任何功能删除前，必须确认需求规格中是否有要求
2. 原型与规格冲突时，以规格为准，原型需同步更新
3. 标记"未完成"时需注明原因（设计变更 vs 实现遗漏）

**预防措施**:
- 删除代码前先grep需求规格确认
- 变更需同步更新原型、规格、tasks.md三者

---

## 二、文档同步一致性

### 2.1 只更新tasks.md，遗漏设计文档和原型

**问题描述**: 修改设计后，只更新tasks.md标记状态，未同步更新UI设计文档和原型。

**典型案例**: 渠道分组显示从分组改为平铺，tasks.md标记为"设计变更"，但原型和ui-design.md未同步更新。

**根本原因**:
- 文档同步意识不足
- 认为tasks.md记录就够了

**审核要点**:
1. 设计变更必须同步更新：tasks.md + spec.md + 原型 + ui-design.md
2. 审核时检查四者一致性

**预防措施**:
- 建立文档同步检查清单
- 设计变更后立即更新相关文档

---

## 三、ViewModel实例隔离

### 3.1 hiltViewModel创建独立实例而非共享实例

**问题描述**: 导航到子页面后，ViewModel是新实例而非共享实例，导致数据不刷新。

**典型案例**: AddAccountScreen添加渠道后返回，渠道列表不刷新。原因是AddAccount和IdentifierList各自创建了独立的IdentifierViewModel实例。

**根本原因**:
- `hiltViewModel()` 默认为当前composable创建独立实例
- 需要共享实例时需用 `getBackStackEntry()` + `hiltViewModel(parentEntry)`

**审核要点**:
1. 子页面需要刷新父页面数据时，检查ViewModel是否共享
2. 共享模式：`remember(navBackStackEntry) { navController.getBackStackEntry(parentRoute) }` + `hiltViewModel(parentEntry)`

**预防措施**:
- 识别数据共享场景（添加返回刷新列表）
- 使用正确的ViewModel共享模式

---

## 四、LaunchedEffect刷新时机

### 4.1 LaunchedEffect(Unit)只执行一次

**问题描述**: 使用 `LaunchedEffect(Unit)` 加载数据，只在首次进入时执行，页面恢复时不刷新。

**典型案例**: AddAccountScreen从AddIdentifier返回后，渠道列表不更新。

**根本原因**:
- `LaunchedEffect(Unit)` key为Unit，只执行一次
- 需要生命周期感知刷新时，应使用 DisposableEffect + LifecycleEventObserver

**审核要点**:
1. 需要每次进入/恢复刷新时，使用DisposableEffect + ON_RESUME
2. 一次性加载用LaunchedEffect(Unit)
3. 参数变化刷新用LaunchedEffect(param)

**正确模式**:
```kotlin
DisposableEffect(Unit) {
    val lifecycleObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            viewModel.loadData()
        }
    }
    navBackStackEntry.lifecycle.addObserver(lifecycleObserver)
    onDispose {
        navBackStackEntry.lifecycle.removeObserver(lifecycleObserver)
    }
}
```

---

## 五、独立审视质量

### 5.1 审视未对照需求规格验证

**问题描述**: 独立审视agent只检查代码实现是否"正确"，未对照需求规格验证是否满足需求。

**典型案例**: 审视AddAccountScreen简化，只确认代码清理干净，未验证是否满足需求规格的分组要求。

**根本原因**:
- 审视prompt未明确要求对照需求规格
- 审视范围局限于代码质量

**审核要点（审视agent重点）**:
1. **必须对照需求规格验证**：审视第一条就是检查需求规格要求
2. 检查原型、规格、tasks.md三者一致性
3. 检查ViewModel数据流是否正确（共享实例、刷新时机）
4. 检查文档同步更新

**审视prompt模板**:
```
独立审视以下实现，必须对照需求规格验证：

1. 需求规格位置：[spec.md路径]
2. 审视要点：
   - 对照需求规格验证是否满足所有要求
   - 检查原型与代码一致性
   - 检查ViewModel数据流
   - 检查文档同步更新

审视步骤：
- 先读取需求规格，列出关键要求
- 对照代码逐一验证
- 发现问题需明确指出违反的具体规格条款
```

---

## 六、主Agent目标管理

### 6.1 未复核独立审视结论

**问题描述**: 主agent收到独立审视"通过"结论后直接结束，未复核审视是否真正验证需求。

**典型案例**: 审视agent结论是"通过"，但实际未验证分组要求，主agent也未发现。

**根本原因**:
- 过度信任子agent结论
- 未检查审视过程是否完整

**审核要点**:
1. 主agent必须复核审视agent的验证范围
2. 检查审视是否包含需求规格对照
3. 发现审视质量不足时需重新审视

---

## 七、UI布局缺陷（新增）

### 7.1 按钮位置错误

**问题描述**: 保存按钮跑到中间位置，下方空白浪费。

**典型案例**: AddAccountScreen保存按钮不在底部，布局不合理。

**根本原因**:
- 未使用Scaffold的bottomBar或正确的Spacer布局
- Column布局未正确处理剩余空间

**审核要点**:
1. 主要操作按钮应固定在底部
2. 使用Spacer(weight=1f)填充中间空白
3. 对照原型验证布局位置

**正确模式**:
```kotlin
Column {
    // 内容区域
    Spacer(modifier = Modifier.weight(1f)) // 填充空白
    // 保存按钮固定在底部
    Button(...) { ... }
}
```

### 7.2 空状态未居中

**问题描述**: 空状态提示文本未居中显示。

**典型案例**: AccountListScreen "暂无账号记录" 未居中。

**根本原因**:
- Box布局未设置horizontalAlignment
- Text组件未正确设置居中对齐

**审核要点**:
1. 空状态Card必须居中显示
2. Box设置horizontalAlignment = Alignment.CenterHorizontally
3. Text居中：textAlign = TextAlign.Center

### 7.3 手势提示动画卡顿

**问题描述**: 每次进入页面都有下滑动画，给人卡顿感。

**典型案例**: IdentifierListScreen手势提示动画每次触发。

**根本原因**:
- 动画触发时机未与用户首次使用关联
- 每次页面加载都触发动画

**审核要点**:
1. 手势提示仅在首次显示（记录到SharedPreferences）
2. 或改为底部静态浅色文本提示
3. 动画不应阻塞页面加载

---

## 八、业务逻辑缺陷（新增）

### 8.1 空数据时默认值不合理

**问题描述**: 0个账号时安全指数显示100分，逻辑不合理。

**典型案例**: DashboardScreen安全指数空状态。

**根本原因**:
- 业务概念未定义：安全指数计算逻辑是什么？
- 空数据时返回了错误的默认值

**审核要点**:
1. 空数据时的默认值必须合理（0分或"暂无数据"）
2. 业务概念必须在需求规格中定义
3. 数据驱动的计算逻辑需明确边界条件

**正确模式**:
```kotlin
fun calculateSecurityScore(accounts: List<Account>): Int {
    if (accounts.isEmpty()) {
        return 0 // 无数据时为0分，而非100分
    }
    // 计算逻辑...
}
```

### 8.2 外键约束未校验

**问题描述**: 保存数据时报错"外键约束失败"。

**典型案例**: AddAccount保存账号报错。

**根本原因**:
- 前端传递的外键值不存在于目标表
- 未在保存前校验外键有效性

**审核要点**:
1. 外键字段保存前必须校验目标表是否存在
2. 提供友好错误提示而非数据库约束报错
3. 检查数据传递逻辑是否正确

---

## 九、功能实现缺陷（新增）

### 9.1 影响范围页面功能缺失

**问题描述**: 点击渠道卡片，影响范围页面功能未实现。

**典型案例**: IdentifierDetailScreen影响范围展示。

**根本原因**:
- 功能未完成但标记为已完成
- tasks.md与实际实现不一致

**审核要点**:
1. 对照需求规格确认功能完整性
2. 核心功能必须有实现代码
3. tasks.md标记需与实际一致

### 9.2 编辑功能字段限制不当

**问题描述**: 编辑页面只有部分字段可编辑，其他被错误锁定。

**典型案例**: EditIdentifierScreen类型和值不可编辑。

**审核要点**:
1. 区分业务规则限制（类型确实不应改）vs 实现缺陷
2. 业务规则限制需在需求规格中明确
3. 可编辑字段必须有完整的编辑逻辑

---

## 审核Checklist

独立审视agent必须包含以下检查项：

| # | 检查项 | 说明 |
|---|--------|------|
| 1 | 需求规格对照 | 必须读取spec.md并验证每条要求 |
| 2 | 原型一致性 | 检查原型是否与代码同步 |
| 3 | ViewModel数据流 | 检查ViewModel实例是否正确共享/刷新 |
| 4 | 文档同步 | 检查tasks.md、spec.md、原型是否同步更新 |
| 5 | 典型问题排查 | 检查是否存在本文档记录的典型问题 |
| 6 | UI布局验证 | 检查按钮位置、空状态居中、布局合理性 |
| 7 | 业务逻辑边界 | 检查空数据默认值、外键校验 |
| 8 | 功能完整性 | 检查标记已完成的功能是否真正实现 |

---

## 本轮排查发现的问题汇总（2026-05-15）

| # | 问题类型 | 问题描述 | 优先级 | 状态 |
|---|---------|---------|-------|------|
| 1 | 业务逻辑缺陷 | 首页安全指数：0账号时显示100分，逻辑不合理 | P0 | ✅ 已修复（三态逻辑） |
| 2 | UI布局缺陷 | 账号列表空状态未居中显示 | P1 | ✅ 验证已正确实现 |
| 3 | UI布局缺陷 | 添加账号页保存按钮位置不对，下方空白浪费 | P1 | ✅ 已修复（Spacer.weight） |
| 4 | 功能实现缺陷 | 渠道选择变成单选，右滑选择用途功能缺失 | P0 | ✅ 已修复（多渠道绑定） |
| 5 | 功能实现缺陷 | 渠道搜索框缺失 | P2 | ✅ 已实现 |
| 6 | 代码缺陷 | 保存账号报错外键约束失败 | P0 | ✅ 已修复（数据种子化） |
| 7 | UI布局缺陷 | 渠道列表手势动画每次触发，卡顿感 | P1 | ✅ 验证已正确实现（首次触发） |
| 8 | 功能实现缺陷 | 渠道详情页（影响范围）功能未实现 | P0 | ✅ 已修复（完整布局） |
| 9 | 功能实现缺陷 | 编辑渠道字段限制不当（需确认业务规则） | P1 | ✅ 业务规则确认（类型不可改） |
| 10 | 代码缺陷 | AddAccountScreen硬编码应用ID | P0 | ✅ 已修复（动态获取） |

---

## 修复记录（2026-05-15）

### 1. AddAccountScreen保存按钮位置修复
- **文件**: AddAccountScreen.kt:337
- **修改**: `Spacer(modifier = Modifier.height(16.dp))` → `Spacer(modifier = Modifier.weight(1f))`
- **效果**: 保存按钮固定在底部，中间空白正确填充

### 2. IdentifierDetailScreen影响范围页面完善
- **文件**: IdentifierDetailScreen.kt
- **新增组件**:
  - `IdentifierDetailCard`: 渐变背景，状态标签，到期倒计时
  - `BoundAccountCard`: 左边框颜色区分，用途标签显示
  - `PurposeTag`: 用途小标签组件
- **功能**: 完整的标识详情展示 + 关联账号列表 + 操作按钮

### 3. AddAccountScreen应用列表动态获取
- **文件**:
  - AddAccountScreen.kt: 新增 `AppOption` 数据类和 `loadApplications` 参数
  - AccountViewModel.kt: 新增 `applicationListState` 和 `loadApplications()` 方法
  - HermesNavigation.kt: 传递动态应用列表到 AddAccountScreen
- **修改**: 硬编码应用ID → 从数据库动态获取（调用 GetApplicationListUseCase）

---

*文档版本: v2.1*
*创建日期: 2026-05-15*
*最后更新: 2026-05-15*