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

package cc.imorning.chat.data

import cc.imorning.common.entity.MessageBody
import cc.imorning.common.entity.MessageEntity

val initialMessages = listOf(
    MessageEntity(
        sender = "admin@chat.catcompany.cn",
        receiver = "imorning@chat.catcompany.cn",
        messageBody = MessageBody(text = "谁爱孤单、谁再平淡、昨夜的抱怨会让谁不安"),
    ),
    MessageEntity(
        sender = "admin@chat.catcompany.cn",
        receiver = "imorning@chat.catcompany.cn",
        messageBody = MessageBody(text = "谁想孤单、谁再平凡、明天的祝愿会不会再留下遗憾"),
    ),
    MessageEntity(
        sender = "admin@chat.catcompany.cn",
        receiver = "imorning@chat.catcompany.cn",
        messageBody = MessageBody(text = "是谁把我丢在路旁都没有留下一句话"),
    ),
    MessageEntity(
        sender = "admin@chat.catcompany.cn",
        receiver = "imorning@chat.catcompany.cn",
        messageBody = MessageBody(text = "我向前看有些凄凉是否该留下一丝幻想"),
    )
)
