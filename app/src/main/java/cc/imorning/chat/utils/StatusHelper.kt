package cc.imorning.chat.utils

import androidx.compose.ui.graphics.Color
import org.jivesoftware.smack.packet.Presence

class StatusHelper(mode: Presence.Mode) {

    private var status: String
    private var color: Color

    init {
        when (mode) {
            Presence.Mode.available -> {
                status = "在线"
                color = Color.Green
            }
            Presence.Mode.chat -> {
                status = "在线"
                color = Color.Green
            }
            Presence.Mode.away -> {
                status = "离开"
                color = Color.Cyan
            }
            Presence.Mode.dnd -> {
                status = "勿扰"
                color = Color.Blue
            }
            Presence.Mode.xa -> {
                status = "离线"
                color = Color.Gray
            }
            else -> {
                status = "未知"
                color = Color.Red
            }
        }
    }

    fun getStatusColor(): Color {
        return color
    }

    override fun toString(): String {
        super.toString()
        return status
    }
}