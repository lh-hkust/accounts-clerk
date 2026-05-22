# Hermes - 数据模型设计

> 定义数据库表结构、实体关系和数据约束

---

## 目录

1. [数据库概述](#一数据库概述)
2. [核心数据表](#二核心数据表)
3. [实体关系图](#三实体关系图)
4. [数据导入导出格式](#四数据导入导出格式)
5. [数据安全设计](#五数据安全设计)

---

## 一、数据库概述

### 1.1 存储方案

| 属性 | 值 |
|------|------|
| **数据库类型** | SQLite + SQLCipher |
| **封装层** | Android Room |
| **加密方式** | DEK+KEK 信封加密 |
| **密钥存储** | Android Keystore / iOS Keychain |

### 1.2 设计原则

1. **数据库级加密**：整个数据库文件加密，数据表存储明文
2. **信封加密**：DEK加密数据库，KEK保护DEK
3. **关系完整性**：通过外键约束保证数据一致性
4. **可扩展性**：支持扩展字段和自定义属性
5. **便于检索**：明文存储支持SQL查询和索引

### 1.3 加密架构（可选密码 + DEK+KEK 信封加密）

```
┌─────────────────────────────────────────────────────────────────┐
│              可选密码 + DEK+KEK 信封加密架构                    │
├─────────────────────────────────────────────────────────────────┤
│                                                               │
│  用户选择是否设置密码                                           │
│        │                                                       │
│        ├── 是 ──→ 用户密码                                      │
│        │              │                                        │
│        │              ▼ PBKDF2-HMAC-SHA256 (100000次迭代)       │
│        │         ┌─────────────────┐                           │
│        │         │      KEK        │ ←── 密钥加密密钥            │
│        │         │  (存于Keystore) │                           │
│        │         └────────┬────────┘                           │
│        │                  │ AES-256-GCM                        │
│        │                  ▼                                    │
│        └──────────→ ┌─────────────────┐                        │
│                     │      DEK        │ ←── 数据加密密钥        │
│                     │  (加密后存DB)   │   (无密码时直接存储)    │
│                     └────────┬────────┘                        │
│                              │ SQLCipher                       │
│                              ▼                                 │
│                     ┌─────────────────┐                        │
│                     │  SQLite数据库   │ ←── 整个数据库文件加密   │
│                     │   (存储明文)    │                        │
│                     └─────────────────┘                        │
│                                                               │
│  无密码模式：DEK直接存储，数据库仍加密，仅无KEK保护              │
│  有密码模式：用户密码 → KEK → DEK → 解密数据库                  │
│                                                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、核心数据表

### 2.1 身份标识表 identity_identifier

```sql
CREATE TABLE identity_identifier (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT NOT NULL,                  -- PHONE / EMAIL
    value TEXT NOT NULL,                 -- 标识值（明文存储），便于检索和重复校验
    status TEXT NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE / PENDING_DEACTIVATION / DEACTIVATED / INVALIDATED
    planned_deact_time TEXT,             -- 计划停用时间 (ISO 8601格式)
    deact_reason TEXT,                   -- 停用原因
    created_at TEXT NOT NULL,            -- 创建时间
    updated_at TEXT NOT NULL             -- 更新时间
);

CREATE INDEX idx_identifier_status ON identity_identifier(status);
CREATE INDEX idx_identifier_planned_deact ON identity_identifier(planned_deact_time);
CREATE INDEX idx_identifier_type_value ON identity_identifier(type, value);
CREATE INDEX idx_identifier_value_like ON identity_identifier(value);
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键，自增 |
| type | TEXT | 标识类型：PHONE/EMAIL（对应 IdentifierType） |
| value | TEXT | 标识值（明文存储），便于检索和重复校验 |
| status | TEXT | 标识状态（对应 IdentifierStatus） |
| planned_deact_time | TEXT | 计划停用时间 |
| deact_reason | TEXT | 停用原因 |
| created_at | TEXT | 创建时间 |
| updated_at | TEXT | 更新时间 |

> **显示策略**：APP中身份标识值默认全文显示，不进行脱敏处理。数据库已加密，用户打开APP后即可看到完整值。

### 2.2 应用平台表 application

```sql
CREATE TABLE application (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,           -- 应用名称 (微博、淘宝)
    type TEXT NOT NULL DEFAULT 'BOTH',   -- WEB_SITE / MOBILE_APP / BOTH
    official_url TEXT,                   -- 官网地址
    icon_url TEXT,                       -- 图标地址（本地存储路径）
    category TEXT,                       -- 分类 (社交、金融、购物)
    is_active INTEGER NOT NULL DEFAULT 1, -- 是否启用
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE INDEX idx_application_name ON application(name);
CREATE INDEX idx_application_category ON application(category);
```

### 2.3 应用账户表 application_account

```sql
CREATE TABLE application_account (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    application_id INTEGER NOT NULL,     -- 关联应用平台
    account_name TEXT NOT NULL,          -- 账户名/昵称
    account_identifier TEXT,             -- 应用内唯一标识 (用户名)
    status TEXT NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / FROZEN / LOST / ARCHIVED
    keep_alive_enabled INTEGER NOT NULL DEFAULT 1, -- 是否支持长期在线
    last_login_date TEXT,                -- 最后登录日期
    notes TEXT,                          -- 备注
    tags TEXT,                           -- 标签 (JSON数组)
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    
    FOREIGN KEY (application_id) REFERENCES application(id)
);

CREATE INDEX idx_account_application ON application_account(application_id);
CREATE INDEX idx_account_status ON application_account(status);
CREATE UNIQUE INDEX idx_account_unique ON application_account(application_id, account_identifier);
```

### 2.4 标识绑定表 identifier_binding

```sql
CREATE TABLE identifier_binding (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,         -- 关联应用账户
    identifier_id INTEGER NOT NULL,      -- 关联身份标识
    purposes TEXT NOT NULL,              -- 用途列表 (JSON数组: ["LOGIN","VERIFICATION"])
    is_primary INTEGER NOT NULL DEFAULT 0, -- 是否主要标识
    bound_at TEXT NOT NULL,              -- 绑定时间
    verified_at TEXT,                    -- 验证时间
    notes TEXT,                          -- 备注
    
    FOREIGN KEY (account_id) REFERENCES application_account(id),
    FOREIGN KEY (identifier_id) REFERENCES identity_identifier(id),
    UNIQUE(account_id, identifier_id)
);

CREATE INDEX idx_binding_account ON identifier_binding(account_id);
CREATE INDEX idx_binding_identifier ON identifier_binding(identifier_id);
```

### 2.5 账户关联表 account_relation

```sql
CREATE TABLE account_relation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id_1 INTEGER NOT NULL,       -- 账户1
    account_id_2 INTEGER NOT NULL,       -- 账户2 (小号)
    relation_type TEXT NOT NULL DEFAULT 'SIBLING', -- SIBLING
    notes TEXT,                          -- 备注
    created_at TEXT NOT NULL,
    
    FOREIGN KEY (account_id_1) REFERENCES application_account(id),
    FOREIGN KEY (account_id_2) REFERENCES application_account(id),
    UNIQUE(account_id_1, account_id_2)
);
```

### 2.6 预警记录表 warning_record

```sql
CREATE TABLE warning_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    credential_id INTEGER,               -- 关联凭证（凭证预警）
    account_id INTEGER,                  -- 关联账户（账户预警）
    warning_type TEXT NOT NULL,          -- 预警类型
    warning_level TEXT NOT NULL,         -- HIGH / MEDIUM / LOW
    message TEXT NOT NULL,               -- 预警消息
    triggered_at TEXT NOT NULL,          -- 触发时间
    is_read INTEGER NOT NULL DEFAULT 0,  -- 是否已读
    is_handled INTEGER NOT NULL DEFAULT 0, -- 是否已处理
    handled_at TEXT,                     -- 处理时间
    
    FOREIGN KEY (credential_id) REFERENCES credential_identifier(id),
    FOREIGN KEY (account_id) REFERENCES application_account(id)
);

CREATE INDEX idx_warning_credential ON warning_record(credential_id);
CREATE INDEX idx_warning_account ON warning_record(account_id);
CREATE INDEX idx_warning_unread ON warning_record(is_read);
```

### 2.7 计划停用表 identifier_deactivation

```sql
CREATE TABLE identifier_deactivation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    identifier_id INTEGER NOT NULL,      -- 关联身份标识
    deact_type TEXT NOT NULL,            -- SCHEDULED / IMMEDIATE
    status TEXT NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED / EXECUTED / CANCELLED
    scheduled_time TEXT,                 -- 计划停用时间
    actual_time TEXT,                    -- 实际停用时间
    reason TEXT,                         -- 停用原因
    cancel_reason TEXT,                  -- 取消原因
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    
    FOREIGN KEY (identifier_id) REFERENCES identity_identifier(id)
);

CREATE INDEX idx_deact_identifier ON identifier_deactivation(identifier_id);
CREATE INDEX idx_deact_scheduled_time ON identifier_deactivation(scheduled_time);
```

### 2.8 账户扩展信息表 account_extension

```sql
CREATE TABLE account_extension (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,         -- 关联账户
    key TEXT NOT NULL,                   -- 字段标识
    value TEXT,                          -- 字段值
    label TEXT,                          -- 显示名称
    field_type TEXT NOT NULL,            -- STRING/DATE/BOOL/ENUM/INTEGER
    options TEXT,                        -- ENUM类型的可选值（JSON数组）
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    
    FOREIGN KEY (account_id) REFERENCES application_account(id),
    UNIQUE(account_id, key)
);

CREATE INDEX idx_extension_account ON account_extension(account_id);
```

### 2.9 扩展字段定义表 extension_field_definition

```sql
CREATE TABLE extension_field_definition (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    key TEXT NOT NULL UNIQUE,            -- 字段标识
    label TEXT NOT NULL,                 -- 显示名称
    field_type TEXT NOT NULL,            -- STRING/DATE/BOOL/ENUM/INTEGER
    options TEXT,                        -- ENUM可选值
    category TEXT,                       -- 字段分类
    is_default INTEGER DEFAULT 0,        -- 是否默认显示
    sort_order INTEGER DEFAULT 0         -- 排序顺序
);
```

### 2.10 绑定历史表 identifier_binding_history

```sql
CREATE TABLE identifier_binding_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    identifier_id INTEGER NOT NULL,
    action_type TEXT NOT NULL,           -- BIND/UNBIND/CHANGE_PURPOSE/REBIND/SWITCH_IDENTIFIER
    previous_purposes TEXT,              -- 变更前的用途
    new_purposes TEXT,                   -- 变更后的用途
    previous_identifier_id INTEGER,      -- 更换绑定时的原标识ID
    new_identifier_id INTEGER,           -- 更换绑定时的新标识ID
    action_at TEXT NOT NULL,             -- 操作时间
    action_by TEXT,                      -- 操作者标识
    notes TEXT,                          -- 操作备注
    
    FOREIGN KEY (account_id) REFERENCES application_account(id),
    FOREIGN KEY (identifier_id) REFERENCES identity_identifier(id)
);

CREATE INDEX idx_history_account ON identifier_binding_history(account_id);
CREATE INDEX idx_history_identifier ON identifier_binding_history(identifier_id);
```

### 2.11 密钥存储表 key_storage

```sql
CREATE TABLE key_storage (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    key_type TEXT NOT NULL UNIQUE,       -- DEK / KEK_WRAPPED
    key_value TEXT NOT NULL,             -- 加密后的密钥值
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);
```

### 2.12 数据导出记录表 export_record

```sql
CREATE TABLE export_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    file_path TEXT NOT NULL,             -- 导出文件路径
    file_format TEXT NOT NULL,           -- JSON / CSV
    export_type TEXT NOT NULL,           -- FULL / PARTIAL
    exported_at TEXT NOT NULL,           -- 导出时间
    file_size INTEGER,                   -- 文件大小(bytes)
    is_encrypted INTEGER NOT NULL DEFAULT 0, -- 是否加密
    record_count INTEGER DEFAULT 0,      -- 导出记录数
    checksum TEXT                        -- 文件校验和
);
```

### 2.13 数据导入记录表 import_record

```sql
CREATE TABLE import_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    file_path TEXT NOT NULL,             -- 导入文件路径
    file_format TEXT NOT NULL,           -- JSON / CSV
    import_type TEXT NOT NULL,           -- MERGE / OVERWRITE / SKIP
    imported_at TEXT NOT NULL,           -- 导入时间
    record_count INTEGER DEFAULT 0,      -- 导入记录数
    conflict_count INTEGER DEFAULT 0,    -- 冲突记录数
    notes TEXT                          -- 导入备注
);
```

---

## 三、实体关系图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          实体关系图                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  credential_identifier                                                │
│  ┌──────────────────────────────────────┐                            │
│  │  id, type, value(明文), status      │                            │
│  │  planned_deact_time, deact_reason    │                            │
│  └────────────────┬─────────────────────┘                            │
│                   │ 1:N                                              │
│                   ▼                                                  │
│  credential_deactivation                                              │
│  ┌──────────────────────────────────────┐                            │
│  │  credential_id, deact_type, status   │                            │
│  │  scheduled_time, actual_time         │                            │
│  └──────────────────────────────────────┘                            │
│                                                                       │
│  application                                                          │
│  ┌──────────────────────────────────────┐                            │
│  │  id, name, type, category, icon_url  │                            │
│  └────────────────┬─────────────────────┘                            │
│                   │ 1:N                                              │
│                   ▼                                                  │
│  application_account                                                  │
│  ┌──────────────────────────────────────┐                            │
│  │  application_id, account_name, status│                            │
│  │  keep_alive_enabled, tags            │                            │
│  └────────────────┬─────────────────────┘                            │
│                   │ 1:N                                              │
│         ┌─────────┼─────────┐                                         │
│         ▼         ▼         ▼                                         │
│  credential_binding   account_extension   account_relation            │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐         │
│  │ account_id      │ │ account_id      │ │ account_id_1    │         │
│  │ credential_id   │ │ key, value      │ │ account_id_2    │         │
│  │ purposes        │ │ label, type     │ │ relation_type   │         │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘         │
│         │                                                            │
│         │ N:1                                                        │
│         ▼                                                            │
│  credential_binding_history                                           │
│  ┌──────────────────────────────────────┐                            │
│  │  account_id, credential_id, action   │                            │
│  │  previous_purposes, new_purposes     │                            │
│  └──────────────────────────────────────┘                            │
│                                                                       │
│  warning_record                                                       │
│  ┌──────────────────────────────────────┐                            │
│  │  credential_id, account_id           │                            │
│  │  warning_type, warning_level, message│                            │
│  │  is_read, is_handled                 │                            │
│  └──────────────────────────────────────┘                            │
│                                                                       │
│  key_storage                                                         │
│  ┌──────────────────────────────────────┐                            │
│  │  key_type, key_value                 │                            │
│  └──────────────────────────────────────┘                            │
│                                                                       │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 四、数据导入导出格式

### 4.1 JSON导出格式

```json
{
  "version": "1.0",
  "exportType": "FULL",
  "exportedAt": "2026-05-08T15:30:00Z",
  "appVersion": "1.0.0",
  
  "credentials": [
    {
      "id": 1,
      "type": "PHONE",
      "value": "13812345678",
      "status": "ACTIVE",
      "plannedDeactTime": null,
      "createdAt": "2026-01-01T10:00:00Z"
    }
  ],
  
  "applications": [
    {
      "id": 1,
      "name": "微博",
      "type": "MOBILE_APP",
      "category": "社交"
    }
  ],
  
  "accounts": [
    {
      "id": 1,
      "applicationId": 1,
      "accountName": "张三",
      "status": "ACTIVE",
      "bindings": [
        {
          "credentialId": 1,
          "purposes": ["LOGIN", "VERIFICATION"],
          "isPrimary": true
        }
      ],
      "extensions": [
        {
          "key": "register_date",
          "value": "2020-01-15",
          "label": "注册日期",
          "fieldType": "DATE"
        }
      ]
    }
  ],
  
  "deactivations": [],
  "warnings": []
}
```

### 4.2 CSV导出格式

```csv
账户ID,应用名称,账户名,凭证类型,凭证值,绑定用途,账户状态
1,微博,张三,PHONE,13812345678,LOGIN;VERIFICATION,ACTIVE
2,淘宝,李四,EMAIL,test@qq.com,VERIFICATION;RECOVERY,ACTIVE
```

---

## 五、数据安全设计

### 5.1 DEK+KEK 信封加密方案

| 组件 | 说明 | 存储位置 |
|------|------|----------|
| **KEK** | 密钥加密密钥 (256位) | Android Keystore / iOS Keychain |
| **DEK** | 数据加密密钥 (256位) | 加密后存储在数据库 |
| **数据库** | 整个文件加密 | SQLCipher |

**密钥派生流程**：

```
用户设置密码
    → PBKDF2-HMAC-SHA256 (盐值: 随机16字节, 迭代: 100000次)
    → 生成KEK (256位)
    → 生成随机DEK (256位)
    → 用KEK加密DEK
    → 存储加密后的DEK到数据库
    → 用DEK初始化SQLCipher数据库
```

**解密流程**：

```
用户输入密码
    → PBKDF2-HMAC-SHA256 (使用存储的盐值)
    → 生成KEK
    → 从数据库读取加密的DEK
    → 用KEK解密DEK
    → 用DEK解密SQLCipher数据库
    → 访问明文数据
```

### 5.7 SQLCipher 集成

| 配置项 | 值 |
|--------|------|
| **加密算法** | AES-256-CBC |
| **密钥派生** | PBKDF2-HMAC-SHA1 (SQLCipher内置) |
| **迭代次数** | 64000次 |
| **页面大小** | 4096字节 |

### 5.3 重复校验实现（明文存储优势）

**身份标识重复检测**：
```sql
-- 直接使用明文进行重复检测
SELECT COUNT(*) FROM identity_identifier 
WHERE type = 'PHONE' AND value = '13812345678';
```

**账户重复检测**：
```sql
SELECT COUNT(*) FROM application_account
WHERE application_id = 1 AND account_identifier = 'zhangsan';
```

**绑定重复检测**：
```sql
-- 数据库唯一约束直接生效
INSERT INTO identifier_binding(account_id, identifier_id, purposes)
VALUES (1, 1, '["LOGIN"]'); -- 重复时自动失败
```

### 5.4 搜索实现（明文存储优势）

```sql
-- 模糊搜索身份标识
SELECT * FROM identity_identifier 
WHERE value LIKE '%138%' OR value LIKE '%qq.com%';

-- 精确搜索
SELECT * FROM identity_identifier 
WHERE value = '13812345678';

-- 组合搜索
SELECT * FROM identity_identifier
WHERE type = 'PHONE' AND status = 'ACTIVE';
```

### 5.5 导出安全

| 场景 | 处理方式 |
|------|----------|
| **明文导出** | 用户可选择是否加密导出文件 |
| **文件加密** | 使用独立密码加密导出文件 |
| **完整性校验** | SHA-256校验和 |

### 5.6 显示策略与访问控制

**显示策略**：
- 身份标识值（手机号/邮箱）默认全文显示，不进行脱敏处理
- 用户打开APP并通过密码/指纹验证后，即可查看所有身份标识的完整值
- 导出文件中的标识值同样为明文（用户可选择加密导出文件）

**访问控制**：

| 操作 | 验证要求 |
|------|----------|
| 打开APP | 密码/指纹验证（解锁数据库） |
| 查看身份标识值 | 直接显示（数据库已解锁） |
| 查看账户详情 | 直接显示（数据库已解锁） |
| 修改身份标识 | 无需额外验证（数据库已解锁） |
| 删除身份标识 | 确认弹窗 |
| 导出数据 | 可选择是否加密导出文件 |

---

## 六、方案优势总结

| 维度 | 原方案（字段加密） | 新方案（数据库加密） |
|------|-------------------|-------------------|
| **检索效率** | 需解密或依赖脱敏索引 | 直接SQL查询，支持索引 |
| **重复校验** | 需解密比对 | 直接明文比对，数据库约束生效 |
| **导入导出** | 需解密后导出 | 直接导出明文，格式清晰 |
| **安全级别** | 字段级加密 | 数据库级加密，更安全 |
| **密钥管理** | 复杂（每字段加密） | 统一管理（DEK+KEK） |
| **性能** | 每次访问需解密 | 数据库打开时解密一次 |

---

*文档版本：v3.0（NIST 标准术语，表名更新）*
*创建日期：2026-05-08*
*最后更新：2026-05-12*
