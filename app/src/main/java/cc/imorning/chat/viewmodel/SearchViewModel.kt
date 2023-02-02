package cc.imorning.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.App
import cc.imorning.chat.action.SearchResult
import cc.imorning.chat.action.UserAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {

    private val connection = App.getTCPConnection()

    private val _key = MutableLiveData("")
    val key: LiveData<String>
        get() = _key

    private val _selectedUserJidString = MutableLiveData("")
    val selectedUserJidString: LiveData<String>
        get() = _selectedUserJidString

    private val _results = MutableLiveData<MutableList<SearchResult>?>()
    val result: LiveData<MutableList<SearchResult>?>
        get() = _results

    private val shouldShowWaiting: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    private val _shouldShowWaitingDialog = MutableLiveData(false)
    val shouldShowWaitingDialog: LiveData<Boolean>
        get() = _shouldShowWaitingDialog

    private val _shouldShowVCardDialog = MutableLiveData(false)
    val shouldShowVCardDialog: LiveData<Boolean>
        get() = _shouldShowVCardDialog

    fun setKey(key: String) {
        _key.value = key
    }

    /**
     * do search on server
     */
    fun search() {
        _shouldShowWaitingDialog.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val searchResults = UserAction.search(_key.value)
            if (searchResults != null) {
                withContext(Dispatchers.Main) {
                    _results.value = searchResults
                    _shouldShowWaitingDialog.value = false
                }
            }
        }
    }

    fun showVCard(user: SearchResult) {
        _selectedUserJidString.value = user.jid
        _shouldShowVCardDialog.value = true
    }

    fun closeVCard() {
        _shouldShowVCardDialog.value = false
    }

    companion object {
        private const val TAG = "SearchViewModel"
    }
}