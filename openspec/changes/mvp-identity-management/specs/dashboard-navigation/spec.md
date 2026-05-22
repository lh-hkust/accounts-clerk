## Dashboard and Navigation Requirements

> 本规格定义首页概览和导航相关的用户交互行为。

---

### Requirement: Dashboard security index displays meaningful state

The system SHALL display appropriate security index based on actual data status. Empty state SHALL provide guidance rather than misleading numbers.

#### Scenario: Empty state security index
- **WHEN** system has zero identifiers and zero accounts
- **THEN** security index section displays "暂无数据"
- **AND** displays prompt: "添加验证渠道开始保护账号"
- **AND** provides "立即添加" button navigates to Add Identifier screen

#### Scenario: Partial data security index
- **WHEN** system has identifiers but no accounts
- **THEN** security index displays based on identifier health status
- **AND** health calculation: (activeCount / totalCount) * 100
- **AND** displays context: "已录入 X 个验证渠道，未绑定账号"
- **AND** prompt: "添加账号建立绑定关系"

#### Scenario: Full data security index
- **WHEN** system has both identifiers and accounts
- **THEN** security index considers:
  - Warning factor: unhandledWarningCount * 10 points deducted
  - Identifier health: unhealthyRate * 20 points deducted
  - Account status: frozenRate * 15 points deducted (TBD: 需要AccountStats.frozenCount)
  - Binding coverage: unboundRate * 15 points deducted (TBD: 需要AccountStats.boundCount)
- **AND** base score is 100, minimum score is 0
- **AND** displays breakdown factors in card footer

#### Scenario: Full data security index calculation formula (更新 2026-05-15)
- **CALCULATION**: `score = 100 - warningDeduction - healthDeduction - statusDeduction - coverageDeduction`
- **WHERE**:
  - `warningDeduction = unhandledWarningCount * 10`
  - `healthDeduction = (identifierStats.totalCount - identifierStats.activeCount) / identifierStats.totalCount * 20`
  - `statusDeduction = accountStats.frozenCount / accountStats.totalCount * 15` (待实现)
  - `coverageDeduction = (accountStats.totalCount - accountStats.boundCount) / accountStats.totalCount * 15` (待实现)
- **MINIMUM**: score is clamped to 0

### Requirement: Dashboard quick entries navigate correctly

The system SHALL provide working navigation from dashboard quick entry cards.

#### Scenario: Click verification channel card
- **WHEN** user clicks "验证渠道" quick entry card
- **THEN** system navigates to Identifier List screen
- **AND** bottom navigation updates to highlight "首页" (current page)

#### Scenario: Click account library card
- **WHEN** user clicks "账号库" quick entry card
- **THEN** system navigates to Account List screen
- **AND** bottom navigation updates accordingly

---

### Requirement: Navigation bar click refreshes current page

The system SHALL respond to navigation bar clicks even when on current page, providing meaningful feedback.

#### Scenario: Click current page nav item (list page)
- **WHEN** user is on Identifier/Account List screen
- **AND** user clicks "首页" navigation item
- **THEN** system scrolls list to top position
- **AND** provides subtle visual feedback (ripple animation)

#### Scenario: Click current page nav item (dashboard)
- **WHEN** user is on Dashboard screen
- **AND** user clicks "首页" navigation item (already current)
- **THEN** system refreshes dashboard statistics
- **AND** provides subtle visual feedback

#### Scenario: Click non-current nav item
- **WHEN** user clicks navigation item for different page
- **THEN** system navigates to target page
- **AND** target page loads with default state (scroll to top)

---

### Requirement: Navigation back button behavior consistent

The system SHALL maintain consistent back navigation behavior across all screens.

#### Scenario: Back from detail page to list
- **WHEN** user clicks back from Identifier/Account Detail screen
- **THEN** system returns to source list screen
- **AND** list screen retains previous scroll position
- **AND** list data refreshes if changes occurred

#### Scenario: Back from settings sub-page
- **WHEN** user clicks back from Notification/Privacy/Data screen
- **THEN** system returns to Settings screen
- **AND** Settings screen retains scroll position

#### Scenario: Back from add/edit page
- **WHEN** user clicks back from Add/Edit screen
- **AND** no changes made
- **THEN** system returns to previous screen without confirmation
- **WHEN** user clicks back and changes exist
- **THEN** system displays discard confirmation dialog

---

### Requirement: Operation feedback auto-dismisses

The system SHALL display transient feedback for operation results that auto-dismiss without requiring user confirmation click.

#### Scenario: Save success feedback
- **WHEN** user saves account/identifier successfully
- **THEN** system displays success message: "保存成功"
- **AND** message displays at bottom (Snackbar style)
- **AND** message auto-dismisses after 2 seconds
- **AND** no "确定" button required

#### Scenario: Delete success feedback
- **WHEN** user deletes item successfully
- **THEN** system displays: "已删除"
- **AND** message auto-dismisses after 2 seconds

#### Scenario: Operation failure feedback
- **WHEN** operation fails (network, validation, etc.)
- **THEN** system displays error message with red indicator
- **AND** message auto-dismisses after 3 seconds (longer for errors)

---

### Requirement: Status badge terminology unified across all screens

The system SHALL use consistent status display names and colors throughout the application.

#### Scenario: Identifier status display
| Domain Status | UI Display | Color |
|---------------|------------|-------|
| ACTIVE | 正常使用 | #22c55e |
| PENDING_DEACTIVATION | 即将到期 | #eab308 |
| DEACTIVATED | 已失效 | #ef4444 |
| INVALIDATED | 已失效 | #ef4444 |

#### Scenario: Account status display
| Domain Status | UI Display | Color |
|---------------|------------|-------|
| ACTIVE | 正常使用 | #22c55e |
| FROZEN | 已冻结 | #ef4444 |
| LOST | 已丢失 | #6b7280 |
| ARCHIVED | 已归档 | #6b7280 |

#### Scenario: Warning level display
| Domain Level | UI Display | Badge |
|--------------|------------|-------|
| CRITICAL | 紧急 | badge-danger (red) |
| HIGH | 建议 | badge-warning (yellow) |
| MEDIUM | 提示 | badge-info (blue) |
| LOW | 低 | badge-muted (gray) |

---

### Requirement: First-time user guidance flow

The system SHALL provide helpful guidance when user first uses the application with empty data.

#### Scenario: Welcome page to dashboard
- **WHEN** user completes welcome page and clicks "立即开启"
- **THEN** system navigates to Dashboard
- **AND** Dashboard displays empty state guidance
- **AND** prominent "添加验证渠道" call-to-action displayed

#### Scenario: Empty dashboard with guidance
- **WHEN** Dashboard has no data
- **THEN** quick entry cards display empty count: "0 个"
- **AND** warning section displays: "暂无提醒"
- **AND** center card displays: "开始保护您的账号资产"
- **AND** provides step-by-step hint: "第一步：添加验证渠道"

---

*文档版本: v1.0*
*创建日期: 2026-05-15*