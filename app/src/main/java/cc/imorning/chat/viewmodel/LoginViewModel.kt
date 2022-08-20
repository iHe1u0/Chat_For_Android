package cc.imorning.chat.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {


    private val _account = MutableStateFlow("")
    val account: StateFlow<String>
        get() = _account.asStateFlow()

}