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

### Requirement: Data import and export functionality

The system SHALL provide data import and export functionality supporting JSON and CSV formats. Export file can be optionally encrypted.

#### Scenario: Import data from file
- **WHEN** user clicks "Import" and selects file (JSON/CSV)
- **THEN** system parses file and validates format
- **AND** system displays import mode options: Merge / Overwrite / Skip Duplicates
- **AND** user confirms import mode
- **AND** system imports data and displays result summary

#### Scenario: Export data to file
- **WHEN** user clicks "Export" and selects format (JSON/CSV)
- **THEN** system generates export file
- **AND** system displays encryption option toggle
- **AND** if encryption enabled, user sets export password
- **AND** system saves file to user-selected location

### Requirement: Clear all data with confirmation

The system SHALL provide data clear functionality with strong confirmation to prevent accidental deletion.

#### Scenario: Clear all data
- **WHEN** user clicks "Clear All Data" button
- **THEN** system displays confirmation dialog "确定清空所有数据？此操作不可撤销"
- **AND** user must type "CLEAR" to confirm
- **AND** after confirmation, system deletes all data and returns to welcome page