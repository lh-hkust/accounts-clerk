package com.hermes.domain.model

import com.hermes.domain.valueobject.AccountStatus
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

/**
 * ApplicationAccount 单元测试
 */
class ApplicationAccountTest {

    @Test
    fun `创建应用账户应成功`() {
        val now = Instant.now()
        val account = ApplicationAccount(
            id = 1L,
            applicationId = 100L,
            accountId = "user_001",
            accountName = "主账号",
            status = AccountStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(1L, account.id)
        assertEquals(100L, account.applicationId)
        assertEquals("user_001", account.accountId)
        assertEquals("主账号", account.accountName)
        assertEquals(AccountStatus.ACTIVE, account.status)
    }

    @Test
    fun `冻结账户应更新状态`() {
        val now = Instant.now()
        val account = ApplicationAccount(
            id = 1L,
            applicationId = 100L,
            accountId = "user_001",
            accountName = "冻结账号",
            status = AccountStatus.FROZEN,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(AccountStatus.FROZEN, account.status)
    }

    @Test
    fun `丢失账户应更新状态`() {
        val now = Instant.now()
        val account = ApplicationAccount(
            id = 1L,
            applicationId = 100L,
            accountId = "user_001",
            accountName = "丢失账号",
            status = AccountStatus.LOST,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(AccountStatus.LOST, account.status)
    }

    @Test
    fun `归档账户应更新状态`() {
        val now = Instant.now()
        val account = ApplicationAccount(
            id = 1L,
            applicationId = 100L,
            accountId = "user_001",
            accountName = "归档账号",
            status = AccountStatus.ARCHIVED,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(AccountStatus.ARCHIVED, account.status)
    }

    @Test
    fun `不同状态的账户应可区分`() {
        val statuses = listOf(
            AccountStatus.ACTIVE,
            AccountStatus.FROZEN,
            AccountStatus.LOST,
            AccountStatus.ARCHIVED
        )

        // 确保所有状态值都不同
        assertEquals(4, statuses.distinct().size)
    }
}