## ADDED Requirements

### Requirement: Settings page indicates single-user local mode

The system SHALL indicate current version is single-user local mode. Cloud sync feature SHALL be indicated as planned for future release.

#### Scenario: Click avatar or logout
- **WHEN** user clicks avatar or profile section
- **THEN** system displays dialog "当前为单机版本，云账号同步功能尽请期待"
- **AND** dialog has "确定" button to close

#### Scenario: Click logout button
- **WHEN** user clicks "Logout" button
- **THEN** system displays dialog "当前为单机版本，退出功能将在云版本中提供"
- **AND** dialog has "确定" button to close

### Requirement: About dialog displays application info

The system SHALL display application name, semantic version number (no 'v' prefix), update check button, and project homepage URL in About dialog.

#### Scenario: View about info
- **WHEN** user clicks "About" in settings
- **THEN** system displays dialog with:
  - Application name: Hermes
  - Version: 1.0.0 (semantic version format, no 'v' prefix)
  - "Check Update" button
  - Project homepage URL (link to GitHub or official site)
- **AND** user can click version to copy version info

#### Scenario: Check update
- **WHEN** user clicks "Check Update" button in About dialog
- **THEN** system displays "当前已是最新版本" or update info if available

### Requirement: Privacy security settings page

The system SHALL provide privacy security settings page including application access password setting and system permissions information.

#### Scenario: Enter privacy security page
- **WHEN** user clicks "Privacy Security" in settings
- **THEN** system enters privacy security page
- **AND** page displays "Access Password" section
- **AND** page displays "System Permissions" section

#### Scenario: Set access password
- **WHEN** user clicks "Set Access Password"
- **THEN** system displays password input dialog
- **AND** user can set or change password for app unlock
- **AND** password SHALL meet minimum length requirement (6 characters)

#### Scenario: View system permissions
- **WHEN** user views "System Permissions" section
- **THEN** system displays list of required permissions:
  - Storage: Required for data import/export
  - Fingerprint/Biometric: Optional for quick unlock
- **AND** each permission shows necessity and supported features

### Requirement: Data management displays encrypted status

The system SHALL display encryption status with shield icon, NOT display platform-specific encryption implementation details.

#### Scenario: View data management page
- **WHEN** user enters data management page
- **THEN** system displays data storage section
- **AND** system displays "已加密" with shield icon
- **AND** system does NOT display "SQLCipher" or other platform-specific terms
- **AND** system displays identifier count and account count
- **AND** system displays database size

### Requirement: Export modes selection

The system SHALL provide two export modes: plain JSON (non-secure) and encrypted Hermes format (secure). System SHALL guide users to prefer secure export.

#### Scenario: Export plain JSON with security warning
- **WHEN** user clicks "Export JSON"
- **THEN** system displays security warning dialog
- **AND** dialog content: "导出为明文JSON文件，数据无加密保护。任何人均可查看文件内容，存在隐私泄露风险。建议使用「安全导出」保护您的数据。"
- **AND** buttons: "使用安全导出" (primary), "继续导出JSON" (secondary)

#### Scenario: User proceeds with plain JSON export
- **WHEN** user clicks "继续导出JSON"
- **THEN** system requires user to check "我已了解风险" checkbox
- **AND** system calls SAF to select save location
- **AND** default filename: hermes_export_YYYYMMDD.json
- **AND** system displays progress dialog
- **AND** after completion, displays success message with warning reminder

#### Scenario: Secure export with password
- **WHEN** user clicks "安全导出"
- **THEN** system displays password setting dialog
- **AND** dialog shows optional password input fields
- **AND** if user enters password, system validates password length >= 6
- **AND** system requires password confirmation input
- **AND** if user skips password, requires checking "我已了解风险" checkbox
- **AND** dialog displays warning: "⚠️ 未设置密码时，文件可被任何拥有本应用的人打开。请勿分享或上传到云端。"

#### Scenario: Secure export file creation
- **WHEN** user confirms export settings
- **THEN** system calls SAF to select save location
- **AND** default filename: hermes_export_YYYYMMDD.hexport
- **AND** system encrypts data using AES-256-GCM
- **AND** if password set, uses PBKDF2(password + appSignature) key derivation
- **AND** if no password, uses HKDF(appSignature) fixed key derivation
- **AND** system displays progress dialog with percentage
- **AND** after completion, displays success message with file path

### Requirement: Import encrypted and plain files

The system SHALL support importing both encrypted .hexport files and plain .json files. System SHALL detect file type automatically.

#### Scenario: Import encrypted file with password
- **WHEN** user clicks "Import Data" and selects .hexport file
- **AND** file header indicates KDF=0x01 (password mode)
- **THEN** system displays password input dialog
- **AND** user enters password
- **AND** system attempts decryption
- **AND** if decryption fails, displays "密码错误或文件损坏"

#### Scenario: Import encrypted file without password
- **WHEN** user selects .hexport file
- **AND** file header indicates KDF=0x02 (no password mode)
- **THEN** system automatically attempts decryption using fixed key
- **AND** if decryption fails, displays "文件损坏或非本应用导出"

#### Scenario: Import plain JSON file
- **WHEN** user selects .json file
- **THEN** system directly parses JSON without decryption

#### Scenario: Import preview and mode selection
- **WHEN** file successfully parsed
- **THEN** system displays import preview dialog
- **AND** preview shows data summary: identifier count, account count, binding count
- **AND** preview shows conflict detection results
- **AND** user selects import mode: "合并" (default), "覆盖", "跳过重复"

#### Scenario: Import mode definitions
- **WHEN** user selects import mode
- **THEN** "合并" mode: add non-existing data, keep existing data
- **AND** "覆盖" mode: add non-existing data, replace existing data
- **AND** "跳过重复" mode: only import non-existing data, skip all duplicates

#### Scenario: Import completion
- **WHEN** user clicks "开始导入"
- **THEN** system displays progress dialog
- **AND** after completion, displays result: "导入完成！新增 X条，更新 Y条，跳过 Z条"

### Requirement: Clear all data with confirmation

The system SHALL provide data clear functionality with strong confirmation to prevent accidental deletion.

#### Scenario: Clear all data
- **WHEN** user clicks "Clear All Data" button
- **THEN** system displays confirmation dialog "确定清空所有数据？此操作不可撤销"
- **AND** user must type "CLEAR" to confirm
- **AND** after confirmation, system deletes all data and returns to welcome page