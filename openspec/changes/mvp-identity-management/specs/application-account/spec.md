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

The system SHALL allow users to update account status. Status transition SHALL follow defined state machine rules.

#### Scenario: Update status to FROZEN
- **WHEN** account status is ACTIVE and user updates status to FROZEN
- **THEN** system validates status transition (ACTIVE→FROZEN is valid)
- **AND** system updates account status to FROZEN

#### Scenario: Reject invalid status transition
- **WHEN** account status is FROZEN and user attempts to update status to LOST
- **THEN** system displays error "Invalid status transition"
- **AND** system does NOT update status

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