package cc.imorning.chat.utils

import androidx.compose.ui.graphics.Color
import cc.imorning.common.CommonApp
import cc.imorning.common.R
import org.jivesoftware.smack.packet.Presence

class StatusHelper(mode: Presence.Mode) {

    private val context = CommonApp.getContext()

    private var status: String
    private var color: Color

    init {
        val statusArray = context.resources.getStringArray(R.array.mode)
        when (mode) {
            Presence.Mode.available -> {
                status = statusArray.first()
                color = Color.Green
            }
            Presence.Mode.chat -> {
                status = statusArray.first()
                color = Color.Green
            }
            Presence.Mode.away -> {
                status = statusArray[1]
                color = Color.Cyan
            }
            Presence.Mode.dnd -> {
                status = statusArray[2]
                color = Color.Blue
            }
            Presence.Mode.xa -> {
                status = statusArray[3]
                color = Color.Gray
            }
            else -> {
                status = statusArray.last()
                color = Color.Black
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

    companion object {
        fun getIndex(rosterStatus: Presence.Mode): Int {
            return when (rosterStatus) {
                Presence.Mode.available -> 0
                Presence.Mode.chat -> 0
                Presence.Mode.away -> 1
                Presence.Mode.dnd -> 2
                Presence.Mode.xa -> 3
            }
        }

        fun getMode(index: Int): Presence.Mode {
            return when (index) {
                0 -> Presence.Mode.available
                1 -> Presence.Mode.away
                2 -> Presence.Mode.dnd
                3 -> Presence.Mode.xa
                else -> Presence.Mode.xa
            }
        }
    }
}