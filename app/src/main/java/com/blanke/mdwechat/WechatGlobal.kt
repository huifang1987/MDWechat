package com.blanke.mdwechat

import android.content.Context
import com.blanke.mdwechat.config.WxVersionConfig
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object WechatGlobal {
    @Volatile
    var wxVersion: String = ""
    @Volatile
    var wxPackageName: String = ""
    @Volatile
    lateinit var wxLoader: ClassLoader
    lateinit var wxClasses: List<String>
    lateinit var wxVersionConfig: WxVersionConfig

    @JvmStatic
    fun init(lpparam: XC_LoadPackage.LoadPackageParam) {
        wxPackageName = lpparam.packageName
        val context = XposedHelpers.callMethod(
                XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null),
                        "currentActivityThread"), "getSystemContext") as Context
        wxVersion = context.packageManager.getPackageInfo(wxPackageName, 0)?.versionName ?: ""
        wxLoader = lpparam.classLoader
    }

    fun <T> wxLazy(initializer: () -> T?): Lazy<T> {
        return wxLazy("", initializer)
    }

    fun <T> wxLazy(name: String, initializer: () -> T?): Lazy<T> {
        return lazy(LazyThreadSafetyMode.PUBLICATION) {
            initializer() ?: throw Error("Failed to evaluate $name")
        }
    }
}