package cc.imorning.chat.compontens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.imorning.chat.R
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

private const val TAG = "Avatar"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Avatar(avatarPath: String, onClick: () -> Unit = {}) {
    SubcomposeAsyncImage(
        model = avatarPath,
        contentDescription = stringResource(id = R.string.desc_contact_item_avatar),
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .combinedClickable(
                onClick = onClick
            ),
        alignment = Alignment.Center,
        filterQuality = FilterQuality.High
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator()
            }
            is AsyncImagePainter.State.Error -> {
                Icon(imageVector = Icons.Filled.Person, contentDescription = null)
            }
            is AsyncImagePainter.State.Empty -> {
                Icon(imageVector = Icons.Filled.Person, contentDescription = null)
            }
            is AsyncImagePainter.State.Success -> {
                SubcomposeAsyncImageContent()
            }
            else -> {
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun GlideAvatar(avatarPath: String, onClick: () -> Unit) {
    GlideImage(
        model = avatarPath,
        contentDescription = stringResource(id = R.string.desc_contact_item_avatar),
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .combinedClickable(
                onClick = onClick
            ),
        alignment = Alignment.Center
    )
}
