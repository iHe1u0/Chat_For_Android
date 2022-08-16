package cc.imorning.common.constant

object StatusCode {
    const val OK = 0
    const val WARNING = -1
    const val ERROR = -100

    // 已经在线
    const val LOGIN_FAILED_CAUSE_ONLINE = -101

    // 账号密码错误
    const val LOGIN_AUTH_FAILED = -102

    // 网络无连接
    const val NETWORK_ERROR = -103
}