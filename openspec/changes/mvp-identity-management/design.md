## Context

Hermes Android 是一款 Android 账号管理工具，采用 DDD（领域驱动设计）方法论，数据完全本地存储，使用 SQLCipher + Android Keystore 加密。项目遵循 TDD（测试驱动开发）原则，所有业务逻辑必须有对应的单元测试。

**技术栈约束**：
- Android Room 作为数据库封装层
- Kotlin 作为开发语言
- SQLCipher 加密数据库文件
- DEK+KEK 信封加密架构

**术语规范（NIST SP 800-63B）**：
- IdentityIdentifier (IID/标识)：手机号/邮箱，声明身份的唯一标识
- ApplicationAccount (AA/账户)：应用内注册的账户
- IdentifierBinding (IB/绑定)：账户与身份标识的关联关系
- AuthenticationCredential (AC/凭据)：密码/令牌（V2.0规划）

## Goals / Non-Goals

**Goals:**
- 实现身份标识基础管理（添加、查看、编辑、删除）
- 实现停用计划功能（设置、取消、修改）
- 实现预警系统（自动触发、级别计算、查看、处理）
- 实现应用账户管理（添加、查看、编辑、绑定标识）
- 数据库加密方案完整实现
- 领域模型和聚合边界清晰定义

**Non-Goals:**
- V2.0认证凭据管理（密码、令牌安全存储）
- 云端同步功能
- 多用户/多设备支持
- 网络请求/第三方API集成

## Decisions

### D1: 聚合设计

**Decision**: 采用以下聚合边界
- IdentityIdentifierAggregate：身份标识 + 停用计划
- ApplicationAccountAggregate：账户 + 绑定关系 + 扩展属性
- WarningAggregate：预警记录（独立聚合）
- ApplicationAggregate：应用平台（引用聚合）

**Alternatives Considered**:
- 将预警放入IdentityIdentifier聚合：被否决，预警可独立查询和处理
- 将绑定放入IdentityIdentifier聚合：被否决，绑定由账户管理更自然

**Rationale**: 聚合边界遵循业务事务一致性边界，身份标识与停用计划强一致，账户与绑定关系强一致。

### D2: 数据库加密方案

**Decision**: SQLCipher数据库级加密 + DEK+KEK信封加密

**Architecture**:
- DEK（数据加密密钥）加密整个数据库文件
- KEK（密钥加密密钥）保护DEK
- KEK由用户密码派生（PBKDF2-HMAC-SHA256，100000次迭代）或直接存储（无密码模式）
- KEK存储于Android Keystore
- 数据库内部存储明文，支持SQL查询和索引

**Alternatives Considered**:
- 字段级加密：被否决，检索效率低，重复校验复杂
- 无加密：被否决，安全性不足

**Rationale**: 数据库级加密兼顾安全性和可用性，明文存储支持高效检索和唯一约束。

### D3: 预警级别计算

**Decision**: 基于影响账户数和账户分类计算级别

| 条件 | 级别 |
|------|------|
| 影响金融类应用账户 | HIGH |
| 影响账户数 > 5 | HIGH |
| 影响账户数 2-5（无金融应用） | MEDIUM |
| 影响账户数 = 1（非敏感账户） | LOW |

**Rationale**: 金融类账户风险最高，影响范围大的账户风险次之。

### D4: 状态机设计

**IdentityIdentifier状态流转**:
```
ACTIVE → PENDING_DEACTIVATION → DEACTIVATED
ACTIVE → INVALIDATED
PENDING_DEACTIVATION → ACTIVE (取消计划)
```

**ApplicationAccount状态流转**:
```
ACTIVE → FROZEN → ACTIVE (解冻)
ACTIVE → LOST
ACTIVE → ARCHIVED
```

**Rationale**: 状态机确保状态变更合法性，防止非法状态转换。

### D5: UI交互设计

**Decision**: 采用深色主题科幻风格，基于Shadcn UI设计规范

**Design Reference**: 详见 [ui-design.md](../../docs/2_design/ui-design.md)

**核心页面结构**:

| 页面 | 功能 | 优先级 |
|------|------|--------|
| 欢迎页 | 动态排版介绍核心价值，引导首次使用 | P0 |
| 首页看板 | 预警概览（风险雷达）、状态统计、快捷入口 | P0 |
| 标识列表页 | 展示所有标识，智能排序（停用日期/风险等级），状态筛选 | P0 |
| 标识详情页 | 标识信息、资产树状视图（关联账户）、影响分析 | P0 |
| 账户列表页 | 按应用分类，多维度状态标签，支持账号分级 | P0 |
| 账户详情页 | 账户信息、绑定标识列表 | P0 |
| 设置停用计划页 | 选择日期、输入原因、显示受影响账户 | P1 |
| 数据工具页 | 批量导入导出（CSV/JSON） | P1 |

**视觉规范**:
- 主色：#3b82f6（霓虹蓝）
- 辅色：#a855f7（极光紫）
- 背景：#0f172a（深色）
- 卡片：#1e293b
- 成功：#22c55e
- 警告：#eab308
- 危险：#ef4444

**交互模式**:
- 底部Tab导航切换主要模块
- 重要操作采用悬浮按钮（FAB）
- 删除/停用等危险操作需确认弹窗

**Rationale**: 深色主题配合霓虹色彩营造2026年安全科技感，符合年轻用户审美偏好。

## Risks / Trade-offs

### R1: 数据库解锁失败
- **Risk**: 用户忘记密码或Keystore损坏导致数据无法解密
- **Mitigation**: 提供密码重置选项（数据清空），支持生物识别作为备用解锁方式

### R2: 预警遗漏
- **Risk**: 停用计划设置后预警未及时触发
- **Mitigation**: 使用WorkManager定时检查停用计划，每小时轮询一次

### R3: 绑定关系复杂度
- **Risk**: 用户不理解绑定用途概念，输入错误
- **Mitigation**: 提供默认用途选项，引导用户选择常见用途组合

### R4: 应用预置清单维护
- **Risk**: 预置应用清单过时或不完整
- **Mitigation**: 允许用户自定义添加应用，预置清单仅作为快捷入口