package cc.imorning.chat.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.utils.AvatarUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailsViewModel : ViewModel() {
    suspend fun init() {
        if (_jid.value.isNotEmpty()) {
            _uiState.value = DetailsScreenState(_jid.value)
        }
    }

    private val connection = App.getTCPConnection()

    private val _jid = MutableStateFlow("")
    val jid: MutableStateFlow<String>
        get() = _jid

    private val _uiState = MutableStateFlow(DetailsScreenState(jid.value))
    val uiState: StateFlow<DetailsScreenState> = _uiState.asStateFlow()

}

class DetailsViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Immutable
data class DetailsScreenState(val jid: String) {
    fun isMe() = jid == App.getTCPConnection().user.asEntityBareJidString()

    fun avatar() = AvatarUtils.instance.getAvatarPath(jid)

    fun nickName() = RosterAction.getNickName(jid)

    fun status() = RosterAction.getRosterStatus(jid)

    fun template1() {

    }
}