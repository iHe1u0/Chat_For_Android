package cc.imorning.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.imorning.common.CommonApp
import cc.imorning.common.action.ContactAction
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val connection = CommonApp.getTCPConnection()

    private val _key = MutableLiveData<String>("")
    val key: LiveData<String>
        get() = _key

    // private val results:MutableLiveData

    fun setKey(key: String) {
        _key.value = key
    }

    fun search() {
        viewModelScope.launch {
            ContactAction.search()
        }
    }

    companion object {
        private const val TAG = "SearchViewModel"
    }
}