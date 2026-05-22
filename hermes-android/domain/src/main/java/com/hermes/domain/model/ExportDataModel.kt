package com.hermes.domain.model

import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.domain.valueobject.AccountStatus
import java.time.Instant

/**
 * 导出数据模型
 * 用于JSON序列化的数据结构
 *
 * @see export-file-format.md 二、明文JSON文件格式
 */

/**
 * 导出数据容器
 */
data class ExportData(
    val version: String = "1.0",
    val exportType: String = "FULL",
    val exportedAt: String,
    val appVersion: String = "1.0.0",
    val identifiers: List<ExportIdentifier>,
    val accounts: List<ExportAccount>,
    val bindings: List<ExportBinding>,
    val applications: List<ExportApplication>
)

/**
 * 导出的身份标识
 */
data class ExportIdentifier(
    val id: Long,
    val type: String,
    val value: String,
    val status: String,
    val plannedDeactTime: String?,
    val createdAt: String
)

/**
 * 导出的应用账户
 */
data class ExportAccount(
    val id: Long,
    val applicationId: Long,
    val accountName: String,
    val accountIdentifier: String?,
    val nickname: String?,
    val status: String,
    val createdAt: String
)

/**
 * 导出的标识绑定
 */
data class ExportBinding(
    val id: Long,
    val accountId: Long,
    val identifierId: Long,
    val purposes: List<String>,
    val isPrimary: Boolean,
    val boundAt: String
)

/**
 * 导出的应用
 */
data class ExportApplication(
    val id: Long,
    val name: String,
    val category: String?
)

/**
 * 从领域模型转换为导出数据模型
 */
fun IdentityIdentifier.toExportModel(): ExportIdentifier {
    return ExportIdentifier(
        id = id ?: 0L,
        type = type.name,
        value = value,
        status = status.name,
        plannedDeactTime = plannedDeactTime?.toString(),
        createdAt = createdAt.toString()
    )
}

fun ApplicationAccount.toExportModel(): ExportAccount {
    return ExportAccount(
        id = id ?: 0L,
        applicationId = applicationId,
        accountName = accountName,
        accountIdentifier = accountIdentifier,
        nickname = nickname,
        status = status.name,
        createdAt = createdAt.toString()
    )
}

fun IdentifierBinding.toExportModel(): ExportBinding {
    return ExportBinding(
        id = id ?: 0L,
        accountId = accountId,
        identifierId = identifierId,
        purposes = purposes.map { it.name },
        isPrimary = isPrimary,
        boundAt = boundAt.toString()
    )
}

fun Application.toExportModel(): ExportApplication {
    return ExportApplication(
        id = id ?: 0L,
        name = name,
        category = category
    )
}

/**
 * 导出数据构建器
 */
class ExportDataBuilder {
    private val identifiers = mutableListOf<ExportIdentifier>()
    private val accounts = mutableListOf<ExportAccount>()
    private val bindings = mutableListOf<ExportBinding>()
    private val applications = mutableListOf<ExportApplication>()

    fun addIdentifier(identifier: IdentityIdentifier) {
        identifiers.add(identifier.toExportModel())
    }

    fun addIdentifiers(identifiers: List<IdentityIdentifier>) {
        identifiers.forEach { addIdentifier(it) }
    }

    fun addAccount(account: ApplicationAccount) {
        accounts.add(account.toExportModel())
    }

    fun addAccounts(accounts: List<ApplicationAccount>) {
        accounts.forEach { addAccount(it) }
    }

    fun addBinding(binding: IdentifierBinding) {
        bindings.add(binding.toExportModel())
    }

    fun addBindings(bindings: List<IdentifierBinding>) {
        bindings.forEach { addBinding(it) }
    }

    fun addApplication(application: Application) {
        applications.add(application.toExportModel())
    }

    fun addApplications(applications: List<Application>) {
        applications.forEach { addApplication(it) }
    }

    fun build(): ExportData {
        return ExportData(
            version = "1.0",
            exportType = "FULL",
            exportedAt = Instant.now().toString(),
            appVersion = "1.0.0",
            identifiers = identifiers,
            accounts = accounts,
            bindings = bindings,
            applications = applications
        )
    }
}

/**
 * 导入数据模型（用于解析导入的JSON）
 */
data class ImportData(
    val version: String?,
    val exportType: String?,
    val exportedAt: String?,
    val appVersion: String?,
    val identifiers: List<ImportIdentifier>?,
    val accounts: List<ImportAccount>?,
    val bindings: List<ImportBinding>?,
    val applications: List<ImportApplication>?
)

data class ImportIdentifier(
    val id: Long?,
    val type: String?,
    val value: String?,
    val status: String?,
    val plannedDeactTime: String?,
    val createdAt: String?
) {
    fun toDomainModel(): IdentityIdentifier? {
        if (type == null || value == null) return null
        return try {
            IdentityIdentifier(
                id = null, // 导入时分配新ID
                type = IdentifierType.valueOf(type),
                value = value,
                status = status?.let { IdentifierStatus.valueOf(it) } ?: IdentifierStatus.ACTIVE,
                plannedDeactTime = plannedDeactTime?.let { Instant.parse(it) },
                createdAt = createdAt?.let { Instant.parse(it) } ?: Instant.now(),
                updatedAt = Instant.now()
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class ImportAccount(
    val id: Long?,
    val applicationId: Long?,
    val accountName: String?,
    val accountIdentifier: String?,
    val nickname: String?,
    val status: String?,
    val createdAt: String?
) {
    fun toDomainModel(): ApplicationAccount? {
        if (applicationId == null || accountName == null) return null
        return try {
            ApplicationAccount(
                id = null, // 导入时分配新ID
                applicationId = applicationId,
                accountName = accountName,
                accountIdentifier = accountIdentifier,
                nickname = nickname,
                status = status?.let { AccountStatus.valueOf(it) } ?: AccountStatus.ACTIVE,
                createdAt = createdAt?.let { Instant.parse(it) } ?: Instant.now(),
                updatedAt = Instant.now()
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class ImportBinding(
    val id: Long?,
    val accountId: Long?,
    val identifierId: Long?,
    val purposes: List<String>?,
    val isPrimary: Boolean?,
    val boundAt: String?
) {
    fun toDomainModel(): IdentifierBinding? {
        if (accountId == null || identifierId == null || purposes == null) return null
        return try {
            IdentifierBinding(
                id = null, // 导入时分配新ID
                accountId = accountId,
                identifierId = identifierId,
                purposes = purposes.map { BindingPurpose.valueOf(it) },
                isPrimary = isPrimary ?: false,
                boundAt = boundAt?.let { Instant.parse(it) } ?: Instant.now()
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class ImportApplication(
    val id: Long?,
    val name: String?,
    val category: String?
) {
    fun toDomainModel(): Application? {
        if (name == null) return null
        return Application(
            id = null, // 导入时分配新ID
            name = name,
            category = category,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}

/**
 * 导入预览数据
 */
data class ImportPreview(
    val identifierCount: Int,
    val accountCount: Int,
    val bindingCount: Int,
    val applicationCount: Int,
    val conflicts: List<ImportConflict>
)

/**
 * 导入冲突
 */
data class ImportConflict(
    val type: ConflictType,
    val description: String,
    val existingId: Long,
    val importId: Long?
)

enum class ConflictType {
    DUPLICATE_IDENTIFIER,
    DUPLICATE_ACCOUNT,
    UNKNOWN_APPLICATION
}

/**
 * 导入模式
 */
enum class ImportMode {
    MERGE,       // 新增不存在数据，保留已有数据
    OVERWRITE,   // 新增不存在数据，替换已有数据
    SKIP_DUPLICATE // 仅导入不存在数据，跳过所有重复
}