package cc.imorning.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import cc.imorning.common.CommonApp

object NetworkUtils {

    fun isNetworkConnected(context: Context = CommonApp.getContext()): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }

    fun isNetworkNotConnected(context: Context = CommonApp.getContext()): Boolean {
        return !isNetworkConnected(context)
    }

}