## ADDED Requirements

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