package cc.imorning.chat.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object PermissionUtils {

    private const val REQUEST_PERMISSION_CODE = 1000

    /**
     * Get all permission of this application
     */
    fun getPermissions(context: Context): Array<String> {
        val packageManager = context.packageManager
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0L)
            )
        } else {
            packageManager.getPackageInfo(context.packageName, 0)
        }
        return packageInfo.requestedPermissions
    }

    /**
     * Request permissions
     */
    fun requestPermission(context: Context, permissions: Array<String>) {
        val activity: Activity = context as Activity
        val deniedPermissions = mutableListOf<String>()
        for (permission in permissions) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission)
            }
        }
        if (deniedPermissions.isNotEmpty()) {
            activity.requestPermissions(deniedPermissions.toTypedArray(), REQUEST_PERMISSION_CODE)
        }
    }
}