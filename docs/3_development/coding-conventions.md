# Hermes - 编码规约

> 定义代码编写规范和最佳实践

---

## 目录

1. [命名规范](#一命名规范)
2. [代码风格](#二代码风格)
3. [架构规范](#三架构规范)
4. [TDD规范](#四tdd规范)
5. [安全规范](#五安全规范)
6. [错误处理](#六错误处理)

---

## 一、命名规范

### 1.1 包命名

| 类型 | 规则 | 示例 |
|------|------|------|
| **包名** | 全小写，使用点分隔 | `com.hermes.domain` |
| **模块包** | 按层级划分 | `com.hermes.domain.entities` |

### 1.2 类命名

| 类型 | 规则 | 示例 |
|------|------|------|
| **实体类** | PascalCase，以实体名命名 | `CredentialIdentifier` |
| **值对象** | PascalCase | `CredentialType` |
| **枚举** | PascalCase | `AccountStatus` |
| **UseCase** | 动词+名词+UseCase | `CreateCredentialUseCase` |
| **Repository** | 实体名+Repository | `CredentialRepository` |
| **ViewModel** | 页面名+ViewModel | `CredentialListViewModel` |
| **DAO** | 实体名+Dao | `CredentialDao` |

### 1.3 方法命名

| 类型 | 规则 | 示例 |
|------|------|------|
| **一般方法** | camelCase，动词开头 | `createCredential()` |
| **获取方法** | `getXxx()` 或 `findXxx()` | `getCredentialById()` |
| **查询方法** | `findXxx()` | `findByStatus()` |
| **保存方法** | `saveXxx()` | `saveCredential()` |
| **删除方法** | `deleteXxx()` | `deleteCredential()` |
| **布尔方法** | `isXxx()` 或 `hasXxx()` | `isActive()` |

### 1.4 属性命名

| 类型 | 规则 | 示例 |
|------|------|------|
| **普通属性** | camelCase | `accountName` |
| **布尔属性** | `isXxx` 或 `hasXxx` | `isActive`, `hasDeactivationPlan` |
| **集合属性** | 复数形式 | `credentials`, `bindings` |
| **常量** | UPPER_SNAKE_CASE | `MAX_WARNING_DAYS` |

---

## 二、代码风格

### 2.1 格式规范

| 规则 | 说明 |
|------|------|
| **缩进** | 4个空格 |
| **换行** | 每行不超过120字符 |
| **空行** | 方法之间空一行，逻辑块之间空一行 |
| **大括号** | 行尾风格（Kotlin默认） |

### 2.2 Kotlin 特定规范

| 规则 | 示例 |
|------|------|
| **数据类** | 使用 `data class` | `data class Credential(val id: Long)` |
| **单例** | 使用 `object` 或 `by lazy` | `object AppConfig` |
| **扩展函数** | 定义在顶层或 companion object | `fun String.toMasked()` |
| **空安全** | 使用 `?` 和 `?:` | `value?.length ?: 0` |
| **when表达式** | 覆盖所有分支或使用 `else` | `when (status) { ... }` |

### 2.3 注释规范

| 类型 | 规则 |
|------|------|
| **文件头部** | 不写版权信息 |
| **类注释** | 不写无用注释，使用KDoc格式 |
| **方法注释** | 仅对复杂逻辑写注释 |
| **行注释** | 尽量少用，优先通过命名表达意图 |

---

## 三、架构规范

### 3.1 Clean Architecture 分层

| 层级 | 包名 | 职责 |
|------|------|------|
| **UI层** | `app.ui`, `app.viewmodels`, `app.navigation` | 界面展示和交互 |
| **应用层** | `app.usecases` | 用例编排 |
| **领域层** | `domain.entities`, `domain.valueobjects`, `domain.repositories`, `domain.services`, `domain.events` | 核心业务逻辑 |
| **数据层** | `data.repositories`, `data.sources`, `data.model`, `data.security` | 数据持久化 |

### 3.2 依赖规则

| 规则 | 说明 |
|------|------|
| **依赖方向** | 外层依赖内层，内层不依赖外层 |
| **领域层** | 无外部依赖，纯Kotlin |
| **数据层** | 依赖领域层，不依赖UI层 |
| **UI层** | 依赖应用层和数据层 |

### 3.3 Repository 模式

| 规则 | 说明 |
|------|------|
| **接口定义** | 在领域层定义Repository接口 |
| **实现** | 在数据层实现Repository接口 |
| **返回类型** | 使用 `Flow` 或 `suspend` 函数 |

---

## 四、TDD规范

### 4.1 测试原则

| 原则 | 说明 |
|------|------|
| **先写测试** | 遵循红-绿-重构循环 |
| **单一职责** | 每个测试方法只测试一个场景 |
| **测试命名** | `shouldXxxWhenYyy()` |
| **边界测试** | 测试边界条件和异常场景 |

### 4.2 测试覆盖率

| 类型 | 目标 |
|------|------|
| **单元测试** | ≥ 80% |
| **集成测试** | ≥ 60% |
| **UI测试** | 关键路径覆盖 |

### 4.3 测试文件结构

```
hermes/
├── app/
│   └── src/
│       ├── test/           # 单元测试
│       └── androidTest/    # 集成测试
├── domain/
│   └── src/
│       └── test/           # 领域层测试
└── data/
    └── src/
        └── test/           # 数据层测试
```

---

## 五、安全规范

### 5.1 数据加密

| 规则 | 说明 |
|------|------|
| **敏感数据** | 存储时加密，显示时脱敏 |
| **密钥管理** | 使用Android Keystore存储密钥 |
| **密码派生** | 使用PBKDF2-HMAC-SHA256 |
| **加密算法** | AES-256-GCM |

### 5.2 身份验证

| 操作 | 验证要求 |
|------|----------|
| 查看完整凭证 | 指纹/密码验证 |
| 修改凭证 | 指纹/密码验证 |
| 删除凭证 | 指纹/密码验证 |
| 导出数据 | 指纹/密码验证 |

### 5.3 日志安全

| 规则 | 说明 |
|------|------|
| **禁止日志** | 不打印敏感信息（凭证值、密码等） |
| **脱敏日志** | 日志中只记录脱敏值 |

---

## 六、错误处理

### 6.1 异常分类

| 类型 | 说明 | 示例 |
|------|------|------|
| **业务异常** | 预期内的业务错误 | `CredentialAlreadyExistsException` |
| **技术异常** | 系统错误 | `DatabaseException` |
| **验证异常** | 输入验证失败 | `ValidationException` |

### 6.2 异常处理

| 规则 | 说明 |
|------|------|
| **向上抛出** | 在UseCase层捕获并转换为业务异常 |
| **用户友好** | 向用户显示友好的错误消息 |
| **日志记录** | 记录异常堆栈用于调试 |

### 6.3 资源管理

| 规则 | 说明 |
|------|------|
| **数据库连接** | 使用Room自动管理 |
| **文件操作** | 使用try-with-resources |
| **内存管理** | 避免内存泄漏 |

---

*文档版本: v1.0*  
*创建日期: 2026-05-11*  
*最后更新: 2026-05-11*