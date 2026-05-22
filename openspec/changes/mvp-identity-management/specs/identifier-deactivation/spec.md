## ADDED Requirements

### Requirement: User can set deactivation plan

The system SHALL allow users to set deactivation plan for identity identifiers. The plan SHALL include deactivation date and reason. Deactivation date MUST be greater than current date.

#### Scenario: Set deactivation plan
- **WHEN** identifier status is ACTIVE and identifier has 5 bound accounts
- **AND** user clicks "Set Deactivation Plan"
- **AND** user selects deactivation date 30 days later
- **AND** user enters reason "Phone number will be cancelled"
- **AND** user clicks "Confirm"
- **THEN** system updates identifier status to PENDING_DEACTIVATION
- **AND** system sets plannedDeactTime to selected date
- **AND** system sets deactReason to entered reason
- **AND** system creates IdentifierDeactivation entity with status=SCHEDULED
- **AND** system triggers warning creation

#### Scenario: Reject past deactivation date
- **WHEN** user sets deactivation date earlier than current date
- **THEN** system displays error "Deactivation date must be greater than current date"
- **AND** system does NOT create deactivation plan

### Requirement: User can view deactivation plan details

The system SHALL display deactivation plan card in identifier detail page when status is PENDING_DEACTIVATION. The card SHALL show countdown days, deactivation reason, affected accounts list, and handling suggestions.

#### Scenario: View deactivation plan
- **WHEN** identifier status is PENDING_DEACTIVATION and user enters identifier detail page
- **THEN** system displays deactivation plan card
- **AND** system displays countdown days
- **AND** system displays deactivation reason
- **AND** system displays affected account list with warning level
- **AND** system displays handling suggestions

### Requirement: User can cancel deactivation plan

The system SHALL allow users to cancel deactivation plan and enter cancel reason. After cancellation, identifier status SHALL return to ACTIVE and related warnings SHALL be deleted.

#### Scenario: Cancel deactivation plan
- **WHEN** identifier status is PENDING_DEACTIVATION and related warnings exist
- **AND** user clicks "Cancel Deactivation Plan"
- **AND** user enters cancel reason "Phone number continues to be used"
- **AND** user clicks "Confirm"
- **THEN** system updates identifier status to ACTIVE
- **AND** system clears plannedDeactTime and deactReason
- **AND** system updates IdentifierDeactivation status to CANCELLED
- **AND** system deletes related WarningRecord entities

### Requirement: User can modify deactivation date

The system SHALL allow users to modify deactivation date of existing plan. New date MUST also be greater than current date.

#### Scenario: Modify deactivation date
- **WHEN** identifier status is PENDING_DEACTIVATION
- **AND** user clicks "Modify Plan"
- **AND** user changes deactivation date to 60 days later
- **AND** user clicks "Confirm"
- **THEN** system updates plannedDeactTime
- **AND** system updates IdentifierDeactivation.scheduledTime
- **AND** system updates warning trigger time

### Requirement: User can mark identifier as deactivated immediately

The system SHALL allow users to mark identifier as DEACTIVATED without waiting for planned date. This operation SHALL require confirmation dialog and SHALL clear all related warnings.

#### Scenario: Mark as deactivated from ACTIVE status
- **WHEN** identifier status is ACTIVE
- **AND** user clicks "Mark as Deactivated" from context menu
- **THEN** system displays confirmation dialog
- **AND** dialog title: "确认标记为已失效？"
- **AND** dialog message: "此渠道将立即失效，绑定账号将无法使用此渠道登录或验证。"
- **AND** dialog shows affected account count
- **AND** primary button: "确认失效" (danger color)
- **AND** secondary button: "取消"

#### Scenario: Execute mark as deactivated (ACTIVE)
- **WHEN** user clicks "确认失效" in confirmation dialog
- **AND** identifier status is ACTIVE
- **THEN** system updates identifier status to DEACTIVATED
- **AND** system sets actualDeactTime to current timestamp
- **AND** system creates IdentifierDeactivation entity with status=EXECUTED and deactType=MANUAL
- **AND** system deletes all related WarningRecord entities
- **AND** system returns to identifier list page

#### Scenario: Mark as deactivated from PENDING_DEACTIVATION status
- **WHEN** identifier status is PENDING_DEACTIVATION
- **AND** user clicks "Mark as Deactivated" from context menu
- **THEN** system displays confirmation dialog
- **AND** dialog message: "此渠道将立即失效，原定于X天后停机的计划将提前执行。"
- **AND** dialog shows affected account count

#### Scenario: Execute mark as deactivated (PENDING_DEACTIVATION)
- **WHEN** user clicks "确认失效" in confirmation dialog
- **AND** identifier status is PENDING_DEACTIVATION
- **THEN** system updates identifier status to DEACTIVATED
- **AND** system sets actualDeactTime to current timestamp
- **AND** system updates IdentifierDeactivation status to EXECUTED
- **AND** system deletes all related WarningRecord entities
- **AND** system returns to identifier list page

### Requirement: System enforces deactivation status rules

The system SHALL prevent setting deactivation plan for identifiers that are already DEACTIVATED or INVALIDATED.

#### Scenario: Prevent plan on deactivated identifier
- **WHEN** identifier status is DEACTIVATED and user attempts to set deactivation plan
- **THEN** system displays message "Identifier already deactivated, no plan needed"
- **AND** system does NOT allow operation