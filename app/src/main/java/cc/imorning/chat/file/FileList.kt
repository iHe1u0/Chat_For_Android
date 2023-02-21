package cc.imorning.chat.file

import androidx.compose.runtime.Immutable

@Immutable
data class Image(
    val id: Long,
    val uri: String,
    val name: String,
    val size: Int
)