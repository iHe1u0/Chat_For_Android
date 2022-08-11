package com.imorning.common.utils

import android.content.Context
import android.content.SharedPreferences
import com.imorning.chat.App
import com.imorning.common.constant.Config

class SessionManager(
    sessionType: String? = Config.DEFAULT_CONFIG
) {

    private var preferences: SharedPreferences = App.getContext().getSharedPreferences(
        sessionType, Context.MODE_PRIVATE
    )


    companion object {
        private const val USER_NAME = "user_name"
        private const val USER_TOKEN = "user_token"
    }

    fun saveAccount(account: String) {
        val editor = preferences.edit()
        editor.putString(USER_NAME, account)
        editor.apply()
    }

    fun fetchAccount(): String? {
        return preferences.getString(USER_NAME, null)
    }

    fun saveAuthToken(token: String) {
        val editor = preferences.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return preferences.getString(USER_TOKEN, null)
    }

    fun logout() {
        val editor = preferences.edit()
        editor.remove(USER_NAME)
        editor.remove(USER_TOKEN)
        editor.apply()
    }

}