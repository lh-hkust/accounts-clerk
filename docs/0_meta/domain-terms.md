# Hermes - 统一语言术语表

> 定义领域核心概念，统一团队沟通语言

---

## 目录

1. [核心概念区分](#一核心概念区分)
2. [身份标识相关术语](#二身份标识相关术语)
3. [认证凭据相关术语](#三认证凭据相关术语)
4. [应用账户相关术语](#四应用账户相关术语)
5. [预警相关术语](#五预警相关术语)
6. [状态枚举定义](#六状态枚举定义)
7. [关系术语](#七关系术语)
8. [命名规范](#八命名规范)
9. [UI界面用语映射表](#九ui界面用语映射表)

---

## 一、核心概念区分

基于 NIST SP 800-63B 标准，身份管理领域有三个核心概念：

| 概念 | 英文 | 定义 | 示例 | 说明 | UI界面用语 |
|------|------|------|------|------|------|
| **身份标识** | IdentityIdentifier / 标识 | 用于声明身份的唯一标识，是"我是谁"的声明 | 手机号、邮箱、用户名 | 本身不证明身份，仅声明 | 验证渠道 / Verification Channel |
| **认证凭据** | AuthenticationCredential / 凭据 | 用于证明身份的手段或因子 | 密码、OTP令牌、硬件密钥、证书 | 用于验证身份声明 | (V2.0规划，暂不展示) |
| **认证凭据组合** | CredentialPair / 凭据组合 | 标识 + 凭据的完整认证包 | 用户名+密码、手机号+SMS验证码 | 完整认证数据 | (V2.0规划，暂不展示) |

**类比**：
- 身份标识 = 名字写在证件上
- 认证凭据 = 证件的防伪特征（照片、芯片）
- 认证凭据组合 = 完整证件本身

### 1.1 Hermes 管理范围

| 版本 | 管理范围 | 说明 |
|------|----------|------|
| **V1.0（当前）** | 身份标识、应用账户、标识绑定 | 手机号/邮箱与应用账户的关系 |
| **V2.0（规划）** | 认证凭据管理 | 密码、令牌的安全存储与检索 |

---

## 二、身份标识相关术语

| 术语 | 英文 | 简称 | 定义 | 备注 | UI界面用语 |
|------|------|------|------|------|------|
| **身份标识** | IdentityIdentifier | 标识 / IID | 用于身份验证或安全验证的唯一标识 | 手机号、邮箱 | 验证渠道 / Verification Channel |
| **标识类型** | IdentifierType | - | 身份标识的分类 | PHONE（手机号）、EMAIL（邮箱） | 手机号 / 邮箱 |
| **标识状态** | IdentifierStatus | - | 身份标识的生命周期状态 | ACTIVE、PENDING_DEACTIVATION、DEACTIVATED、INVALIDATED | 正常使用 / 即将到期 / 已失效 |
| **计划停用** | ScheduledDeactivation | - | 用户预设的标识停用计划 | 设置某手机号将在 2026-12-31 停用 | 即将到期 / Expiring Soon |
| **标识值** | IdentifierValue | - | 身份标识的原始值 | 13812345678、test@qq.com | (直接显示) |
| **停用原因** | DeactivationReason | - | 标识停用的原因记录 | "更换新手机号" | (详情页展示) |

---

## 三、认证凭据相关术语

| 术语 | 英文 | 简称 | 定义 | 备注 | UI界面用语 |
|------|------|------|------|------|------|
| **认证凭据** | AuthenticationCredential | 凭据 / AC | 用于证明身份的机密信息 | 密码、令牌、证书 | (V2.0规划) |
| **凭据类型** | CredentialType | - | 认证凭据的分类 | PASSWORD、TOKEN、CERTIFICATE | (V2.0规划) |
| **密码** | Password | - | 用户记忆的秘密字符串 | 符合 NIST Memorized Secret | (V2.0规划) |
| **令牌** | Token | - | 动态生成的认证码 | OTP、TOTP、硬件令牌 | (V2.0规划) |
| **证书** | Certificate | - | 包含身份信息和密钥的数字证书 | X.509 证书 | (V2.0规划) |

### 3.1 NIST 认证器分类对应

| 凭据类型 | NIST 分类 | 因子类别 | AAL 级别 |
|----------|----------|----------|----------|
| Password | Memorized Secret | Something you KNOW | AAL1 |
| Token (软件OTP) | Single-Factor OTP Device | Something you HAVE | AAL1-AAL2 |
| Certificate | Cryptographic Device | Something you HAVE | AAL1-AAL3 |
| Hardware Token | Multi-Factor Cryptographic Device | Something you HAVE | AAL2-AAL3 |

---

## 四、应用账户相关术语

| 术语 | 英文 | 简称 | 定义 | 备注 | UI界面用语 |
|------|------|------|------|------|------|
| **应用平台** | Application | 应用 / App | 用户拥有账户的第三方网络服务 | 微博、淘宝、抖音 | (应用图标+名称) |
| **应用类型** | ApplicationType | - | 应用平台的载体类型 | WEB_SITE、MOBILE_APP、BOTH | (不展示) |
| **应用账户** | ApplicationAccount | 账户 / AA | 用户在应用中注册的账户 | 微博账号、淘宝账号 | 账号 / Account |
| **账户名** | AccountName | - | 用户在应用中的显示名称 | 微博昵称 | (直接显示) |
| **账户标识** | AccountIdentifier | - | 用户在应用中的唯一标识符 | 用户名、用户ID | ID / (直接显示) |
| **账户状态** | AccountStatus | - | 账户的生命周期状态 | ACTIVE、FROZEN、LOST、ARCHIVED | 正常使用 / 已冻结 / 已丢失 / 已归档 |
| **长期在线偏好** | KeepAlivePreference | - | 账户是否支持长期在线的设置 | 微博长期在线，抖音自动登出 | 长期在线 / Keep Online |
| **标识绑定** | IdentifierBinding | 绑定 / IB | 账户与身份标识的关联关系 | 淘宝账号绑定了手机号 | 绑定 / Binding |
| **绑定用途** | BindingPurpose | - | 身份标识在账户中的具体用途 | LOGIN、VERIFICATION、RECOVERY、NOTIFICATION、SECONDARY_AUTH | 登录 / 验证 / 找回 / 通知 / 二次验证 |
| **账户扩展信息** | AccountExtension | 扩展 / AE | 账户的额外属性 | 注册日期、地区、实名认证 | (详情页展示) |
| **账户关联** | AccountRelation | - | 同一用户在同一应用的多账户关系 | 主号与小号关系 | 关联账号 / Related Accounts |

---

## 五、预警相关术语

| 术语 | 英文 | 简称 | 定义 | 备注 | UI界面用语 |
|------|------|------|------|------|------|
| **预警** | Warning | - | 系统对潜在风险的提醒 | 身份标识即将停用预警 | 提醒 / Reminder |
| **预警类型** | WarningType | - | 预警的分类 | IDENTIFIER_PENDING_DEACT、IDENTIFIER_DEACTIVATED、ACCOUNT_FROZEN | (不直接展示) |
| **预警级别** | WarningLevel | - | 预警的紧急程度 | HIGH、MEDIUM、LOW | 紧急处理 / 建议处理 / 可稍后处理 |
| **影响范围** | ImpactScope | - | 标识变更影响的账户集合 | 手机号停用影响5个账户 | 受影响账号 / Affected Accounts |
| **预警状态** | WarningStatus | - | 预警的处理状态 | UNREAD、READ、HANDLED | 未读 / 已读 / 已处理 |
| **看板** | Dashboard | - | 数据统计展示视图 | 首页预警概览 | 概览 / Overview |

---

## 六、状态枚举定义

### 6.1 标识状态 IdentifierStatus

| 状态 | 英文 | 含义 | 触发条件 | UI界面用语 |
|------|------|------|----------|------|
| **正常** | ACTIVE | 标识可正常使用 | 标识创建 | 正常使用 / Active |
| **待停用** | PENDING_DEACTIVATION | 已设置计划停用时间 | 用户设置计划停用 | 即将到期 / Expiring Soon |
| **已停用** | DEACTIVATED | 标识已停用（换号、注销） | 计划时间到达或手动停用 | 已失效 / Invalidated |
| **已失效** | INVALIDATED | 标识验证失败 | 手机号被运营商回收 | 已失效 / Invalidated |

### 6.2 账户状态 AccountStatus

| 状态 | 英文 | 含义 | 触发条件 | UI界面用语 |
|------|------|------|----------|------|
| **正常** | ACTIVE | 账户可正常使用 | 账户创建 | 正常使用 / Active |
| **冻结** | FROZEN | 因标识停用导致账户暂停使用 | 关联标识停用 | 已冻结 / Frozen |
| **丢失** | LOST | 所有找回渠道不可用，无法找回 | 用户标记"已丢失" | 已丢失 / Lost |
| **归档** | ARCHIVED | 已归档，隐藏显示 | 用户主动归档或账户注销 | 已归档 / Archived |

### 6.3 预警级别 WarningLevel

| 级别 | 英文 | 含义 | 适用场景 | UI界面用语 |
|------|------|------|----------|------|
| **高** | HIGH | 紧急预警，需立即处理 | 标识即将停用，影响敏感账户 | 紧急处理 / Urgent |
| **中** | MEDIUM | 中等预警，建议尽快处理 | 标识即将停用，影响普通账户 | 建议处理 / Recommended |
| **低** | LOW | 低级别预警，可稍后处理 | 标识状态变更通知 | 可稍后处理 / Low Priority |

### 6.4 绑定用途 BindingPurpose

| 用途 | 英文 | 说明 | UI界面用语 |
|------|------|------|------|
| **登录** | LOGIN | 作为登录标识（用户名） | 登录 / Login |
| **验证** | VERIFICATION | 安全验证（接收验证码） | 验证 / Verification |
| **找回** | RECOVERY | 找回密码/账户 | 找回 / Recovery |
| **通知** | NOTIFICATION | 接收通知消息 | 通知 / Notification |
| **二次验证** | SECONDARY_AUTH | 双因素认证 | 二次验证 / 2FA |

---

## 七、关系术语

| 术语 | 英文 | 定义 | UI界面用语 |
|------|------|------|------|
| **账户关联** | AccountRelation | 同一用户在同一应用的多账户关系（无主次之分） | 关联账号 / Related Accounts |
| **标识绑定** | IdentifierBinding | 账户与身份标识的关联关系，包含用途属性 | 绑定关系 / Binding |
| **影响分析** | ImpactAnalysis | 分析标识变更对账户的影响范围 | 影响范围 / Impact Scope |
| **绑定历史** | BindingHistory | 记录标识绑定关系的历史变更 | 变更记录 / History |

---

## 八、命名规范

### 8.1 代码命名

- **聚合根**: `IdentityIdentifier`、`ApplicationAccount`、`Application`、`WarningRecord`
- **实体**: `IdentifierBinding`、`AccountExtension`、`IdentifierDeactivation`
- **值对象**: `IdentifierType`、`AccountStatus`、`BindingPurpose`、`CredentialType`
- **枚举**: 使用 PascalCase，如 `IdentifierStatus.ACTIVE`
- **属性**: 使用 camelCase，如 `plannedDeactivationTime`
- **方法**: 使用 camelCase，如 `createIdentifier()`
- **常量**: 使用 UPPER_SNAKE_CASE，如 `MAX_WARNING_DAYS`

### 8.2 数据表命名

- **身份标识表**: `identity_identifier`
- **应用平台表**: `application`
- **应用账户表**: `application_account`
- **标识绑定表**: `identifier_binding`
- **账户关联表**: `account_relation`
- **预警表**: `warning_record`
- **停用计划表**: `identifier_deactivation`
- **绑定历史表**: `identifier_binding_history`
- **认证凭据表（规划）**: `authentication_credential`

### 8.3 简写对照表

| 全称 | 简写 | 使用场景 |
|------|------|----------|
| IdentityIdentifier | IID | 代码注释、日志 |
| ApplicationAccount | AA | 代码注释、日志 |
| IdentifierBinding | IB | 代码注释、日志 |
| AuthenticationCredential | AC | 代码注释、日志 |
| AccountExtension | AE | 代码注释、日志 |

---

# 九 ui界面用语映射表
见: [UI词汇表](ui-glossary.md)

---

*文档版本: v4.0*
*创建日期: 2026-05-11*
*最后更新: 2026-05-12*