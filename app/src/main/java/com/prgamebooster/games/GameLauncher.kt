package com.prgamebooster.games

import android.content.Context
import android.content.Intent
import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

sealed class LaunchResult {
    object Launched : LaunchResult()
    object NotInstalled : LaunchResult()
    object LaunchFailed : LaunchResult()
}

/**
 * اجرای واقعی بازی با PackageManager + Intent.
 * هرگز وانمود نمی‌کند بازی اجرا شده مگر اینکه Intent واقعاً ارسال شود.
 */
@Singleton
class GameLauncher @Inject constructor(
    private val context: Context
) {
    fun isInstalled(packageName: String): Boolean = try {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (exception: android.content.pm.PackageManager.NameNotFoundException) {
        false
    }

    fun launch(packageName: String): LaunchResult {
        if (!isInstalled(packageName)) return LaunchResult.NotInstalled

        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?: return LaunchResult.LaunchFailed

        return try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            LaunchResult.Launched
        } catch (exception: Exception) {
            LaunchResult.LaunchFailed
        }
    }

    fun openInStore(context: Context, packageName: String) {
        val uri = Uri.parse("market://details?id=$packageName")
        val playIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(playIntent)
        } catch (exception: android.content.ActivityNotFoundException) {
            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
