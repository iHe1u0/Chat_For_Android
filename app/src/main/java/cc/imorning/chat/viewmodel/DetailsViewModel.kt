package cc.imorning.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.ui.state.DetailsScreenUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel : ViewModel() {
    suspend fun init() {
        if (_jid.value.isNotEmpty()) {
            _uiState.value = DetailsScreenUiState(_jid.value)
        }
    }

    fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            RosterAction.removeRoster(jid.value)
        }
    }

    private val connection = App.getTCPConnection()

    private val _jid = MutableStateFlow("")
    val jid: MutableStateFlow<String>
        get() = _jid

    private val _uiState = MutableStateFlow(DetailsScreenUiState(jid.value))
    val uiState: StateFlow<DetailsScreenUiState>
        get() = _uiState.asStateFlow()

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
