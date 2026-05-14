package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.component.IdentifierCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.identifier.IdentifierListItem
import com.hermes.presentation.viewmodel.IdentifierListState

/**
 * 标识列表页面（支持卡片手势交互）
 */
@Composable
fun IdentifierListScreen(
    uiState: IdentifierListState,
    onIdentifierClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditIdentifier: (Long) -> Unit = {},
    onSetReminder: (Long) -> Unit = {},
    onModifyReminder: (Long) -> Unit = {},
    onCancelReminder: (Long) -> Unit = {},
    onMarkDeactivated: (Long) -> Unit = {},
    onDeleteIdentifier: (Long) -> Unit = {},
    onMarkHandled: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HermesColors.Background)
    ) {
        when (uiState) {
            is IdentifierListState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = HermesColors.Primary)
                }
            }
            is IdentifierListState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.items) { item ->
                        val identifierId = item.identifier.id
                        if (identifierId != null) {
                            IdentifierCard(
                                item = item,
                                onClick = { onIdentifierClick(identifierId) },
                                onEdit = { onEditIdentifier(identifierId) },
                                onSetReminder = { onSetReminder(identifierId) },
                                onModifyReminder = { onModifyReminder(identifierId) },
                                onCancelReminder = { onCancelReminder(identifierId) },
                                onMarkDeactivated = { onMarkDeactivated(identifierId) },
                                onDelete = { onDeleteIdentifier(identifierId) },
                                onMarkHandled = { onMarkHandled(identifierId) }
                            )
                        }
                    }
                }
            }
            is IdentifierListState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.message, color = HermesColors.Danger)
                }
            }
        }
    }
}