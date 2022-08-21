package cc.imorning.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.imorning.common.action.LoginAction
import cc.imorning.common.constant.Config
import cc.imorning.common.constant.StatusCode
import cc.imorning.common.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val sessionManager = SessionManager(Config.LOGIN_INFO)
    private val account: MutableLiveData<String> by lazy {
        MutableLiveData("") //.also { loadUser() }
    }
    private val token: MutableLiveData<String> by lazy {
        MutableLiveData<String>("") //.also { token.value = "" }
    }
    private val isSaveChecked: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    private val loginCode: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(StatusCode.INIT)
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

    fun setChecked(value: Boolean) {
        isSaveChecked.value = value
    }

    fun setStatus(statusCode: Int = StatusCode.INIT) {
        loginCode.value = statusCode
    }

    fun getAccount(): LiveData<String> {
        return account
    }

    fun getToken(): LiveData<String> {
        return token
    }

    fun getChecked(): LiveData<Boolean> {
        return isSaveChecked
    }

    fun getLoginStatus(): MutableLiveData<Int> {
        return loginCode
    }

    fun login() {
        val accountValue = account.value
        val tokenValue = token.value
        if (accountValue.isNullOrBlank() || tokenValue.isNullOrBlank()) {
            loginCode.value = StatusCode.LoginCode.ACCOUNT_OR_TOKEN_IS_NULL
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            when (LoginAction.run(accountValue, tokenValue)) {
                StatusCode.OK -> {
                    if (isSaveChecked.value!!) {
                        sessionManager.saveAccount(accountValue)
                        sessionManager.saveAuthToken(tokenValue)
                    }
                    withContext(Dispatchers.Main) {
                        loginCode.value = StatusCode.OK
                    }
                }
                StatusCode.LoginCode.LOGIN_AUTH_FAILED -> {
                    withContext(Dispatchers.Main) {
                        loginCode.value = StatusCode.LoginCode.LOGIN_AUTH_FAILED
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        loginCode.value = StatusCode.ERROR
                    }
                }
            }
        }
    }
}