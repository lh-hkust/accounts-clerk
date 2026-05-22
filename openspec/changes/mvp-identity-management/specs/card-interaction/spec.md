## ADDED Requirements

> 本规格定义卡片组件的手势交互行为。手势视觉反馈设计详见 [卡片手势设计规范](../../../docs/2_design/card-gesture-design.md)。

### Requirement: User can interact with identifier card via gestures

The system SHALL support four gesture types on identifier card: click, long-press, right-swipe, left-swipe.

#### Scenario: Click identifier card
- **WHEN** user taps identifier card
- **THEN** system navigates to identifier detail page (影响范围)
- **AND** system displays identifier full information and bound accounts

#### Scenario: Long-press identifier card
- **WHEN** user long-presses (500ms+) identifier card
- **THEN** system displays context menu
- **AND** menu items vary by identifier status:
  - ACTIVE: [编辑渠道, 设置到期提醒, 标记已失效, 删除渠道]
  - PENDING_DEACTIVATION: [编辑渠道, 修改到期提醒, 取消到期提醒, 标记已失效, 删除渠道]
  - DEACTIVATED: [编辑渠道, 删除渠道]

#### Scenario: Right-swipe identifier card (ACTIVE status)
- **WHEN** identifier status is ACTIVE
- **AND** user swipes right (card width 30%+)
- **THEN** system displays "设置到期提醒" button
- **AND** button background: primary color gradient (#3b82f6)
- **AND** user clicks button to trigger action

#### Scenario: Right-swipe identifier card (PENDING status)
- **WHEN** identifier status is PENDING_DEACTIVATION
- **AND** user swipes right
- **THEN** system displays "修改到期提醒" button

#### Scenario: Left-swipe identifier card (PENDING status)
- **WHEN** identifier status is PENDING_DEACTIVATION
- **AND** user swipes left
- **THEN** system displays "标记已处理" button
- **AND** user clicks button to mark warning as handled

#### Scenario: Left-swipe identifier card (other status)
- **WHEN** identifier status is ACTIVE or DEACTIVATED
- **AND** user swipes left
- **THEN** system shows no action (swipe ignored)

### Requirement: User can interact with account card via gestures

The system SHALL support four gesture types on account card.

#### Scenario: Click account card
- **WHEN** user taps account card
- **THEN** system navigates to account detail page
- **AND** system displays account info and bound identifiers

#### Scenario: Long-press account card
- **WHEN** user long-presses account card
- **THEN** system displays context menu
- **AND** menu items: [编辑账号, 更换验证渠道, 变更账号状态, 删除账号, 取消]

#### Scenario: Right-swipe account card
- **WHEN** user swipes right on account card
- **THEN** system displays "编辑" button
- **AND** button background: primary color gradient
- **AND** user clicks button to open edit page

#### Scenario: Left-swipe account card
- **WHEN** user swipes left on account card
- **THEN** system displays "删除" button
- **AND** button background: danger color gradient (#ef4444)
- **AND** user clicks button to trigger delete confirmation

### Requirement: User can delete account with anti-mistake confirmation

The system SHALL require user to input account name to confirm deletion.

#### Scenario: Delete account confirmation dialog
- **WHEN** user triggers delete action (long-press menu or left-swipe)
- **THEN** system displays confirmation dialog
- **AND** dialog title: "确认删除账号？"
- **AND** dialog shows: "账号：微博 - 工作小号01"
- **AND** dialog shows input prompt: "请输入「工作小号01」确认删除"
- **AND** delete button disabled until correct input
- **AND** after correct input, delete button becomes enabled

#### Scenario: Delete account execution
- **WHEN** user enters correct account name "工作小号01"
- **AND** user clicks "确认删除"
- **THEN** system deletes ApplicationAccount entity
- **AND** system deletes all related IdentifierBinding entities
- **AND** system creates BindingHistoryRecord for each unbind
- **AND** system returns to account list page

### Requirement: User can interact with warning card via gestures

The system SHALL support gesture interaction on warning cards.

#### Scenario: Click warning card
- **WHEN** user taps warning card
- **THEN** system navigates to identifier detail page (影响范围)

#### Scenario: Long-press warning card
- **WHEN** user long-presses warning card
- **THEN** system displays context menu
- **AND** menu items: [标记已处理, 设置到期提醒, 查看影响账号, 取消]

#### Scenario: Right-swipe warning card
- **WHEN** user swipes right on warning card
- **THEN** system displays "标记已处理" button
- **AND** user clicks button to mark warning as handled

#### Scenario: Left-swipe warning card
- **WHEN** user swipes left on warning card
- **THEN** system shows no action

### Requirement: User cannot delete identifier with bindings

The system SHALL prevent deletion of identifiers that have bound accounts.

#### Scenario: Attempt delete identifier with bindings
- **WHEN** identifier has 2 bound accounts
- **AND** user triggers delete action
- **THEN** system displays blocking dialog
- **AND** dialog shows: "此渠道绑定了 2 个账号，无法删除"
- **AND** dialog shows: "请先解绑以下账号后再删除"
- **AND** dialog displays bound account list (clickable to navigate)
- **AND** dialog buttons: "查看绑定账号", "取消"

#### Scenario: Delete identifier without bindings
- **WHEN** identifier has 0 bound accounts
- **AND** user triggers delete action
- **THEN** system displays confirmation: "确认删除渠道？"
- **AND** user confirms to delete