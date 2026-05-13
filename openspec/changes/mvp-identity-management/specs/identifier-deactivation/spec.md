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

### Requirement: System enforces deactivation status rules

The system SHALL prevent setting deactivation plan for identifiers that are already DEACTIVATED or INVALIDATED.

#### Scenario: Prevent plan on deactivated identifier
- **WHEN** identifier status is DEACTIVATED and user attempts to set deactivation plan
- **THEN** system displays message "Identifier already deactivated, no plan needed"
- **AND** system does NOT allow operation