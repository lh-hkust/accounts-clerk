## ADDED Requirements

### Requirement: User can bind identifier to account

The system SHALL allow users to bind identity identifiers to application accounts. Binding SHALL specify purposes (LOGIN, VERIFICATION, RECOVERY, NOTIFICATION, SECONDARY_AUTH). Duplicate binding (same account and identifier) SHALL be rejected.

#### Scenario: Bind identifier with purposes
- **WHEN** account exists and identifier exists
- **AND** user clicks "Bind Identifier"
- **AND** user selects identifier "13812345678"
- **AND** user selects purposes ["LOGIN", "VERIFICATION"]
- **AND** user sets isPrimary=true
- **AND** user clicks "Save"
- **THEN** system creates IdentifierBinding entity
- **AND** accountId is the account ID
- **AND** identifierId is the identifier ID
- **AND** purposes is ["LOGIN", "VERIFICATION"]
- **AND** isPrimary is true
- **AND** boundAt is current timestamp

#### Scenario: Reject duplicate binding
- **WHEN** user binds identifier to account and binding already exists
- **THEN** system displays error "Identifier already bound to this account"
- **AND** system does NOT create new binding

### Requirement: User can unbind identifier from account

The system SHALL allow users to unbind identity identifiers from accounts. Unbinding SHALL be recorded in binding history.

#### Scenario: Unbind identifier
- **WHEN** binding exists and user clicks "Unbind"
- **AND** user confirms unbind
- **THEN** system deletes IdentifierBinding entity
- **AND** system creates BindingHistoryRecord with actionType=UNBIND
- **AND** history records previous purposes, action timestamp

### Requirement: User can change binding purposes

The system SHALL allow users to modify binding purposes. Purposes list SHALL not be empty after modification. Change SHALL be recorded in binding history.

#### Scenario: Change binding purposes
- **WHEN** binding exists with purposes=["LOGIN", "VERIFICATION"]
- **AND** user clicks "Edit Purpose"
- **AND** user changes purposes to ["LOGIN", "RECOVERY"]
- **AND** user clicks "Save"
- **THEN** system updates IdentifierBinding.purposes to ["LOGIN", "RECOVERY"]
- **AND** system creates BindingHistoryRecord with actionType=CHANGE_PURPOSE
- **AND** history records previousPurposes=["LOGIN", "VERIFICATION"] and newPurposes=["LOGIN", "RECOVERY"]

### Requirement: User can switch binding to different identifier

The system SHALL allow users to switch binding to a different identifier while keeping purposes. Switch SHALL be recorded in binding history.

#### Scenario: Switch binding identifier
- **WHEN** account has binding to identifier "13812345678"
- **AND** user clicks "Switch Identifier"
- **AND** user selects new identifier "test@qq.com"
- **AND** user clicks "Confirm"
- **THEN** system updates existing binding's identifierId to new identifier ID
- **OR** system creates new binding with same purposes and deletes old binding
- **AND** system creates BindingHistoryRecord with actionType=SWITCH_IDENTIFIER
- **AND** history records previousIdentifierId and newIdentifierId

### Requirement: System records binding history

The system SHALL create history record for every binding action (BIND, UNBIND, CHANGE_PURPOSE, SWITCH_IDENTIFIER). History SHALL include previous and new values, action timestamp.

#### Scenario: History record on bind
- **WHEN** user binds identifier to account
- **THEN** system creates BindingHistoryRecord with actionType=BIND
- **AND** history includes accountId, identifierId, newPurposes, actionAt, actionBy

#### Scenario: History record on unbind
- **WHEN** user unbinds identifier from account
- **THEN** system creates BindingHistoryRecord with actionType=UNBIND
- **AND** history includes accountId, identifierId, previousPurposes, actionAt