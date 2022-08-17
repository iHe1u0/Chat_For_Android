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

//    val connectionLiveData = ConnectionLiveData(App.getContext())
//    connectionLiveData.observe(this, Observer { isConnected ->
//        isConnected?.let {
//            // do job
//        }
//    })


    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _messages = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf("Hello iMorning")
    }
    val messages: MutableLiveData<MutableList<String>> = _messages

    fun refresh() {
        // return if isRefreshing
        if (_isRefreshing.value) {
            return
        }
        viewModelScope.launch {
            _isRefreshing.emit(true)
            _messages.value = mutableListOf("${System.currentTimeMillis()}")
            delay(1000)
            _isRefreshing.emit(false)
        }
    }

}