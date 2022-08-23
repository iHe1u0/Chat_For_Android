package cc.imorning.common.constant

enum class ResultCode {

    INIT,
    OK,
    WARNING,
    ERROR,

    /**
     * network in a wrong status
     */
    ERROR_NETWORK,

    /**
     * didn't support do it, maybe try it later
     */
    ERROR_NOT_SUPPORT_OPERATION,

    /**
     * error if something required is null
     */
    ERROR_EMPTY,

    /**
     * server didn't response after timeout
     */
    ERROR_NO_RESPONSE
}