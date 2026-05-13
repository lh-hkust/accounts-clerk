## Why

用户需要在手机号或邮箱停用前，了解所有绑定账号并提前处理，避免账号丢失或无法登录。当前市场上缺乏专门管理身份标识与账号绑定关系的工具，用户只能手动记录，容易遗漏关键账号。

## What Changes

- 新增身份标识管理功能：添加、查看、编辑、删除手机号/邮箱
- 新增停用计划功能：为身份标识设置计划停用日期和原因
- 新增预警系统功能：自动检测停用计划并生成预警提醒
- 新增应用账户管理功能：记录账号与身份标识的绑定关系
- 新增影响分析功能：显示身份标识变更影响的账户列表

## Capabilities

### New Capabilities

- `identity-identifier`: 身份标识基础管理 - 添加、查看、编辑、删除手机号/邮箱，防止重复添加，查看绑定账户列表
- `identifier-deactivation`: 身份标识停用计划 - 设置停用日期和原因，取消停用计划，状态流转控制
- `warning-system`: 预警系统 - 自动触发预警，级别计算，查看列表和详情，标记已读和处理
- `application-account`: 应用账户管理 - 添加、查看、编辑账户，绑定身份标识，管理绑定用途
- `identifier-binding`: 标识绑定管理 - 账户与身份标识的绑定关系，用途设置，绑定历史记录

### Modified Capabilities

<!-- 无现有能力变更 -->

## Impact

- 新增领域模型：IdentityIdentifier、ApplicationAccount、IdentifierBinding、WarningRecord、IdentifierDeactivation
- 新增数据表：identity_identifier、application_account、identifier_binding、warning_record、identifier_deactivation
- 新增UI页面：标识列表、标识详情、账户列表、账户详情、预警列表、预警详情、设置停用计划
- 数据库加密方案：SQLCipher + DEK+KEK 信封加密