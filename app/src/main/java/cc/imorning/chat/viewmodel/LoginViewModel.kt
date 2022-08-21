package cc.imorning.chat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.Config
import cc.imorning.common.utils.NetworkUtils
import cc.imorning.common.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.sasl.SASLErrorException
import org.joda.time.DateTime

class LoginViewModel : ViewModel() {

    private val connection = CommonApp.getTCPConnection()
    private val sessionManager = SessionManager(Config.LOGIN_INFO)
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

    private fun loadUser() {
        if (sessionManager.fetchAccount() != null && sessionManager.fetchAuthToken() != null) {
            account.value = sessionManager.fetchAccount()
            token.value = sessionManager.fetchAuthToken()
        }
    }

    init {
        loadUser()
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
            updateLoginStatus(isNeedWaiting = false, isError = true, message = "账号或密码为空")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (NetworkUtils.isNetworkNotConnected()) {
                updateLoginStatus(isNeedWaiting = false, isError = true, message = "网络未连接")
                return@launch
            }
            if (!connection.isConnected) {
                connection.connect()
            }
            if (!connection.isAuthenticated) {
                try {
                    connection.login(accountValue, tokenValue)
                    updateLoginStatus(isNeedWaiting = false, isError = false)
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "login success @ ${DateTime.now()}")
                    }
                    withContext(Dispatchers.Main) { needStartActivity.value = true }
                } catch (e: SASLErrorException) {
                    updateLoginStatus(isNeedWaiting = false, isError = true, message = "账号或密码错误")
                    if (shouldSavedState.value == true) {
                        sessionManager.saveAccount(accountValue)
                        sessionManager.saveAuthToken(tokenValue)
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "auth failed [$accountValue,$tokenValue]")
                    }
                } catch (e: SmackException.AlreadyLoggedInException) {
                    updateLoginStatus(isNeedWaiting = false, isError = false)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "$accountValue already online")
                    }
                } catch (throwable: Throwable) {
                    updateLoginStatus(isNeedWaiting = false, isError = true, message = "未知错误")
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "login failed: ${throwable.localizedMessage}", throwable)
                    }
                }
            } else {
                updateLoginStatus(isNeedWaiting = false, isError = false, message = "用户已在线")
            }
        }
    }

    private fun updateLoginStatus(isNeedWaiting: Boolean, isError: Boolean, message: String = "") {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "updateLoginStatus: $isNeedWaiting $isError $message")
        }
        MainScope().launch(Dispatchers.Main) {
            shouldShowWaiting.value = isNeedWaiting
            showErrorDialog.value = isError
            errorMessage.value = message
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}