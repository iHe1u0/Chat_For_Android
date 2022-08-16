package cc.imorning.chat.activity.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _messages = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf("Hello iMorning")
    }
    val messages: MutableLiveData<MutableList<String>> = _messages

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is message Fragment"
//    }
//    val text: LiveData<String> = _text

    fun refresh() {
        // This doesn't handle multiple 'refreshing' tasks, don't use this
        viewModelScope.launch {
            _isRefreshing.emit(true)
            _messages.value = mutableListOf("${System.currentTimeMillis()}")
            delay(1000)
            _isRefreshing.emit(false)
        }
    }

}