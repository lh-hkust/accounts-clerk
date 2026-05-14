package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.presentation.usecase.account.GetAccountListUseCase
import com.hermes.presentation.usecase.identifier.GetIdentifierListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

data class DataManagementUiState(
    val identifierCount: Int = 0,
    val accountCount: Int = 0,
    val databaseSize: String = "0 KB",
    val isLoading: Boolean = true,
    val exportData: String? = null,
    val importResult: String? = null,
    val showClearConfirm: Boolean = false
)

@HiltViewModel
class DataManagementViewModel @Inject constructor(
    private val getIdentifierListUseCase: GetIdentifierListUseCase,
    private val getAccountListUseCase: GetAccountListUseCase,
    private val identifierRepository: IdentityIdentifierRepository,
    private val accountRepository: ApplicationAccountRepository,
    private val bindingRepository: IdentifierBindingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataManagementUiState())
    val uiState: StateFlow<DataManagementUiState> = _uiState.asStateFlow()

    init {
        loadDataStats()
    }

    fun loadDataStats() {
        viewModelScope.launch {
            _uiState.value = DataManagementUiState(isLoading = true)
            try {
                val identifiers = getIdentifierListUseCase()
                val accounts = getAccountListUseCase()
                _uiState.value = DataManagementUiState(
                    identifierCount = identifiers.size,
                    accountCount = accounts.size,
                    databaseSize = estimateDatabaseSize(identifiers.size, accounts.size),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = DataManagementUiState(isLoading = false)
            }
        }
    }

    private fun estimateDatabaseSize(identifierCount: Int, accountCount: Int): String {
        val estimatedBytes = (identifierCount * 500 + accountCount * 800).toLong()
        return when {
            estimatedBytes < 1024 -> "$estimatedBytes B"
            estimatedBytes < 1024 * 1024 -> "${estimatedBytes / 1024} KB"
            else -> "${estimatedBytes / (1024 * 1024)} MB"
        }
    }

    fun importJson() {
        // 文件选择需要在Activity层面处理，这里仅标记需要导入
        _uiState.value = _uiState.value.copy(importResult = "请选择JSON文件")
    }

    fun importCsv() {
        // 文件选择需要在Activity层面处理
        _uiState.value = _uiState.value.copy(importResult = "请选择CSV文件")
    }

    fun exportJson() {
        viewModelScope.launch {
            try {
                val identifiers = identifierRepository.getAll()
                val accounts = accountRepository.getAll()
                val bindings = mutableListOf<com.hermes.domain.model.IdentifierBinding>()

                // 获取所有绑定关系
                identifiers.forEach { identifier ->
                    val identifierBindings = bindingRepository.getByIdentifierId(identifier.id ?: 0L)
                    bindings.addAll(identifierBindings)
                }

                val json = JSONObject()

                // 标识列表
                val identifiersArray = JSONArray()
                identifiers.forEach { identifier ->
                    val identifierJson = JSONObject()
                    identifierJson.put("id", identifier.id)
                    identifierJson.put("type", identifier.type.name)
                    identifierJson.put("value", identifier.value)
                    identifierJson.put("status", identifier.status.name)
                    identifierJson.put("plannedDeactTime", identifier.plannedDeactTime?.toString())
                    identifierJson.put("createdAt", identifier.createdAt.toString())
                    identifierJson.put("updatedAt", identifier.updatedAt.toString())
                    identifiersArray.put(identifierJson)
                }
                json.put("identifiers", identifiersArray)

                // 账号列表
                val accountsArray = JSONArray()
                accounts.forEach { account ->
                    val accountJson = JSONObject()
                    accountJson.put("id", account.id)
                    accountJson.put("applicationId", account.applicationId)
                    accountJson.put("accountId", account.accountIdentifier)
                    accountJson.put("accountName", account.accountName)
                    accountJson.put("status", account.status.name)
                    accountJson.put("createdAt", account.createdAt.toString())
                    accountJson.put("updatedAt", account.updatedAt.toString())
                    accountsArray.put(accountJson)
                }
                json.put("accounts", accountsArray)

                // 绑定关系列表
                val bindingsArray = JSONArray()
                bindings.forEach { binding ->
                    val bindingJson = JSONObject()
                    bindingJson.put("id", binding.id)
                    bindingJson.put("accountId", binding.accountId)
                    bindingJson.put("identifierId", binding.identifierId)
                    val purposesArray = JSONArray()
                    binding.purposes.forEach { purposesArray.put(it.name) }
                    bindingJson.put("purposes", purposesArray)
                    bindingJson.put("isPrimary", binding.isPrimary)
                    bindingsArray.put(bindingJson)
                }
                json.put("bindings", bindingsArray)

                // 导出时间
                json.put("exportedAt", java.time.Instant.now().toString())
                json.put("appVersion", "1.0.0")

                _uiState.value = _uiState.value.copy(exportData = json.toString(2))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(exportData = null)
            }
        }
    }

    fun exportCsv() {
        viewModelScope.launch {
            try {
                val identifiers = identifierRepository.getAll()
                val accounts = accountRepository.getAll()

                val csvBuilder = StringBuilder()

                // 标识CSV
                csvBuilder.appendLine("=== IDENTIFIERS ===")
                csvBuilder.appendLine("id,type,value,status,plannedDeactTime,createdAt,updatedAt")
                identifiers.forEach { identifier ->
                    csvBuilder.appendLine("${identifier.id},${identifier.type.name},${identifier.value},${identifier.status.name},${identifier.plannedDeactTime},${identifier.createdAt},${identifier.updatedAt}")
                }

                csvBuilder.appendLine()

                // 账号CSV
                csvBuilder.appendLine("=== ACCOUNTS ===")
                csvBuilder.appendLine("id,applicationId,accountId,accountName,status,createdAt,updatedAt")
                accounts.forEach { account ->
                    csvBuilder.appendLine("${account.id},${account.applicationId},${account.accountIdentifier},${account.accountName},${account.status.name},${account.createdAt},${account.updatedAt}")
                }

                _uiState.value = _uiState.value.copy(exportData = csvBuilder.toString())
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(exportData = null)
            }
        }
    }

    fun clearAllData() {
        _uiState.value = _uiState.value.copy(showClearConfirm = true)
    }

    fun confirmClearData() {
        viewModelScope.launch {
            try {
                // 删除所有数据（逐条删除）
                val accounts = accountRepository.getAll()
                accounts.forEach { accountRepository.delete(it) }

                val identifiers = identifierRepository.getAll()
                identifiers.forEach { identifierRepository.delete(it) }

                val bindings = mutableListOf<com.hermes.domain.model.IdentifierBinding>()
                identifiers.forEach { identifier ->
                    val identifierBindings = bindingRepository.getByIdentifierId(identifier.id ?: 0L)
                    bindings.addAll(identifierBindings)
                }
                bindings.forEach { bindingRepository.delete(it) }

                loadDataStats()
                _uiState.value = _uiState.value.copy(showClearConfirm = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(showClearConfirm = false)
            }
        }
    }

    fun cancelClearData() {
        _uiState.value = _uiState.value.copy(showClearConfirm = false)
    }

    fun clearExportData() {
        _uiState.value = _uiState.value.copy(exportData = null)
    }

    fun clearImportResult() {
        _uiState.value = _uiState.value.copy(importResult = null)
    }
}