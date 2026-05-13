# Hermes - 聚合设计说明

> 定义领域聚合根、实体和值对象的详细设计

---

## 目录

1. [聚合设计原则](#一聚合设计原则)
2. [身份标识聚合](#二身份标识聚合)
3. [应用账户聚合](#三应用账户聚合)
4. [应用聚合](#四应用聚合)
5. [预警聚合](#五预警聚合)
6. [绑定历史聚合](#六绑定历史聚合)
7. [聚合关系图](#七聚合关系图)

---

## 一、聚合设计原则

| 原则 | 说明 |
|------|------|
| **边界清晰** | 每个聚合有明确的边界，聚合内数据强一致性 |
| **单一职责** | 每个聚合只负责一个核心业务概念 |
| **根实体控制** | 聚合根作为唯一入口，控制对聚合内实体的访问 |
| **事务边界** | 聚合是事务一致性的边界 |
| **避免循环依赖** | 聚合间通过ID引用，避免直接对象引用 |

---

## 二、身份标识聚合

### 2.1 聚合根：IdentityIdentifier（IID）

| 属性 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | 自增 |
| type | IdentifierType | 标识类型 | PHONE/EMAIL |
| value | String | 标识值（明文） | 非空，唯一（同类型） |
| status | IdentifierStatus | 标识状态 | 非空 |
| plannedDeactTime | Instant? | 计划停用时间 | 可选 |
| deactReason | String? | 停用原因 | 可选 |
| createdAt | Instant | 创建时间 | 非空 |
| updatedAt | Instant | 更新时间 | 非空 |

> **显示策略**：APP中标识值默认全文显示，不进行脱敏处理。

**行为方法**：

| 方法 | 参数 | 返回值 | 说明 |
|------|------|--------|------|
| scheduleDeactivation(time, reason) | Instant, String | Unit | 设置停用计划 |
| cancelDeactivation() | - | Unit | 取消停用计划 |
| activate() | - | Unit | 激活标识 |
| deactivate() | - | Unit | 停用标识 |

### 2.2 实体：IdentifierDeactivation

| 属性 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | 自增 |
| identifierId | Long | 关联标识 | 外键 |
| deactType | DeactivationType | 停用类型 | SCHEDULED/IMMEDIATE |
| status | DeactivationStatus | 状态 | SCHEDULED/EXECUTED/CANCELLED |
| scheduledTime | Instant? | 计划时间 | 计划停用必填 |
| actualTime | Instant? | 实际停用时间 | 执行后填充 |
| reason | String? | 停用原因 | 可选 |
| cancelReason | String? | 取消原因 | 取消时填充 |
| createdAt | Instant | 创建时间 | 非空 |
| updatedAt | Instant | 更新时间 | 非空 |

---

## 三、应用账户聚合

### 3.1 聚合根：ApplicationAccount（AA）

| 属性 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | 自增 |
| applicationId | Long | 关联应用 | 外键 |
| accountName | String | 账户名/昵称 | 非空 |
| accountIdentifier | String? | 应用内唯一标识 | 可选 |
| status | AccountStatus | 账户状态 | 非空 |
| keepAliveEnabled | Boolean | 是否支持长期在线 | 默认true |
| lastLoginDate | LocalDate? | 最后登录日期 | 可选 |
| notes | String? | 备注 | 可选 |
| tags | List<String> | 标签列表 | JSON存储 |
| createdAt | Instant | 创建时间 | 非空 |
| updatedAt | Instant | 更新时间 | 非空 |

**行为方法**：

| 方法 | 参数 | 返回值 | 说明 |
|------|------|--------|------|
| bindIdentifier(identifierId, purposes, isPrimary) | Long, List<BindingPurpose>, Boolean | IdentifierBinding | 绑定身份标识 |
| unbindIdentifier(identifierId) | Long | Unit | 解绑身份标识 |
| updateStatus(newStatus) | AccountStatus | Unit | 更新状态 |
| addExtension(key, value, label, fieldType) | String, String, String, FieldType | AccountExtension | 添加扩展字段 |
| removeExtension(key) | String | Unit | 删除扩展字段 |

### 3.2 实体：IdentifierBinding（IB）

| 属性 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | 自增 |
| accountId | Long | 关联账户 | 外键 |
| identifierId | Long | 关联身份标识 | 外键 |
| purposes | List<BindingPurpose> | 用途列表 | 非空，至少一个 |
| isPrimary | Boolean | 是否主要标识 | 默认false |
| boundAt | Instant | 绑定时间 | 非空 |
| verifiedAt | Instant? | 验证时间 | 可选 |
| notes | String? | 备注 | 可选 |

### 3.3 实体：AccountExtension（AE）

| 属性 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | 自增 |
| accountId | Long | 关联账户 | 外键 |
| key | String | 字段标识 | 唯一（同账户） |
| value | String? | 字段值 | 可选 |
| label | String | 显示名称 | 非空 |
| fieldType | FieldType | 字段类型 | STRING/DATE/BOOL/ENUM/INTEGER |
| options | List<String>? | ENUM类型可选值 | 可选 |
| createdAt | Instant | 创建时间 | 非空 |
| updatedAt | Instant | 更新时间 | 非空 |

---

## 四、应用聚合

### 4.1 聚合根：Application（App）

| 属性 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | 自增 |
| name | String | 应用名称 | 非空，唯一 |
| type | ApplicationType | 应用类型 | WEB_SITE/MOBILE_APP/BOTH |
| officialUrl | String? | 官网地址 | 可选 |
| iconUrl | String? | 图标路径 | 可选 |
| category | String? | 分类 | 可选 |
| isActive | Boolean | 是否启用 | 默认true |
| createdAt | Instant | 创建时间 | 非空 |
| updatedAt | Instant | 更新时间 | 非空 |

---

## 五、预警聚合

### 5.1 聚合根：WarningRecord

| 属性 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | 自增 |
| identifierId | Long? | 关联身份标识 | 可空（账户预警） |
| accountId | Long? | 关联账户 | 可空（标识预警） |
| warningType | WarningType | 预警类型 | 非空 |
| warningLevel | WarningLevel | 预警级别 | 非空 |
| message | String | 预警消息 | 非空 |
| triggeredAt | Instant | 触发时间 | 非空 |
| isRead | Boolean | 是否已读 | 默认false |
| isHandled | Boolean | 是否已处理 | 默认false |
| handledAt | Instant? | 处理时间 | 可选 |

**行为方法**：

| 方法 | 参数 | 返回值 | 说明 |
|------|------|--------|------|
| markAsRead() | - | Unit | 标记已读 |
| handle() | - | Unit | 标记已处理 |

---

## 六、绑定历史聚合

### 6.1 聚合根：BindingHistoryRecord

| 属性 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | Long | 主键 | 自增 |
| accountId | Long | 关联账户 | 外键 |
| identifierId | Long | 关联身份标识 | 外键 |
| actionType | ActionType | 操作类型 | BIND/UNBIND/CHANGE_PURPOSE/REBIND/SWITCH_IDENTIFIER |
| previousPurposes | List<BindingPurpose>? | 变更前用途 | 可选 |
| newPurposes | List<BindingPurpose>? | 变更后用途 | 可选 |
| previousIdentifierId | Long? | 原标识ID（更换时） | 可选 |
| newIdentifierId | Long? | 新标识ID（更换时） | 可选 |
| actionAt | Instant | 操作时间 | 非空 |
| actionBy | String? | 操作者标识 | 可选 |
| notes | String? | 操作备注 | 可选 |

---

## 七、聚合关系图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Hermes 聚合关系图                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  IdentityIdentifierAggregate                                        │
│  ┌──────────────────────────────┐                                  │
│  │   IdentityIdentifier (IID)   │ ←── 聚合根                        │
│  │  - id: Long                  │                                  │
│  │  - type: IdentifierType      │                                  │
│  │  - value: String             │                                  │
│  │  - status: IdentifierStatus  │                                  │
│  │  - plannedDeactTime          │                                  │
│  └────────────────┬─────────────┘                                  │
│                   │ 1:N                                            │
│                   ▼                                                │
│  ┌──────────────────────────────┐                                  │
│  │   IdentifierDeactivation     │ ←── 实体                          │
│  │  - identifierId              │                                  │
│  │  - deactType                 │                                  │
│  │  - scheduledTime             │                                  │
│  │  - status                    │                                  │
│  └──────────────────────────────┘                                  │
│                                                                     │
│  ApplicationAccountAggregate                                        │
│  ┌──────────────────────────────┐                                  │
│  │   ApplicationAccount (AA)    │ ←── 聚合根                        │
│  │  - id: Long                  │                                  │
│  │  - applicationId: Long       │                                  │
│  │  - accountName: String       │                                  │
│  │  - status: AccountStatus     │                                  │
│  │  - keepAliveEnabled          │                                  │
│  └────────────────┬─────────────┘                                  │
│                   │ 1:N                                            │
│         ┌─────────┼─────────┐                                       │
│         ▼         ▼         ▼                                       │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐                │
│  │IdentifierBind│ │AccountExt(AE)│ │AccountRelation│                │
│  │  (IB)        │ │  - key/value │ │  - account2Id │                │
│  │  - purposes  │ │  - label     │ │  - relationType│               │
│  │  - isPrimary │ │              │ │               │                │
│  └──────────────┘ └──────────────┘ └──────────────┘                │
│                                                                     │
│  ApplicationAggregate                                               │
│  ┌──────────────────────────────┐                                  │
│  │   Application (App)          │ ←── 聚合根                        │
│  │  - id: Long                  │                                  │
│  │  - name: String              │                                  │
│  │  - type: ApplicationType     │                                  │
│  │  - category: String          │                                  │
│  └──────────────────────────────┘                                  │
│                                                                     │
│  WarningAggregate                                                   │
│  ┌──────────────────────────────┐                                  │
│  │   WarningRecord              │ ←── 聚合根                        │
│  │  - identifierId              │                                  │
│  │  - warningLevel              │                                  │
│  │  - message                   │                                  │
│  │  - isHandled                 │                                  │
│  └──────────────────────────────┘                                  │
│                                                                     │
│  BindingHistoryAggregate                                            │
│  ┌──────────────────────────────┐                                  │
│  │   BindingHistoryRecord       │ ←── 聚合根                        │
│  │  - accountId                 │                                  │
│  │  - identifierId              │                                  │
│  │  - actionType                │                                  │
│  │  - actionAt                  │                                  │
│  └──────────────────────────────┘                                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

*文档版本: v2.0（NIST 标准术语）*
*创建日期: 2026-05-11*
*最后更新: 2026-05-12*