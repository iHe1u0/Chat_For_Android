package cc.imorning.chat.compontens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.imorning.chat.view.ui.ComposeDialogUtils

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    var showBuildingDialog by remember { mutableStateOf(false) }
    if (showBuildingDialog) {
        ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.onBackground.copy(0.08f),
                shape = CircleShape
            )
            .clickable {
                showBuildingDialog = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "搜索",
            modifier = Modifier.padding(start = 16.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "搜索",
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}