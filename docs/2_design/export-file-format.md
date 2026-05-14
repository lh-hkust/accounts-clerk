# Hermes - 导出文件格式规范

> 定义加密导出文件和明文导出文件的数据格式

---

## 一、加密文件格式（.hexport）

### 1.1 文件头结构

| 偏移 | 字段 | 大小 | 说明 |
|------|------|------|------|
| 0 | 魔数 | 4字节 | `0xAAFF0101` |
| 4 | 版本号 | 1字节 | `0x01` |
| 5 | 加密算法ID | 1字节 | `0x01` = AES-256-GCM |
| 6 | KDF算法ID | 1字节 | `0x01` = PBKDF2(密码模式), `0x02` = HKDF(无密码) |
| 7 | 标志位 | 1字节 | bit0: 压缩启用(zlib) |
| 8 | 盐长度 | 1字节 | KDF=0x01时为16，KDF=0x02时为0 |
| 9 | 盐 | 变长 | 仅KDF=0x01时存在 |
| 9+盐长 | IV长度 | 1字节 | 固定12 |
| 9+盐长+1 | IV | 12字节 | 随机生成 |
| 后 | 认证标签 | 16字节 | GCM Tag |
| 后 | 密文长度 | 4字节 | 大端序 |
| 后 | 密文 | 变长 | 加密后的压缩数据 |

### 1.2 文件头字段说明

| 字段 | 必要性 | 说明 |
|------|--------|------|
| 魔数 | 必要 | 用于识别文件类型 |
| 版本号 | 必要 | 支持未来格式升级 |
| 加密算法ID | 必要 | 标识使用的加密算法 |
| KDF算法ID | 必要 | 区分密码模式和无密码模式 |
| 盐 | 密码模式必要 | PBKDF2所需的随机盐值 |
| IV | 必要 | AES-GCM所需的初始化向量 |
| 认证标签 | 必要 | GCM模式完整性校验 |

---

## 二、明文JSON文件格式

### 2.1 JSON结构

```json
{
  "version": "1.0",
  "exportType": "FULL",
  "exportedAt": "2026-05-14T15:30:00Z",
  "appVersion": "1.0.0",
  
  "identifiers": [
    {
      "id": 1,
      "type": "PHONE",
      "value": "13812345678",
      "status": "ACTIVE",
      "plannedDeactTime": null,
      "createdAt": "2026-01-01T10:00:00Z"
    }
  ],
  
  "accounts": [
    {
      "id": 1,
      "applicationId": 1,
      "accountName": "张三",
      "accountIdentifier": "zhangsan123",
      "status": "ACTIVE",
      "createdAt": "2026-01-01T10:00:00Z"
    }
  ],
  
  "bindings": [
    {
      "id": 1,
      "accountId": 1,
      "identifierId": 1,
      "purposes": ["LOGIN", "VERIFICATION"],
      "isPrimary": true,
      "boundAt": "2026-01-01T10:00:00Z"
    }
  ],
  
  "applications": [
    {
      "id": 1,
      "name": "微博",
      "category": "社交"
    }
  ]
}
```

### 2.2 字段说明

| 根字段 | 类型 | 说明 |
|--------|------|------|
| version | String | 导出格式版本 |
| exportType | String | 导出类型（FULL） |
| exportedAt | String | 导出时间（ISO 8601） |
| appVersion | String | 应用版本号 |
| identifiers | Array | 身份标识列表 |
| accounts | Array | 应用账户列表 |
| bindings | Array | 标识绑定列表 |
| applications | Array | 应用平台列表 |

---

## 三、数据序列化规则

### 3.1 加密文件序列化流程

```
原始数据 → JSON序列化 → zlib压缩 → AES-256-GCM加密 → 写入文件头+密文
```

### 3.2 明文文件序列化流程

```
原始数据 → JSON序列化 → 写入文件
```

### 3.3 时间格式

所有时间字段使用 ISO 8601 格式：`YYYY-MM-DDTHH:mm:ssZ`

---

## 四、导入模式定义

| 模式 | 英文标识 | 说明 |
|------|----------|------|
| **合并** | MERGE | 新增不存在数据，保留已有数据 |
| **覆盖** | OVERWRITE | 新增不存在数据，替换已有数据 |
| **跳过重复** | SKIP_DUPLICATE | 仅导入不存在数据，跳过所有重复 |

---

## 相关文档

- [数据安全技术方案](data-security.md) - 加密密钥派生方案

---

*文档版本: v1.0*
*创建日期: 2026-05-14*