package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.IdentifierDetailState
import com.hermes.presentation.usecase.deactivation.DeactivationDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentifierDetailScreen(
    uiState: IdentifierDetailState,
    deactivationDetail: DeactivationDetail?,
    onBackClick: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    onAccountClick: (Long) -> Unit,
    onCancelDeactivation: (Long) -> Unit,
    onModifyDeactivation: () -> Unit,
    onScheduleDeactivation: () -> Unit,
    onBatchChange: () -> Unit,
    onMarkHandled: (Long) -> Unit,
    canDelete: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("影响范围", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(HermesColors.Background),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is IdentifierDetailState.Loading -> {
                    CircularProgressIndicator(color = HermesColors.Primary)
                }
                is IdentifierDetailState.Success -> {
                    Text("标识: ${uiState.detail.identifier.value}", color = HermesColors.TextPrimary)
                }
                is IdentifierDetailState.NotFound -> {
                    Text("标识不存在", color = HermesColors.TextMuted)
                }
                is IdentifierDetailState.Error -> {
                    Text(uiState.message, color = HermesColors.Danger)
                }
            }
        }
    }
}