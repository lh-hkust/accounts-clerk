# Hermes - TDD流程说明

> 定义测试驱动开发的流程和实践指南

---

## 目录

1. [TDD概述](#一tdd概述)
2. [红-绿-重构循环](#二红-绿-重构循环)
3. [测试类型](#三测试类型)
4. [测试结构](#四测试结构)
5. [测试命名规范](#五测试命名规范)
6. [Mock与依赖注入](#六mock与依赖注入)
7. [测试覆盖率](#七测试覆盖率)
8. [CI/CD集成](#八cicd集成)

---

## 一、TDD概述

### 1.1 TDD定义

测试驱动开发（Test-Driven Development）是一种软件开发方法论，核心流程：
1. 先写失败的测试
2. 编写最小化代码使测试通过
3. 重构代码提升质量

### 1.2 TDD优势

| 优势 | 说明 |
|------|------|
| **质量保障** | 代码有测试覆盖，减少回归Bug |
| **设计驱动** | 测试驱动代码设计，职责清晰 |
| **文档作用** | 测试用例作为活文档 |
| **快速反馈** | 快速发现问题，降低修复成本 |

---

## 二、红-绿-重构循环

### 2.1 流程说明

```
┌─────────────────────────────────────────────────────────────┐
│                    TDD 红-绿-重构循环                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐                                           │
│  │   红 (Red)  │ ←── 编写失败的测试用例                      │
│  └──────┬──────┘                                           │
│         │                                                   │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │   绿 (Green)│ ←── 编写最小化代码使测试通过                 │
│  └──────┬──────┘                                           │
│         │                                                   │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │ 重构(Refactor)│ ←── 优化代码结构，不改变行为               │
│  └──────┬──────┘                                           │
│         │                                                   │
│         └───────────→ 重复循环                               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 实践要点

| 阶段 | 要点 |
|------|------|
| **红** | 只写一个测试，确保它失败 |
| **绿** | 只写使测试通过的最小代码 |
| **重构** | 改进代码质量，保持测试通过 |

---

## 三、测试类型

### 3.1 测试金字塔

```
        UI测试 (少量)
           ↓
     集成测试 (中等)
           ↓
     单元测试 (大量)
```

### 3.2 测试类型说明

| 类型 | 层级 | 覆盖范围 | 目的 |
|------|------|----------|------|
| **单元测试** | 领域层/数据层 | 单个类/方法 | 验证业务逻辑正确性 |
| **集成测试** | 跨层 | 多个组件协作 | 验证模块间集成 |
| **UI测试** | UI层 | 用户交互流程 | 验证端到端流程 |

---

## 四、测试结构

### 4.1 测试文件组织

```
hermes/
├── app/
│   └── src/
│       ├── test/
│       │   └── java/com/hermes/
│       │       ├── viewmodels/    # ViewModel测试
│       │       └── usecases/      # UseCase测试
│       └── androidTest/
│           └── java/com/hermes/
│               └── ui/            # UI集成测试
├── domain/
│   └── src/
│       └── test/
│           └── java/com/hermes/domain/
│               ├── entities/      # 实体测试
│               ├── services/      # 领域服务测试
│               └── events/        # 领域事件测试
└── data/
    └── src/
        └── test/
            └── java/com/hermes/data/
                ├── repositories/  # Repository测试
                ├── sources/       # 数据源测试
                └── security/      # 安全模块测试
```

### 4.2 测试类命名

| 被测类 | 测试类 | 示例 |
|--------|--------|------|
| `CredentialIdentifier` | `CredentialIdentifierTest` | `CredentialIdentifierTest.kt` |
| `CreateCredentialUseCase` | `CreateCredentialUseCaseTest` | `CreateCredentialUseCaseTest.kt` |
| `CredentialRepositoryImpl` | `CredentialRepositoryImplTest` | `CredentialRepositoryImplTest.kt` |

---

## 五、测试命名规范

### 5.1 测试方法命名

**格式**: `should[预期结果]When[条件]`

| 示例 | 说明 |
|------|------|
| `shouldCreateCredentialWhenValidInput()` | 验证有效输入时创建凭证 |
| `shouldThrowExceptionWhenCredentialExists()` | 验证凭证已存在时抛出异常 |
| `shouldReturnEmptyListWhenNoCredentials()` | 验证无凭证时返回空列表 |

### 5.2 测试方法结构

```kotlin
@Test
fun `shouldCreateCredentialWhenValidInput`() {
    // Given - 设置测试条件
    val repository = mock<CredentialRepository>()
    val useCase = CreateCredentialUseCase(repository)
    val input = CredentialInput("PHONE", "13812345678")
    
    // When - 执行被测方法
    val result = useCase.execute(input)
    
    // Then - 验证结果
    assertThat(result.isSuccess).isTrue()
    verify(repository).save(any())
}
```

---

## 六、Mock与依赖注入

### 6.1 Mock使用原则

| 原则 | 说明 |
|------|------|
| **外部依赖** | Mock外部依赖（Repository、API等） |
| **被测类** | 不Mock被测类本身 |
| **验证交互** | 使用verify验证方法调用 |
| **返回预设值** | 使用whenever预设返回值 |

### 6.2 Mock库选择

| 库 | 用途 |
|----|------|
| **MockK** | Kotlin首选Mock库 |
| **Mockito-Kotlin** | Mockito的Kotlin扩展 |

### 6.3 依赖注入测试

```kotlin
class CredentialRepositoryImplTest {
    private lateinit var dao: CredentialDao
    private lateinit var repository: CredentialRepositoryImpl
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.credentialDao()
        repository = CredentialRepositoryImpl(dao)
    }
    
    @Test
    fun `shouldSaveCredentialSuccessfully`() = runBlocking {
        // Given
        val credential = CredentialIdentifier(...)
        
        // When
        repository.save(credential)
        
        // Then
        val saved = repository.findById(credential.id)
        assertThat(saved).isEqualTo(credential)
    }
}
```

---

## 七、测试覆盖率

### 7.1 覆盖率目标

| 模块 | 目标覆盖率 |
|------|-----------|
| **领域层** | ≥ 90% |
| **数据层** | ≥ 80% |
| **应用层** | ≥ 80% |
| **UI层** | ≥ 60% |

### 7.2 覆盖率工具

| 工具 | 用途 |
|------|------|
| **JaCoCo** | 代码覆盖率统计 |
| **Android Studio** | 内置覆盖率工具 |

### 7.3 覆盖率报告

```groovy
// build.gradle
jacoco {
    toolVersion = "0.8.11"
}

tasks.withType(Test) {
    jacoco.includeNoLocationClasses = true
}

task jacocoTestReport(type: JacocoReport) {
    reports {
        html.required = true
        xml.required = true
    }
}
```

---

## 八、CI/CD集成

### 8.1 持续集成流程

```
代码提交 → CI触发 → 编译 → 单元测试 → 集成测试 → 覆盖率检查 → 构建APK
```

### 8.2 测试命令

| 命令 | 说明 |
|------|------|
| `./gradlew test` | 运行单元测试 |
| `./gradlew connectedAndroidTest` | 运行集成测试 |
| `./gradlew jacocoTestReport` | 生成覆盖率报告 |

### 8.3 质量门禁

| 检查项 | 阈值 |
|--------|------|
| 单元测试通过率 | 100% |
| 代码覆盖率 | ≥ 80% |
| 静态分析 | 无严重问题 |

---

*文档版本: v1.0*  
*创建日期: 2026-05-11*  
*最后更新: 2026-05-11*