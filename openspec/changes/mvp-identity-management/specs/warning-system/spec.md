## ADDED Requirements

### Requirement: System automatically triggers warning on deactivation plan

The system SHALL automatically create warning records when deactivation plan is set. Warning level SHALL be calculated based on affected account count and account category.

#### Scenario: Trigger warning on plan creation
- **WHEN** identifier status is PENDING_DEACTIVATION
- **AND** identifier has 5 bound accounts including 2 financial applications
- **AND** system detects deactivation plan creation
- **THEN** system creates WarningRecord entity
- **AND** warningLevel is HIGH
- **AND** message contains "affects 5 accounts"
- **AND** identifierId is the identity identifier ID
- **AND** isRead is false
- **AND** isHandled is false

#### Scenario: Calculate warning level HIGH for financial accounts
- **WHEN** identifier has bound financial application accounts
- **THEN** system calculates warningLevel as HIGH

#### Scenario: Calculate warning level HIGH for many accounts
- **WHEN** identifier has more than 5 bound accounts and no financial applications
- **THEN** system calculates warningLevel as HIGH

#### Scenario: Calculate warning level MEDIUM
- **WHEN** identifier has 2-5 bound accounts and no financial applications
- **THEN** system calculates warningLevel as MEDIUM

#### Scenario: Calculate warning level LOW
- **WHEN** identifier has 1 bound account and account is not sensitive
- **THEN** system calculates warningLevel as LOW

### Requirement: User can view warning list

The system SHALL display warning list on home dashboard sorted by warning level (HIGH→MEDIUM→LOW). Each warning SHALL display message, affected account count, trigger time, and read status.

#### Scenario: View warning list sorted by level
- **WHEN** 3 unhandled warnings exist and user enters home dashboard
- **THEN** system displays warning list
- **AND** warnings are sorted by warningLevel (HIGH→MEDIUM→LOW)
- **AND** each warning displays message, affected account count, trigger time
- **AND** system distinguishes read/unread status

### Requirement: User can quick handle warnings from dashboard

The system SHALL provide "Quick Handle" button on home dashboard. When clicked, system SHALL display top 3 unhandled warnings sorted by level and deadline.

#### Scenario: Click quick handle button
- **WHEN** user clicks "Quick Handle" button on dashboard
- **THEN** system displays unhandled warning list (max 3 items)
- **AND** warnings are sorted by warningLevel (HIGH→MEDIUM→LOW) and then by deadline
- **AND** clicking warning enters warning detail page

### Requirement: User can view all warnings in secondary page

The system SHALL provide "View All" button to enter full warning list page. The page SHALL display all unhandled warnings with collapsible section for handled warnings.

#### Scenario: View all warnings
- **WHEN** user clicks "View All" button
- **THEN** system enters warning list secondary page
- **AND** system displays all unhandled warnings
- **AND** system displays collapsed section "View Handled Warnings" at bottom
- **AND** user can expand to view handled warnings

#### Scenario: Mark warning as handled
- **WHEN** user views warning detail and clicks "Mark Handled" button
- **THEN** system updates isHandled to true
- **AND** system records handledAt timestamp
- **AND** warning moves to handled section in warning list
- **AND** warning is removed from dashboard quick handle list

### Requirement: User can view warning details

The system SHALL display warning detail page with identifier info, deactivation plan info, affected account list (application icon, account name, binding purpose), and handling suggestions. Viewing SHALL automatically mark as read.

#### Scenario: View warning details
- **WHEN** warning record exists and user clicks warning card
- **THEN** system enters warning detail page
- **AND** system displays identifier info
- **AND** system displays deactivation plan info
- **AND** system displays affected account list with application icon, account name, binding purpose
- **AND** system displays handling suggestions
- **AND** system updates isRead to true

### Requirement: User can handle warning

The system SHALL allow users to mark warning as handled. After handling, warning SHALL be removed from dashboard and display handled indicator in warning list.

#### Scenario: Handle warning
- **WHEN** warning status is unhandled (isHandled=false) and user clicks "Handle" button
- **THEN** system updates isHandled to true
- **AND** system records handledAt timestamp
- **AND** system removes warning from dashboard
- **AND** warning list shows handled indicator

### Requirement: System clears warning on plan cancellation

The system SHALL delete related warning records when deactivation plan is cancelled.

#### Scenario: Clear warning on cancellation
- **WHEN** identifier status is PENDING_DEACTIVATION and related warnings exist
- **AND** user cancels deactivation plan
- **THEN** system deletes related WarningRecord entities
- **AND** dashboard no longer shows the warning