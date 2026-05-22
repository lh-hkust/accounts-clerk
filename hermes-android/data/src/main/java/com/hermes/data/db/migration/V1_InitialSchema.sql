-- Hermes Account Management Database Schema
-- Version: 1.0
-- NIST SP 800-63B Terminology

-- Identity Identifier Table
CREATE TABLE identity_identifier (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT NOT NULL CHECK(type IN ('PHONE', 'EMAIL')),
    value TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK(status IN ('ACTIVE', 'PENDING_DEACTIVATION', 'DEACTIVATED', 'INVALIDATED')),
    planned_deact_time INTEGER,
    deact_reason TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    UNIQUE(type, value)
);

-- Application Table
CREATE TABLE application (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    type TEXT NOT NULL DEFAULT 'BOTH' CHECK(type IN ('WEB_SITE', 'MOBILE_APP', 'BOTH')),
    official_url TEXT,
    icon_url TEXT,
    category TEXT,
    is_active INTEGER NOT NULL DEFAULT 1,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

-- Application Account Table
CREATE TABLE application_account (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    application_id INTEGER NOT NULL,
    account_name TEXT NOT NULL,
    account_identifier TEXT,
    nickname TEXT,
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK(status IN ('ACTIVE', 'FROZEN', 'LOST', 'ARCHIVED')),
    keep_alive_enabled INTEGER NOT NULL DEFAULT 1,
    last_login_date INTEGER,
    notes TEXT,
    tags TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (application_id) REFERENCES application(id),
    UNIQUE(application_id, account_identifier)
);

-- Identifier Binding Table
CREATE TABLE identifier_binding (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    identifier_id INTEGER NOT NULL,
    purposes TEXT NOT NULL,
    is_primary INTEGER NOT NULL DEFAULT 0,
    bound_at INTEGER NOT NULL,
    verified_at INTEGER,
    notes TEXT,
    FOREIGN KEY (account_id) REFERENCES application_account(id),
    FOREIGN KEY (identifier_id) REFERENCES identity_identifier(id),
    UNIQUE(account_id, identifier_id)
);

-- Account Extension Table
CREATE TABLE account_extension (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    key TEXT NOT NULL,
    value TEXT,
    label TEXT NOT NULL,
    field_type TEXT NOT NULL CHECK(field_type IN ('TEXT', 'NUMBER', 'DATE', 'SELECT', 'MULTI_LINE')),
    options TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (account_id) REFERENCES application_account(id),
    UNIQUE(account_id, key)
);

-- Identifier Deactivation Table
CREATE TABLE identifier_deactivation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    identifier_id INTEGER NOT NULL,
    deactivation_type TEXT NOT NULL CHECK(deactivation_type IN ('PHONE_NUMBER_CHANGE', 'EMAIL_CHANGE', 'ACCOUNT_CLOSURE', 'OTHER')),
    status TEXT NOT NULL DEFAULT 'SCHEDULED' CHECK(status IN ('SCHEDULED', 'EXECUTED', 'CANCELLED')),
    scheduled_time INTEGER NOT NULL,
    executed_time INTEGER,
    cancelled_time INTEGER,
    cancel_reason TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (identifier_id) REFERENCES identity_identifier(id)
);

-- Warning Record Table
CREATE TABLE warning_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    identifier_id INTEGER,
    account_id INTEGER,
    warning_type TEXT NOT NULL CHECK(warning_type IN ('DEACTIVATION_PLAN', 'BOUND_ACCOUNT_CHANGE', 'DEADLINE_APPROACHING')),
    warning_level TEXT NOT NULL CHECK(warning_level IN ('HIGH', 'MEDIUM', 'LOW')),
    message TEXT NOT NULL,
    triggered_at INTEGER NOT NULL,
    is_read INTEGER NOT NULL DEFAULT 0,
    is_handled INTEGER NOT NULL DEFAULT 0,
    handled_at INTEGER,
    FOREIGN KEY (identifier_id) REFERENCES identity_identifier(id),
    FOREIGN KEY (account_id) REFERENCES application_account(id)
);

-- Binding History Record Table
CREATE TABLE binding_history_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    identifier_id INTEGER NOT NULL,
    action_type TEXT NOT NULL CHECK(action_type IN ('BIND', 'UNBIND', 'CHANGE_PURPOSE', 'SWITCH_IDENTIFIER')),
    previous_purposes TEXT,
    new_purposes TEXT,
    previous_identifier_id INTEGER,
    new_identifier_id INTEGER,
    action_at INTEGER NOT NULL,
    action_by TEXT,
    notes TEXT,
    FOREIGN KEY (account_id) REFERENCES application_account(id),
    FOREIGN KEY (identifier_id) REFERENCES identity_identifier(id)
);

-- Indexes for common queries
CREATE INDEX idx_identifier_status ON identity_identifier(status);
CREATE INDEX idx_identifier_type_value ON identity_identifier(type, value);
CREATE INDEX idx_account_application ON application_account(application_id);
CREATE INDEX idx_account_status ON application_account(status);
CREATE INDEX idx_binding_account ON identifier_binding(account_id);
CREATE INDEX idx_binding_identifier ON identifier_binding(identifier_id);
CREATE INDEX idx_warning_level ON warning_record(warning_level);
CREATE INDEX idx_warning_unhandled ON warning_record(is_handled, warning_level);
CREATE INDEX idx_deactivation_identifier ON identifier_deactivation(identifier_id);
CREATE INDEX idx_deactivation_scheduled ON identifier_deactivation(status, scheduled_time);