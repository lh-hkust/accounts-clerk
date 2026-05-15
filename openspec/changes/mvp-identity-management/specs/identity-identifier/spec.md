## ADDED Requirements

### Requirement: Identifier list displays gesture hint for first-time users

The system SHALL provide visual gesture hints for identifier cards when user first enters the list screen.

#### Scenario: First-time gesture hint display
- **WHEN** user first enters Identifier List screen
- **AND** no previous gesture interaction recorded (SharedPreferences flag)
- **THEN** first identifier card displays gesture hint dots (3 animated dots)
- **AND** hint indicates swipeable gestures available
- **AND** hint auto-dismisses after 3 seconds
- **AND** system records flag to prevent re-display on subsequent visits

### Requirement: User can add identity identifier

The system SHALL allow users to add identity identifiers (phone number or email address). The identifier value SHALL be stored in plaintext for searchability. Duplicate identifiers (same type and value) SHALL be rejected.

#### Scenario: Add phone number identifier
- **WHEN** user selects "Add Identifier" and chooses type "PHONE" and enters value "13812345678" and clicks "Save"
- **THEN** system creates IdentityIdentifier entity with type=PHONE, value="13812345678", status=ACTIVE
- **AND** system returns to identifier list page
- **AND** new identifier appears in "Active" group

#### Scenario: Add email identifier
- **WHEN** user selects "Add Identifier" and chooses type "EMAIL" and enters value "test@qq.com" and clicks "Save"
- **THEN** system creates IdentityIdentifier entity with type=EMAIL, value="test@qq.com", status=ACTIVE

#### Scenario: Reject duplicate identifier
- **WHEN** user attempts to add phone number "13812345678" and identifier already exists
- **THEN** system displays error "Identifier already exists"
- **AND** system does NOT create new entity

### Requirement: User can view identifier list

The system SHALL display all identity identifiers grouped by status (ACTIVE, PENDING_DEACTIVATION, DEACTIVATED). Each identifier SHALL display type icon, full value, and bound account count.

#### Scenario: View identifier list with grouping
- **WHEN** user enters identifier management page
- **THEN** system displays identifier list grouped by status
- **AND** each identifier shows type icon, full value, bound account count
- **AND** clicking identifier opens detail page

### Requirement: User can view identifier details

The system SHALL display identifier details including bound account list with application icon, account name, and binding purposes. The identifier value SHALL be displayed in full text without masking.

#### Scenario: View identifier with bound accounts
- **WHEN** user enters identifier detail page for identifier "13812345678" with 3 bound accounts
- **THEN** system displays identifier value in full text
- **AND** system displays bound account list with application icon, account name, binding purposes
- **AND** clicking account opens account detail page

### Requirement: User can delete identifier

The system SHALL allow deletion of identifiers that are not bound to any account. Deletion SHALL require confirmation dialog.

#### Scenario: Delete unbound identifier
- **WHEN** identifier has no bound accounts and user clicks "Delete" and confirms in dialog
- **THEN** system deletes IdentityIdentifier entity
- **AND** system removes identifier from list

#### Scenario: Prevent deletion of bound identifier
- **WHEN** identifier has 2 bound accounts and user clicks "Delete"
- **THEN** system displays message "Identifier has bound accounts, please unbind first"
- **AND** system does NOT execute deletion

### Requirement: System prevents duplicate identifiers

The system SHALL enforce uniqueness constraint on identifier type and value combination. The uniqueness check SHALL use plaintext comparison.

#### Scenario: Duplicate check on add
- **WHEN** user adds identifier with type="PHONE" and value="13812345678"
- **THEN** system checks existence using SQL query on plaintext value
- **AND** system rejects if match found

### Requirement: Identifier list displays empty state with guidance

The system SHALL display helpful empty state UI when identifier list has no items.

#### Scenario: Empty identifier list
- **WHEN** identifier list has zero items
- **THEN** system displays empty state card
- **AND** card shows: "暂无验证渠道"
- **AND** card shows: "添加手机号或邮箱，开始追踪账号绑定关系"
- **AND** provides "添加验证渠道" button

### Requirement: Identifier search empty result displays friendly feedback

The system SHALL display helpful message when search yields no results.

#### Scenario: No search results
- **WHEN** user searches in Identifier list
- **AND** search query matches zero items
- **THEN** system displays: "未找到匹配结果"
- **AND** displays suggestion: "请尝试其他关键词"
- **AND** clears search query after 2 seconds automatically