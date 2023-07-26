package cc.imorning.chat.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.imorning.chat.App
import cc.imorning.common.BuildConfig
import cc.imorning.common.constant.Config
import cc.imorning.common.utils.NetworkUtils
import cc.imorning.common.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.sasl.SASLErrorException

class LoginViewModel : ViewModel() {

    private val connection = App.getTCPConnection()
    private lateinit var sessionManager: SessionManager
    private val account: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }
    private val token: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    private val shouldSavedState: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    private val shouldShowWaiting: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    private val showErrorDialog: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    private val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    private val needStartActivity: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    private val _showMoreMenu = MutableStateFlow(false)
    val showMoreMenu: MutableStateFlow<Boolean>
        get() = _showMoreMenu

    fun loadUser(context: Context) {
        sessionManager = SessionManager(context = context, sessionType = Config.LOGIN_INFO)
        if (sessionManager.fetchAccount() != null && sessionManager.fetchAuthToken() != null) {
            account.value = sessionManager.fetchAccount()
            token.value = sessionManager.fetchAuthToken()
        }
    }

    fun setAccount(value: String) {
        account.value = value
        token.value = ""
    }

    fun setToken(value: String) {
        token.value = value
    }

    fun setSaveState(value: Boolean) {
        shouldSavedState.value = value
    }

    fun closeDialog() {
        showErrorDialog.value = false
        shouldShowWaiting.value = false
    }

    fun getAccount(): LiveData<String> {
        return account
    }

    fun getToken(): LiveData<String> {
        return token
    }

    fun shouldSaveState(): LiveData<Boolean> {
        return shouldSavedState
    }

    fun needStartActivity(): LiveData<Boolean> {
        return needStartActivity
    }

    fun shouldShowWaitingDialog(): LiveData<Boolean> {
        return shouldShowWaiting
    }

    fun shouldShowErrorDialog(): LiveData<Boolean> {
        return showErrorDialog
    }

    fun getErrorMessage(): LiveData<String> {
        return errorMessage
    }

    fun login() {
        shouldShowWaiting.value = true
        val accountValue = account.value?.trim()
        val tokenValue = token.value?.trim()
        if (accountValue.isNullOrBlank() || tokenValue.isNullOrBlank()) {
            updateLoginStatus(needWaiting = false, isError = true, message = "账号或密码为空")
            return
        }
        if (NetworkUtils.isNetworkNotConnected()) {
            updateLoginStatus(needWaiting = false, isError = true, message = "网络未连接")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (!connection.isAuthenticated) {
                try {
                    if (!connection.isConnected) {
                        connection.connect()
                    }
                    connection.login(accountValue, tokenValue)
                    if (shouldSavedState.value == true) {
                        sessionManager.saveAccount(accountValue)
                        sessionManager.saveAuthToken(tokenValue)
                    }
                    updateLoginStatus(needWaiting = false, isError = false)
                } catch (e: SASLErrorException) {
                    updateLoginStatus(
                        needWaiting = false,
                        isError = true,
                        message = "账号或密码错误"
                    )
                } catch (e: SmackException.AlreadyLoggedInException) {
                    updateLoginStatus(needWaiting = false, isError = false)
                } catch (throwable: Throwable) {
                    updateLoginStatus(
                        needWaiting = false,
                        isError = true,
                        message = "unknown error"
                    )
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "login failed: ${throwable.localizedMessage}", throwable)
                    }
                }
            } else {
                updateLoginStatus(needWaiting = false, isError = false, message = "用户已在线")
            }
        }
    }

    private fun updateLoginStatus(needWaiting: Boolean, isError: Boolean, message: String = "") {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "update login status: $needWaiting $isError $message")
        }
        viewModelScope.launch(Dispatchers.Main) {
            shouldShowWaiting.value = needWaiting
            showErrorDialog.value = isError
            errorMessage.value = message
            if (!needWaiting && !isError) {
                needStartActivity.value = true
            }
        }
    }

    fun updateBottomSheetStatus() {
        viewModelScope.launch {
            _showMoreMenu.emit(!_showMoreMenu.value)
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}