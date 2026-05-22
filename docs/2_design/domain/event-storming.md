# Hermes - 事件风暴分析（精简版）

---

## 一、领域事件清单

### 1.1 身份标识相关事件

| 事件 | 英文标识 | 触发场景 | 关联实体 |
|------|----------|----------|----------|
| 身份标识创建 | IdentifierCreated | 用户添加新身份标识 | IdentityIdentifier |
| 身份标识修改 | IdentifierUpdated | 用户修改身份标识信息 | IdentityIdentifier |
| 身份标识删除 | IdentifierDeleted | 用户删除身份标识 | IdentityIdentifier |
| 停用计划创建 | DeactivationScheduled | 用户设置停用计划 | IdentifierDeactivation |
| 停用计划取消 | DeactivationCancelled | 用户取消停用计划 | IdentifierDeactivation |
| 身份标识停用执行 | IdentifierDeactivated | 身份标识到达停用时间 | IdentityIdentifier |
| 身份标识失效 | IdentifierInvalidated | 身份标识验证失败 | IdentityIdentifier |

### 1.2 应用账户相关事件

| 事件 | 英文标识 | 触发场景 | 关联实体 |
|------|----------|----------|----------|
| 账户创建 | AccountCreated | 用户添加新账户 | ApplicationAccount |
| 账户修改 | AccountUpdated | 用户修改账户信息 | ApplicationAccount |
| 账户删除 | AccountDeleted | 用户删除账户 | ApplicationAccount |
| 账户状态变更 | AccountStatusChanged | 用户/系统变更账户状态 | ApplicationAccount |
| 账户扩展添加 | AccountExtensionAdded | 用户添加扩展属性 | AccountExtension |
| 账户扩展删除 | AccountExtensionDeleted | 用户删除扩展属性 | AccountExtension |

### 1.3 标识绑定相关事件

| 事件 | 英文标识 | 触发场景 | 关联实体 |
|------|----------|----------|----------|
| 标识绑定 | IdentifierBound | 用户绑定身份标识到账户 | IdentifierBinding |
| 标识解绑 | IdentifierUnbound | 用户解绑身份标识 | IdentifierBinding |
| 绑定用途变更 | BindingPurposeChanged | 用户修改绑定用途 | IdentifierBinding |
| 绑定关系更换 | IdentifierSwitched | 用户更换绑定身份标识 | IdentifierBinding |

### 1.4 预警相关事件

| 事件 | 英文标识 | 触发场景 | 关联实体 |
|------|----------|----------|----------|
| 预警触发 | WarningTriggered | 系统检测到风险 | WarningRecord |
| 预警已读 | WarningRead | 用户查看预警 | WarningRecord |
| 预警处理 | WarningHandled | 用户处理预警 | WarningRecord |
| 预警清除 | WarningCleared | 风险解除 | WarningRecord |

---

## 二、命令清单

### 2.1 身份标识管理命令

| 命令 | 业务规则 |
|------|----------|
| CreateIdentifierCommand | 检测重复身份标识 |
| UpdateIdentifierCommand | 仅允许修改备注 |
| DeleteIdentifierCommand | 确认影响范围 |
| ScheduleDeactivationCommand | 停用时间必须大于当前时间 |
| CancelDeactivationCommand | 只能取消未执行的计划 |

### 2.2 应用账户管理命令

| 命令 | 业务规则 |
|------|----------|
| CreateAccountCommand | 检测重复账户 |
| UpdateAccountCommand | 允许修改名称、状态等 |
| DeleteAccountCommand | 确认删除 |
| UpdateAccountStatusCommand | 验证状态转换规则 |
| AddAccountExtensionCommand | 字段key唯一 |
| RemoveAccountExtensionCommand | 只能删除自定义字段 |

### 2.3 标识绑定管理命令

| 命令 | 业务规则 |
|------|----------|
| BindIdentifierCommand | 检测重复绑定 |
| UnbindIdentifierCommand | 记录解绑历史 |
| ChangeBindingPurposeCommand | 用途非空 |
| SwitchIdentifierCommand | 记录更换历史 |

### 2.4 预警管理命令

| 命令 | 业务规则 |
|------|----------|
| TriggerWarningCommand | 根据影响范围计算级别 |
| MarkWarningReadCommand | 更新已读状态 |
| HandleWarningCommand | 验证处理权限 |
| ClearWarningCommand | 仅清除已处理预警 |

---

## 三、聚合根列表

| 聚合 | 聚合根 | 包含实体 | 值对象 |
|------|--------|----------|--------|
| 身份标识聚合 | IdentityIdentifier (IID) | IdentifierDeactivation | IdentifierType, IdentifierStatus |
| 应用账户聚合 | ApplicationAccount (AA) | IdentifierBinding (IB), AccountExtension | AccountStatus, BindingPurpose |
| 应用聚合 | Application | - | ApplicationType |
| 预警聚合 | WarningRecord | - | WarningLevel, WarningType |
| 绑定历史聚合 | BindingHistoryRecord | - | ActionType |

---

*文档版本: v2.0（NIST 标准术语）*
*创建日期: 2026-05-11*
*最后更新: 2026-05-12*