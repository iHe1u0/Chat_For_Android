package cc.imorning.common.constant

import cc.imorning.common.BuildConfig

/**
 * This class saved configure of chat sever,modify them for your own server
 */
object ServerConfig {

    /**
     * Host name, typically is domain of server
     */
    const val HOST_NAME = BuildConfig.host_name

    /**
     * Same as [HOST_NAME]
     */
    const val DOMAIN = BuildConfig.domain

    /**
     * Resource of client, different servers have different resources
     */
    const val RESOURCE = BuildConfig.resource

    /**
     * Login port, default is 5222
     */
    const val LOGIN_PORT = BuildConfig.port
}