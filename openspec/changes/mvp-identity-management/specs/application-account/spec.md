## ADDED Requirements

### Requirement: User can add application account

The system SHALL allow users to add application accounts. Account SHALL be associated with an application platform. Account identifier (username) SHALL be unique per application. Account nickname is optional and displayed in list when set.

#### Scenario: Add application account
- **WHEN** user selects "Add Account" and selects application "Weibo"
- **AND** user enters accountName "张三" and accountIdentifier "zhangsan123"
- **AND** user clicks "Save"
- **THEN** system creates ApplicationAccount entity
- **AND** applicationId is Weibo's application ID
- **AND** accountName is "张三"
- **AND** accountIdentifier is "zhangsan123"
- **AND** status is ACTIVE

#### Scenario: Add account with optional nickname
- **WHEN** user adds account and enters optional nickname "我的微博主号"
- **AND** user clicks "Save"
- **THEN** system creates ApplicationAccount entity with nickname
- **AND** account list displays nickname "我的微博主号" instead of accountIdentifier
- **AND** account detail page displays both nickname and accountIdentifier

#### Scenario: Reject duplicate account identifier per application
- **WHEN** user adds account with applicationId=1 and accountIdentifier="zhangsan123"
- **AND** account already exists with same application and identifier
- **THEN** system displays error "Account identifier already exists for this application"
- **AND** system does NOT create new entity

### Requirement: User can bind identifier when adding account

The system SHALL allow users to bind identity identifiers when adding a new account. User SHALL select identifier from available list. User SHALL specify binding purposes (at least one required).

#### Scenario: Bind identifier when adding account
- **WHEN** user adds account and selects identifier "13812345678"
- **AND** user selects purposes ["VERIFICATION", "RECOVERY"]
- **AND** user clicks "Save"
- **THEN** system creates ApplicationAccount entity
- **AND** system creates IdentifierBinding entity with selected purposes
- **AND** account detail page displays bound identifier with purposes

#### Scenario: Default purpose selection
- **WHEN** user selects identifier for binding
- **THEN** system default selects "VERIFICATION" purpose
- **AND** user can modify purpose selection

#### Scenario: Identifier list display and sorting
- **WHEN** user views available identifiers for binding
- **THEN** system displays identifiers grouped by status
- **AND** status groups are separated by short horizontal line (no text)
- **AND** sorting order: ACTIVE > PENDING_DEACTIVATION > DEACTIVATED (within group, newest first)
- **AND** DEACTIVATED group is collapsed by default with "View All" button

#### Scenario: Purpose selection dialog
- **WHEN** user clicks selected identifier again
- **THEN** system displays purpose selection dialog
- **AND** purposes are displayed as colored bubble chips
- **AND** selected purposes have border highlight
- **AND** user can multi-select purposes
- **AND** at least one purpose must be selected to confirm

### Requirement: User can edit account information

The system SHALL allow users to edit account information including accountName, accountIdentifier, nickname, and status. ApplicationId SHALL NOT be editable after creation.

#### Scenario: Edit account name and nickname
- **WHEN** user clicks "Edit Account" in account detail page
- **AND** user changes accountName to "李四" and nickname to "工作小号"
- **AND** user clicks "Save"
- **THEN** system updates accountName and nickname
- **AND** account list displays new nickname

#### Scenario: Edit account identifier with uniqueness check
- **WHEN** user edits accountIdentifier to "lisi456"
- **AND** accountIdentifier "lisi456" does not exist for same application
- **THEN** system updates accountIdentifier
- **AND** system creates BindingHistoryRecord

#### Scenario: Reject duplicate identifier on edit
- **WHEN** user edits accountIdentifier to "zhangsan123"
- **AND** another account with same application and identifier exists
- **THEN** system displays error "Account identifier already exists"
- **AND** system does NOT update

### Requirement: User can delete account with confirmation

The system SHALL allow users to delete accounts. Deletion SHALL require confirmation with anti-mistake input. Account bindings SHALL be automatically deleted when account is deleted.

#### Scenario: Delete account with confirmation
- **WHEN** user triggers delete action (long-press menu or left-swipe)
- **THEN** system displays confirmation dialog
- **AND** dialog shows account name "微博 - 工作小号01"
- **AND** user must input account nickname "工作小号01" to confirm
- **AND** after correct input, delete button becomes enabled

#### Scenario: Delete account with bindings
- **WHEN** user confirms deletion of account with 3 bindings
- **THEN** system deletes ApplicationAccount entity
- **AND** system deletes all related IdentifierBinding entities
- **AND** system creates BindingHistoryRecord for each unbind action

### Requirement: User can view account list

The system SHALL display account list grouped by application. Each account SHALL display application icon, account name (nickname if set), and status.

#### Scenario: View account list grouped by application
- **WHEN** user enters account management page
- **THEN** system displays account list grouped by application
- **AND** each account displays application icon, account name, status
- **AND** if nickname is set, displays nickname instead of accountIdentifier
- **AND** clicking account opens account detail page

### Requirement: User can view account details

The system SHALL display account details including bound identifiers list with identifier type, value, and binding purposes.

#### Scenario: View account with bound identifiers
- **WHEN** account has 2 bound identifiers and user enters account detail page
- **THEN** system displays account info (nickname if set, accountIdentifier, status, last login date)
- **AND** system displays bound identifier list with type, value, purposes
- **AND** clicking identifier opens identifier detail page

### Requirement: User can update account status

The system SHALL allow users to update account status to any valid status. No state machine restriction applies.

#### Scenario: Update status to FROZEN
- **WHEN** account status is ACTIVE and user updates status to FROZEN
- **THEN** system updates account status to FROZEN

#### Scenario: Update status from LOST to ACTIVE
- **WHEN** account status is LOST and user updates status to ACTIVE
- **THEN** system updates account status to ACTIVE
- **AND** no state machine restriction applies

### Requirement: User can change binding to different identifier

The system SHALL allow users to switch account's binding to a different identifier while keeping purposes. Default behavior preserves original purposes.

#### Scenario: Switch binding identifier with preserved purposes
- **WHEN** account has binding to identifier "13812345678" with purposes ["VERIFICATION", "RECOVERY"]
- **AND** user selects "Change Binding"
- **AND** user selects new identifier "test@qq.com"
- **AND** user clicks "Confirm" (no purpose modification)
- **THEN** system creates new binding to "test@qq.com"
- **AND** new binding has same purposes ["VERIFICATION", "RECOVERY"]
- **AND** system deletes old binding
- **AND** system creates BindingHistoryRecord with actionType=SWITCH_IDENTIFIER

#### Scenario: Switch binding with modified purposes
- **WHEN** user switches binding and clicks "Modify Purposes"
- **AND** user changes purposes to ["LOGIN", "VERIFICATION"]
- **THEN** system creates new binding with modified purposes
- **AND** system creates BindingHistoryRecord

### Requirement: User can add account extension fields

The system SHALL allow users to add custom extension fields to accounts. Field key SHALL be unique per account.

#### Scenario: Add extension field
- **WHEN** user adds extension with key="register_date" and value="2020-01-15" and fieldType=DATE
- **THEN** system creates AccountExtension entity
- **AND** accountId is the account ID
- **AND** key is "register_date"
- **AND** value is "2020-01-15"

#### Scenario: Reject duplicate extension key
- **WHEN** user adds extension with key="register_date" and extension with same key already exists for account
- **THEN** system displays error "Extension key already exists"
- **AND** system does NOT create new extension