package com.hermes.presentation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.model.ExportFileFormat
import com.hermes.domain.model.ImportData
import com.hermes.domain.model.ImportMode
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.presentation.ui.component.ImportPreviewData
import com.hermes.presentation.ui.component.ImportResultData
import com.hermes.presentation.usecase.export.ImportExportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 导入导出ViewModel
 * 管理导入导出操作的状态和流程
 */
@HiltViewModel
class ExportImportViewModel @Inject constructor(
    private val importExportUseCase: ImportExportUseCase,
    private val identifierRepository: IdentityIdentifierRepository,
    private val accountRepository: ApplicationAccountRepository,
    private val bindingRepository: IdentifierBindingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportImportUiState())
    val uiState: StateFlow<ExportImportUiState> = _uiState.asStateFlow()

    // 暂存导出数据
    private var pendingExportData: ByteArray? = null
    private var pendingExportPassword: String? = null

    // 暂存导入数据
    private var pendingImportFileData: ByteArray? = null
    private var pendingImportJsonData: String? = null

    init {
        loadDataStats()
    }

    /**
     * 加载数据统计
     */
    fun loadDataStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingStats = true)
            try {
                val identifiers = identifierRepository.getAll()
                val accounts = accountRepository.getAll()
                _uiState.value = _uiState.value.copy(
                    identifierCount = identifiers.size,
                    accountCount = accounts.size,
                    databaseSize = estimateDatabaseSize(identifiers.size, accounts.size),
                    isLoadingStats = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingStats = false)
            }
        }
    }

    /**
     * 开始明文导出
     */
    fun startPlainExport() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    showExportProgress = true,
                    exportProgress = 0,
                    exportStage = "正在处理：准备数据",
                    exportComplete = false
                )

                val exportData = importExportUseCase.prepareExportData(
                    callback = object : ImportExportUseCase.ExportProgressCallback {
                        override fun onProgress(progress: Int, stage: String) {
                            _uiState.value = _uiState.value.copy(
                                exportProgress = progress,
                                exportStage = stage
                            )
                        }

                        override fun onComplete() {
                            // 进度完成
                        }

                        override fun onError(message: String) {
                            _uiState.value = _uiState.value.copy(
                                showExportProgress = false,
                                errorMessage = message
                            )
                        }
                    }
                )

                val jsonData = importExportUseCase.serializeToJson(exportData)
                pendingExportData = jsonData.toByteArray(Charsets.UTF_8)
                pendingExportPassword = null

                _uiState.value = _uiState.value.copy(
                    exportProgress = 100,
                    exportStage = "数据准备完成，等待保存"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showExportProgress = false,
                    errorMessage = "导出失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 开始安全导出
     */
    fun startSecureExport(password: String?) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    showExportProgress = true,
                    exportProgress = 0,
                    exportStage = "正在处理：准备数据",
                    exportComplete = false
                )

                val exportData = importExportUseCase.prepareExportData(
                    callback = object : ImportExportUseCase.ExportProgressCallback {
                        override fun onProgress(progress: Int, stage: String) {
                            _uiState.value = _uiState.value.copy(
                                exportProgress = progress,
                                exportStage = stage
                            )
                        }

                        override fun onComplete() {}

                        override fun onError(message: String) {
                            _uiState.value = _uiState.value.copy(
                                showExportProgress = false,
                                errorMessage = message
                            )
                        }
                    }
                )

                val jsonData = importExportUseCase.serializeToJson(exportData)

                _uiState.value = _uiState.value.copy(
                    exportProgress = 0,
                    exportStage = "正在处理：加密数据"
                )

                val encryptedData = importExportUseCase.encryptExportData(
                    jsonData = jsonData,
                    password = password,
                    callback = object : ImportExportUseCase.ExportProgressCallback {
                        override fun onProgress(progress: Int, stage: String) {
                            _uiState.value = _uiState.value.copy(
                                exportProgress = progress,
                                exportStage = stage
                            )
                        }

                        override fun onComplete() {}

                        override fun onError(message: String) {
                            _uiState.value = _uiState.value.copy(
                                showExportProgress = false,
                                errorMessage = message
                            )
                        }
                    }
                )

                pendingExportData = encryptedData
                pendingExportPassword = password

                _uiState.value = _uiState.value.copy(
                    exportProgress = 100,
                    exportStage = "加密完成，等待保存"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showExportProgress = false,
                    errorMessage = "导出失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 写入导出文件（SAF选择后）
     */
    fun writeExportFile(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val data = pendingExportData
                if (data == null) {
                    _uiState.value = _uiState.value.copy(
                        showExportProgress = false,
                        errorMessage = "无导出数据"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    exportStage = "正在处理：写入文件"
                )

                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(data)
                    }
                }

                val filePath = uri.path ?: uri.toString()
                _uiState.value = _uiState.value.copy(
                    exportProgress = 100,
                    exportStage = if (pendingExportPassword != null) "文件已加密保存" else "导出完成",
                    exportComplete = true,
                    exportFilePath = filePath
                )

                // 清除暂存数据
                pendingExportData = null
                pendingExportPassword = null

                // 刷新数据统计
                loadDataStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showExportProgress = false,
                    errorMessage = "写入文件失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 复制导出路径
     */
    fun copyExportPath(context: Context) {
        val filePath = _uiState.value.exportFilePath
        if (filePath != null) {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Export Path", filePath))
        }
    }

    /**
     * 关闭导出进度对话框
     */
    fun dismissExportProgress() {
        _uiState.value = _uiState.value.copy(
            showExportProgress = false,
            exportComplete = false,
            exportFilePath = null
        )
    }

    /**
     * 读取导入文件（SAF选择后）
     */
    fun readImportFile(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoadingImport = true,
                    importFileName = uri.lastPathSegment ?: "导入文件"
                )

                val fileData = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.readBytes()
                    }
                }

                if (fileData == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoadingImport = false,
                        errorMessage = "无法读取文件"
                    )
                    return@launch
                }

                // 检测文件类型
                val fileType = importExportUseCase.detectFileType(fileData)

                when (fileType) {
                    ExportFileFormat.FileType.ENCRYPTED -> {
                        // 加密文件：需要检测是密码模式还是无密码模式
                        pendingImportFileData = fileData
                        try {
                            val (header, _) = ExportFileFormat.parse(fileData)
                            if (header.isPasswordMode()) {
                                // 密码模式：显示密码输入对话框
                                _uiState.value = _uiState.value.copy(
                                    isLoadingImport = false,
                                    showImportPasswordDialog = true
                                )
                            } else {
                                // 无密码模式：自动解密
                                decryptImportFile(null)
                            }
                        } catch (e: Exception) {
                            _uiState.value = _uiState.value.copy(
                                isLoadingImport = false,
                                errorMessage = "文件格式无效: ${e.message}"
                            )
                        }
                    }
                    ExportFileFormat.FileType.PLAIN_JSON -> {
                        // 明文JSON文件：直接解析
                        pendingImportJsonData = fileData.toString(Charsets.UTF_8)
                        processImportJson()
                    }
                    ExportFileFormat.FileType.UNKNOWN -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingImport = false,
                            errorMessage = "未知的文件格式"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingImport = false,
                    errorMessage = "读取文件失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 解密导入文件
     */
    fun decryptImportFile(password: String?) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoadingImport = true,
                    importPasswordError = null
                )

                val fileData = pendingImportFileData
                if (fileData == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoadingImport = false,
                        errorMessage = "无导入数据"
                    )
                    return@launch
                }

                val jsonData = importExportUseCase.decryptImportData(fileData, password)
                pendingImportJsonData = jsonData

                _uiState.value = _uiState.value.copy(
                    showImportPasswordDialog = false,
                    isLoadingImport = false
                )

                processImportJson()
            } catch (e: SecurityException) {
                _uiState.value = _uiState.value.copy(
                    isLoadingImport = false,
                    importPasswordError = "密码错误或文件损坏"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingImport = false,
                    showImportPasswordDialog = false,
                    errorMessage = "解密失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 处理导入的JSON数据
     */
    private fun processImportJson() {
        viewModelScope.launch {
            try {
                val jsonData = pendingImportJsonData
                if (jsonData == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "无导入数据"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isLoadingImport = true,
                    importStage = "正在解析数据"
                )

                val importData = importExportUseCase.parseImportJson(jsonData)

                _uiState.value = _uiState.value.copy(
                    isLoadingImport = true,
                    importStage = "正在检测冲突"
                )

                val preview = importExportUseCase.generateImportPreview(importData)

                _uiState.value = _uiState.value.copy(
                    isLoadingImport = false,
                    showImportPreview = true,
                    importPreview = ImportPreviewData(
                        identifierCount = preview.identifierCount,
                        accountCount = preview.accountCount,
                        bindingCount = preview.bindingCount,
                        applicationCount = preview.applicationCount,
                        hasConflicts = preview.conflicts.isNotEmpty(),
                        conflictCount = preview.conflicts.size
                    ),
                    pendingImportData = importData
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingImport = false,
                    errorMessage = "解析失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 执行导入
     */
    fun executeImport(mode: ImportMode) {
        viewModelScope.launch {
            try {
                val importData = _uiState.value.pendingImportData
                if (importData == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "无导入数据"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    showImportPreview = false,
                    showImportProgress = true,
                    importProgress = 0,
                    importStage = "正在导入数据",
                    importComplete = false
                )

                // Use the same ImportMode from domain directly
                val result = importExportUseCase.executeImport(importData, mode)

                _uiState.value = _uiState.value.copy(
                    importProgress = 100,
                    importStage = "导入完成",
                    importComplete = true,
                    importResult = ImportResultData(
                        addedCount = result.addedCount,
                        updatedCount = result.updatedCount,
                        skippedCount = result.skippedCount
                    )
                )

                // 清除暂存数据
                pendingImportFileData = null
                pendingImportJsonData = null

                // 刷新数据统计
                loadDataStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showImportProgress = false,
                    errorMessage = "导入失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 关闭导入密码对话框
     */
    fun dismissImportPasswordDialog() {
        _uiState.value = _uiState.value.copy(
            showImportPasswordDialog = false,
            importPasswordError = null
        )
        pendingImportFileData = null
    }

    /**
     * 关闭导入预览对话框
     */
    fun dismissImportPreview() {
        _uiState.value = _uiState.value.copy(
            showImportPreview = false,
            importPreview = null,
            pendingImportData = null
        )
        pendingImportJsonData = null
    }

    /**
     * 关闭导入进度对话框
     */
    fun dismissImportProgress() {
        _uiState.value = _uiState.value.copy(
            showImportProgress = false,
            importComplete = false,
            importResult = null
        )
    }

    /**
     * 显示清空数据确认对话框
     */
    fun showClearConfirm() {
        _uiState.value = _uiState.value.copy(showClearConfirm = true)
    }

    /**
     * 关闭清空数据确认对话框
     */
    fun dismissClearConfirm() {
        _uiState.value = _uiState.value.copy(showClearConfirm = false)
    }

    /**
     * 确认清空数据
     */
    fun confirmClearData() {
        viewModelScope.launch {
            try {
                bindingRepository.deleteAll()
                accountRepository.deleteAll()
                identifierRepository.deleteAll()

                loadDataStats()
                _uiState.value = _uiState.value.copy(showClearConfirm = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showClearConfirm = false,
                    errorMessage = "清空失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 估算数据库大小
     */
    private fun estimateDatabaseSize(identifierCount: Int, accountCount: Int): String {
        val estimatedBytes = (identifierCount * 500 + accountCount * 800).toLong()
        return when {
            estimatedBytes < 1024 -> "$estimatedBytes B"
            estimatedBytes < 1024 * 1024 -> "${estimatedBytes / 1024} KB"
            else -> "${estimatedBytes / (1024 * 1024)} MB"
        }
    }
}

/**
 * 导入导出UI状态
 */
data class ExportImportUiState(
    // 数据统计
    val identifierCount: Int = 0,
    val accountCount: Int = 0,
    val databaseSize: String = "0 KB",
    val isLoadingStats: Boolean = true,

    // 导出状态
    val showExportProgress: Boolean = false,
    val exportProgress: Int = 0,
    val exportStage: String = "",
    val exportComplete: Boolean = false,
    val exportFilePath: String? = null,

    // 导入状态
    val isLoadingImport: Boolean = false,
    val importFileName: String = "",
    val showImportPasswordDialog: Boolean = false,
    val importPasswordError: String? = null,
    val showImportPreview: Boolean = false,
    val importPreview: ImportPreviewData? = null,
    val pendingImportData: ImportData? = null,
    val showImportProgress: Boolean = false,
    val importProgress: Int = 0,
    val importStage: String = "",
    val importComplete: Boolean = false,
    val importResult: ImportResultData? = null,

    // 清空数据
    val showClearConfirm: Boolean = false,

    // 错误信息
    val errorMessage: String? = null
)