package cc.imorning.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.imorning.common.CommonApp
import cc.imorning.common.action.SearchResult
import cc.imorning.common.action.UserAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchViewModel : ViewModel() {

    private val connection = CommonApp.getTCPConnection()

    private val _key = MutableLiveData("")
    val key: LiveData<String>
        get() = _key

    private val _results = MutableLiveData<MutableList<SearchResult>?>()
    val result: LiveData<MutableList<SearchResult>?>
        get() = _results

    private val shouldShowWaiting: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    private val _shouldShowWaitingDialog = MutableLiveData(false)
    val shouldShowWaitingDialog: LiveData<Boolean>
        get() = _shouldShowWaitingDialog

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

    companion object {
        private const val TAG = "SearchViewModel"
    }
}