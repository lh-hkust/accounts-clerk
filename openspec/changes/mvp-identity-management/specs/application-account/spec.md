### Requirement: User can add account without binding verification channel

The system SHALL allow account creation without mandatory binding to verification channels. Binding is optional, not required.

#### Scenario: Add account without binding
- **WHEN** user adds account with accountName and accountIdentifier
- **AND** user does not select any verification channel
- **AND** user clicks "Save"
- **THEN** system creates ApplicationAccount entity
- **AND** account has empty binding list
- **AND** account list displays "未绑定渠道" status indicator

#### Scenario: Add account with binding (normal flow)
- **WHEN** user selects a verification channel
- **AND** user selects binding purposes
- **THEN** system creates account with binding relationship
- **AND** binding status displays in account detail

### Requirement: Add Account screen provides shortcut to add verification channel

The system SHALL provide quick navigation to Add Identifier screen when user needs a new verification channel.

#### Scenario: No available channels prompt
- **WHEN** user is on Add Account screen
- **AND** available identifier list is empty
- **THEN** system displays prompt: "暂无可用验证渠道"
- **AND** displays action: "点击添加验证渠道"
- **AND** clicking prompt navigates to Add Identifier screen

#### Scenario: Add channel button in channel section
- **WHEN** available identifier list has items
- **THEN** system displays "+ 添加渠道" button at end of channel list
- **AND** clicking button navigates to Add Identifier screen
- **AND** after adding, user returns to Add Account screen with new channel available

---

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

#### Scenario: Bind multiple identifiers when adding account (新增 2026-05-15)
- **WHEN** user adds account and selects multiple identifiers ["13812345678", "test@qq.com"]
- **AND** user selects purposes for each identifier
- **AND** user clicks "Save"
- **THEN** system creates ApplicationAccount entity
- **AND** system creates multiple IdentifierBinding entities
- **AND** each binding has its own purposes
- **AND** account detail page displays all bound identifiers with their purposes

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

### Requirement: User can delete account with simple confirmation

The system SHALL allow users to delete accounts. Deletion SHALL require simple confirmation dialog showing account info and impact. Account bindings SHALL be automatically deleted when account is deleted.

#### Scenario: Delete account confirmation dialog
- **WHEN** user triggers delete action (long-press menu or left-swipe)
- **THEN** system displays confirmation dialog
- **AND** dialog title: "确认删除账号？"
- **AND** dialog shows account name "微博 - 工作小号01"
- **AND** dialog shows impact message: "将解绑 2 个验证渠道"
- **AND** dialog shows warning: "此操作不可撤销"
- **AND** primary button: "确认删除" (danger color)
- **AND** secondary button: "取消"

#### Scenario: Delete account execution
- **WHEN** user clicks "确认删除" in confirmation dialog
- **THEN** system deletes ApplicationAccount entity
- **AND** system deletes all related IdentifierBinding entities
- **AND** system creates BindingHistoryRecord for each unbind action
- **AND** system returns to account list page

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

### Requirement: Account list displays empty state with guidance

The system SHALL display helpful empty state UI when account list has no items.

#### Scenario: Empty account list
- **WHEN** account list has zero items
- **THEN** system displays empty state card
- **AND** card shows: "暂无账号记录"
- **AND** card shows: "添加常用应用账号，建立与验证渠道的绑定关系"
- **AND** provides "添加账号" button

### Requirement: Account search empty result displays friendly feedback

The system SHALL display helpful message when search yields no results.

#### Scenario: No search results
- **WHEN** user searches in Account list
- **AND** search query matches zero items
- **THEN** system displays: "未找到匹配结果"
- **AND** displays suggestion: "请尝试其他关键词"
- **AND** clears search query after 2 seconds automatically

### Requirement: Account detail page displays only domain model fields

The system SHALL only display fields that exist in domain model or can be derived.

#### Scenario: Display account info
- **WHEN** user views Account Detail screen
- **THEN** system displays: accountName, accountIdentifier, nickname, status
- **AND** system displays derived info: binding count, createdAt, updatedAt
- **AND** system does NOT display non-existent fields (e.g., "最后登录", "长期在线")

#### Scenario: Related account navigation
- **WHEN** user clicks related account item in Account Detail screen
- **THEN** system navigates to clicked account's detail screen
- **AND** back button returns to previous account detail (not account list)
- **AND** navigation stack maintains proper order