## ADDED Requirements

> 本规格定义数据导入导出的用户交互流程和界面设计。技术方案详见 [导出文件格式规范](../../../docs/2_design/export-file-format.md) 和 [数据安全技术方案](../../../docs/2_design/data-security.md)。

### Requirement: User can choose export mode

The system SHALL provide two export modes with clear security guidance.

#### Scenario: Export plain JSON with security warning
- **WHEN** user clicks "Export JSON"
- **THEN** system displays security warning dialog
- **AND** dialog title: "安全警告"
- **AND** dialog content: "导出为明文JSON文件，数据无加密保护。任何人均可查看文件内容，存在隐私泄露风险。建议使用「安全导出」保护您的数据。"
- **AND** primary button: "使用安全导出"
- **AND** secondary button: "继续导出JSON"

#### Scenario: User switches to secure export
- **WHEN** user clicks "使用安全导出" in warning dialog
- **THEN** system closes warning dialog
- **AND** system opens secure export password dialog

#### Scenario: User proceeds with plain JSON export
- **WHEN** user clicks "继续导出JSON"
- **THEN** system requires user to check "我已了解风险" checkbox
- **AND** system calls SAF to select save location
- **AND** default filename: hermes_export_YYYYMMDD.json
- **AND** system displays progress dialog
- **AND** after completion displays success message with warning reminder

### Requirement: User can set password for secure export

The system SHALL allow user to optionally set password for encrypted export. Default behavior preserves data security even without user password.

#### Scenario: Secure export password dialog
- **WHEN** user clicks "安全导出" or switches from JSON warning
- **THEN** system displays password setting dialog
- **AND** dialog title: "安全导出"
- **AND** dialog shows: "为导出文件设置密码可保护数据安全。若不设置密码，文件仍加密但任何拥有本应用的人都能打开。"
- **AND** optional password input field
- **AND** confirm password field (if password entered)
- **AND** password strength hint: "至少6个字符"

#### Scenario: User sets password for export
- **WHEN** user enters password "mypassword123"
- **AND** user enters confirm password "mypassword123"
- **AND** user clicks "开始导出"
- **THEN** system validates password length >= 6
- **AND** system validates passwords match
- **AND** system encrypts using PBKDF2 key derivation
- **AND** file saved with KDF=0x01 marker

#### Scenario: User skips password with risk confirmation
- **WHEN** user does not enter password
- **AND** user checks "我已了解风险，不设置密码" checkbox
- **AND** user clicks "开始导出"
- **THEN** system displays warning: "⚠️ 未设置密码时，文件可被任何拥有本应用的人都能打开。请勿分享或上传到云端。"
- **AND** system encrypts using HKDF fixed key derivation
- **AND** file saved with KDF=0x02 marker

### Requirement: User can select export location

The system SHALL use Android SAF (Storage Access Framework) for file location selection.

#### Scenario: SAF file creation
- **WHEN** user confirms export settings
- **THEN** system calls SAF create file intent
- **AND** default filename: hermes_export_YYYYMMDD.hexport (secure) or .json (plain)
- **AND** user selects storage location
- **AND** system writes file to selected location

### Requirement: User can view export progress

The system SHALL display progress dialog during export operation.

#### Scenario: Export progress dialog
- **WHEN** export operation starts
- **THEN** system displays progress dialog
- **AND** dialog shows LinearProgressIndicator
- **AND** dialog shows percentage (0-100%)
- **AND** dialog shows current processing stage: "正在处理：验证渠道数据"
- **AND** dialog is cancellable (optional)

#### Scenario: Export completion
- **WHEN** export completes successfully
- **THEN** system displays success dialog
- **AND** dialog shows: "✓ 文件已加密保存" (secure) or "导出完成" (plain)
- **AND** dialog shows file path
- **AND** dialog provides "复制路径" button
- **AND** dialog provides "打开文件" button
- **AND** dialog provides "关闭" button
- **AND** plain JSON shows warning reminder: "请妥善保管此文件，避免分享或上传云端"

### Requirement: User can import encrypted and plain files

The system SHALL support importing .hexport encrypted files and .json plain files.

#### Scenario: Import file selection
- **WHEN** user clicks "导入数据"
- **THEN** system calls SAF open file intent
- **AND** file filter: .hexport, .json
- **AND** user selects file

#### Scenario: Import encrypted file with password
- **WHEN** user selects .hexport file
- **AND** file header indicates KDF=0x01 (password mode)
- **THEN** system displays password input dialog
- **AND** user enters password
- **AND** system attempts decryption using PBKDF2 derived key
- **AND** if decryption fails, displays: "密码错误或文件损坏"

#### Scenario: Import encrypted file without password
- **WHEN** user selects .hexport file
- **AND** file header indicates KDF=0x02 (no password mode)
- **THEN** system automatically attempts decryption using HKDF derived key
- **AND** if decryption fails, displays: "文件损坏或非本应用导出"

#### Scenario: Import plain JSON file
- **WHEN** user selects .json file
- **THEN** system directly parses JSON without decryption

### Requirement: User can preview import data

The system SHALL display import preview before final import.

#### Scenario: Import preview dialog
- **WHEN** file successfully parsed
- **THEN** system displays import preview dialog
- **AND** dialog shows data summary:
  - "验证渠道：X 个"
  - "账号：Y 个"
  - "绑定关系：Z 个"
- **AND** dialog shows conflict detection result
- **AND** dialog shows import mode options

#### Scenario: Import mode selection
- **WHEN** user views import mode options
- **THEN** system displays three mode cards:
  - "合并" (default, selected)
  - "覆盖"
  - "跳过重复"
- **AND** each mode shows description:
  - 合并: "新增不存在数据，保留已有数据"
  - 覆盖: "新增不存在数据，替换已有数据"
  - 跳过重复: "仅导入不存在数据，跳过所有重复"

### Requirement: User can execute import

The system SHALL process import based on selected mode.

#### Scenario: Import execution
- **WHEN** user clicks "开始导入"
- **THEN** system displays progress dialog
- **AND** system processes data according to selected mode
- **AND** after completion displays result: "导入完成！新增 X条，更新 Y条，跳过 Z条"