package cc.imorning.chat.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.imorning.chat.ui.theme.ChatTheme

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen()
        }
    }

    companion object {
        private const val TAG = "SearchActivity"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SearchScreen() {
    ChatTheme {
        Scaffold(
            topBar = {},
            content = { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentScreen()
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContentScreen() {

    var key by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            TextField(
                value = key,
                onValueChange = { key = it.trim() },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                label = { Text(text = "账号、昵称") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "搜索好友",
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Center)
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(100) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Green.copy(0.1f))
                    .animateItemPlacement()
                    .clickable { }
                ) {
                    Icon(
                        imageVector = Icons.Filled.HeartBroken,
                        tint = Color.Red,
                        contentDescription = "",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = "想问你看过一张照片 那个女孩笑的很甜很甜 他们说你就住在青浦路的下面",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}