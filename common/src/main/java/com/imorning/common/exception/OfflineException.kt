package com.imorning.common.exception

class OfflineException(private val msg: String) : IllegalStateException(msg)