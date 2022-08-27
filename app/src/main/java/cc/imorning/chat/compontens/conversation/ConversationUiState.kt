/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.imorning.chat.compontens.conversation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList
import cc.imorning.chat.R
import cc.imorning.common.entity.MessageEntity

class ConversationUiState(
    val channelName: String,
    val channelMembers: Int,
    initialMessages: List<MessageEntity>
) {
    private val _messages: MutableList<MessageEntity> = initialMessages.toMutableStateList()
    val messages: List<MessageEntity> = _messages

    fun addMessage(msg: MessageEntity) {
        _messages.add(0, msg) // Add to the beginning of the list
    }
}

@Immutable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Int? = null,
    val authorImage: Int = if (author == "me") R.drawable.ic_default_avatar else R.drawable.ic_nick
)
