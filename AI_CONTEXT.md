# Hermes 项目 AI 协作规约

## 1. 项目定位
Hermes 是一款账号管理工具，帮助用户在手机号/邮箱停用前，查看所有绑定账号并批量更新绑定关系。数据完全本地存储，使用 SQLCipher + Android Keystore 加密。

## 2. 方法论约束
- **DDD**：必须围绕领域模型设计代码。核心聚合根是 `ApplicationAccount`，不变量：同一应用内账户标识必须唯一，同一账户同一身份标识不能重复绑定。
- **TDD**：所有业务逻辑必须有对应的单元测试。AI 在输出实现代码前，必须先输出测试用例。
- **OpenSpec**：所有需求变更必须通过 `specs/` 目录下的规格文件驱动。

## 3. 关键领域术语

基于 NIST SP 800-63B 标准：

| 概念 | 英文 | 简称 | 说明 |
|------|------|------|------|
| **身份标识** | IdentityIdentifier | IID / 标识 | 手机号/邮箱，声明身份的唯一标识 |
| **应用账户** | ApplicationAccount | AA / 账户 | 应用内注册的账户（微博账号、淘宝账号） |
| **标识绑定** | IdentifierBinding | IB / 绑定 | 账户与身份标识的关联关系 |
| **认证凭据** | AuthenticationCredential | AC / 凭据 | 密码、令牌、证书（用于证明身份） |

**版本管理范围**：
- V1.0：身份标识、应用账户、标识绑定、预警
- V2.0：认证凭据管理（密码、令牌安全存储）

**显示策略**：APP中身份标识值默认全文显示，不进行脱敏处理。

## 4. 文档索引

```
docs/
├── 0_meta/                    # 元信息
│   ├── architecture.md        # 架构设计
│   ├── domain-terms.md        # 统一语言术语表（权威来源）
│   └── vision.md              # 项目愿景
├── 1_product/                 # 产品需求
│   └ roadmap.md               # 产品路线图（含预置应用清单）
├── 2_design/                  # 设计文档
│   ├── data-model.md          # 数据模型
│   ├── ui-design.md           # UI设计规范
│   └ domain/
│       ├── domain-model.md    # 领域模型
│       ├── aggregates.md      # 聚合设计说明
│       └ event-storming.md    # 事件风暴分析
├── 3_development/             # 开发规范
│   ├── coding-conventions.md  # 编码规范
│   └ tdd-guide.md             # TDD指南
└── openspec/                  # OpenSpec规格
```

## 5. 输出格式要求
- 先给出 **测试用例**（TDD 红色阶段）。
- 然后给出实现代码，确保测试通过。
- 最后给出 **重构建议** 和 **对现有规格的影响分析**。
- 聚合根操作必须通过 Repository，禁止直接操作数据库。

## 6. 禁止行为
- 不要生成敏捷相关文档。
- 不要在未更新 `specs/` 的情况下直接修改代码。

---

*版本：v5.0（NIST 标准术语）*
*创建日期：2026-05-11*