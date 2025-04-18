package com.galib.kaizokuani

// from https://www.apps2sd.info/idmp/Util1DM.kt
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File

object Util1DM {
    private const val PACKAGE_NAME_1DM_PLUS = "idm.internet.download.manager.plus"
    private const val PACKAGE_NAME_1DM_NORMAL = "idm.internet.download.manager"
    private const val PACKAGE_NAME_1DM_LITE = "idm.internet.download.manager.adm.lite"
    private const val DOWNLOADER_ACTIVITY_NAME_1DM = "idm.internet.download.manager.Downloader"
    private const val SECURE_URI_1DM_SUPPORT_MIN_VERSION_CODE = 169
    private const val HEADERS_AND_MULTIPLE_LINKS_1DM_SUPPORT_MIN_VERSION_CODE = 157
    private const val GOOGLE_PLAY_STORE_SCHEMA = "market://details?id="
    private const val HUAWEI_APP_GALLERY_SCHEMA = "appmarket://details?id="
    private const val GOOGLE_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id="
    private const val EXTRA_SECURE_URI = "secure_uri"
    private const val EXTRA_COOKIES = "extra_cookies"
    private const val EXTRA_USERAGENT = "extra_useragent"
    private const val EXTRA_REFERER = "extra_referer"
    private const val EXTRA_HEADERS = "extra_headers"
    private const val EXTRA_FILENAME = "extra_filename"
    private const val EXTRA_URL_LIST = "url_list"
    private const val EXTRA_URL_FILENAME_LIST = "url_list.filename"
    private const val MESSAGE_INSTALL_1DM = "To download content install 1DM"
    private const val MESSAGE_UPDATE_1DM = "To download content update 1DM"

    /*
        Note 1) If you want to download a torrent file from local storage, add the below file provider in the Manifest file
             <provider
                android:name="androidx.core.content.FileProvider"
                android:authorities="${applicationId}.provider"
                android:exported="false"
                android:grantUriPermissions="true">
                <meta-data
                    android:name="android.support.FILE_PROVIDER_PATHS"
                    android:resource="@xml/provider_paths" />
            </provider>

            Then create provider_paths file in the xml folder with below content
            <?xml version="1.0" encoding="utf-8"?>
            <paths>
                <external-path
                    name="external_files"
                    path="." />
                <root-path
                    name="sdcard_files"
                    path="/storage/" />
                <files-path
                    name="files"
                    path="." />
                <cache-path
                    name="cache"
                    path="." />
            </paths>

        Note 2) If you're targeting Android 11+ (targetSdkVersion is 30+) then add below queries in the manifest file. More details on https://developer.android.com/training/package-visibility
            <queries>
                <package android:name="idm.internet.download.manager" />
                <package android:name="idm.internet.download.manager.adm.lite" />
                <package android:name="idm.internet.download.manager.plus" />
            </queries>

            Or if your app is not on Play store then add below permission in the Manifest file
                <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
     */

    enum class AppState { OK, UPDATE_REQUIRED, NOT_INSTALLED }

    @Throws(Exception::class)
    fun downloadTorrent(activity: Activity, torrentUrl: String, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, null, torrentUrl, null, null, null, null, null, false, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadTorrent(activity: Activity, torrentUrl: String, headers: Map<String, String>?, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, null, torrentUrl, null, null, null, null, headers, false, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadMagnet(activity: Activity, magnetUrl: String, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, null, magnetUrl, null, null, null, null, null, false, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadFile(activity: Activity, url: String, secureUri: Boolean, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, null, url, null, null, null, null, null, secureUri, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadFile(activity: Activity, url: String, headers: Map<String, String>?, secureUri: Boolean, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, null, url, null, null, null, null, headers, secureUri, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadFile(activity: Activity, url: String, referer: String?, fileName: String?, userAgent: String?, cookies: String?, secureUri: Boolean, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, null, url, referer, fileName, userAgent, cookies, null, secureUri, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadFile(activity: Activity, url: String, referer: String?, fileName: String?, userAgent: String?, cookies: String?, headers: Map<String, String>?, secureUri: Boolean, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, null, url, referer, fileName, userAgent, cookies, headers, secureUri, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadFiles(activity: Activity, urlAndFileNames: Map<String, String>, secureUri: Boolean, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, urlAndFileNames, null, null, null, null, null, null, secureUri, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadFiles(activity: Activity, urlAndFileNames: Map<String, String>, headers: Map<String, String>?, secureUri: Boolean, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadFilesInternal(activity, urlAndFileNames, null, null, null, null, null, headers, secureUri, askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadTorrentFile(activity: Activity, torrent: File, askUserToInstall1DMIfNotInstalled: Boolean) {
        downloadTorrentFile(activity, torrent, activity.packageName + ".provider", askUserToInstall1DMIfNotInstalled)
    }

    @Throws(Exception::class)
    fun downloadTorrentFile(activity: Activity, torrentFile: File, authority: String, askUserToInstall1DMIfNotInstalled: Boolean) {
        val packageName = get1DMInstalledPackageName(activity, 0, askUserToInstall1DMIfNotInstalled)
        if (TextUtils.isEmpty(packageName)) {
            return
        }
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(activity, authority, torrentFile)
        } else {
            Uri.fromFile(torrentFile)
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.component = ComponentName(packageName.toString(), DOWNLOADER_ACTIVITY_NAME_1DM)
        intent.data = uri
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(intent)
    }

    private fun downloadFilesInternal(
        activity: Activity, urlAndFileNames: Map<String, String>?, url: String?, referer: String?,
        fileName: String?, userAgent: String?, cookies: String?, headers: Map<String, String>?,
        secureUri: Boolean, askUserToInstall1DMIfNotInstalled: Boolean
    ) {
        val requiredVersionCode = if (secureUri) SECURE_URI_1DM_SUPPORT_MIN_VERSION_CODE else if (!isEmpty(urlAndFileNames) || !isEmpty(headers)) HEADERS_AND_MULTIPLE_LINKS_1DM_SUPPORT_MIN_VERSION_CODE else 0
        val packageName = get1DMInstalledPackageName(activity, requiredVersionCode, askUserToInstall1DMIfNotInstalled)
        if (TextUtils.isEmpty(packageName)) {
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.component = ComponentName(packageName.toString(), DOWNLOADER_ACTIVITY_NAME_1DM)
        intent.putExtra(EXTRA_SECURE_URI, secureUri)
        if (isEmpty(urlAndFileNames)) {
            intent.data = url?.toUri()
            if (!TextUtils.isEmpty(referer)) intent.putExtra(EXTRA_REFERER, referer)
            if (!TextUtils.isEmpty(userAgent)) intent.putExtra(EXTRA_USERAGENT, userAgent)
            if (!TextUtils.isEmpty(cookies)) intent.putExtra(EXTRA_COOKIES, cookies)
            if (!TextUtils.isEmpty(fileName)) intent.putExtra(EXTRA_FILENAME, fileName)
        } else {
            val urls: ArrayList<String> = ArrayList(urlAndFileNames!!.size)
            val names: ArrayList<String> = ArrayList(urlAndFileNames.size)
            for ((key, value) in urlAndFileNames) {
                if (!TextUtils.isEmpty(key)) {
                    urls.add(key)
                    names.add(value)
                }
            }
            if (urls.size > 0) {
                intent.putExtra(EXTRA_URL_LIST, urls)
                intent.putExtra(EXTRA_URL_FILENAME_LIST, names)
                intent.data = urls[0].toUri()
            }
        }
        if (!isEmpty(headers)) {
            val extra = Bundle()
            if (headers != null) {
                for ((key, value) in headers) extra.putString(key, value)
            }
            intent.putExtra(EXTRA_HEADERS, extra)
        }
        activity.startActivity(intent)
    }

    private fun install1DM(activity: Activity, packageName: String, update: Boolean) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(if (update) MESSAGE_UPDATE_1DM else MESSAGE_INSTALL_1DM)
            .setPositiveButton(if (update) "Update" else "Install") { _, _ ->
                try {
                    try {
                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_SCHEMA + packageName + getStoreTracking(activity))))
                    } catch (e: ActivityNotFoundException) {
                        try {
                            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(HUAWEI_APP_GALLERY_SCHEMA + packageName + getStoreTracking(activity))))
                        } catch (e1: ActivityNotFoundException) {
                            try {
                                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_URL + packageName + getStoreTracking(activity))))
                            } catch (e2: ActivityNotFoundException) {
                                Toast.makeText(activity, e2.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
            }
        builder.show()
    }

    private fun <S, T> isEmpty(map: Map<S, T>?): Boolean {
        return map.isNullOrEmpty()
    }

    private fun getStoreTracking(context: Context): String {
        return "&referrer=utm_source%3D" + context.packageName + "%26utm_medium%3DApp%26utm_campaign%3DDownload"
    }

    @Throws(Exception::class)
    private fun get1DMInstalledPackageName(activity: Activity, requiredVersionCode: Int, askUserToInstall1DMIfNotInstalled: Boolean): String? {
        val packageManager: PackageManager = activity.packageManager
        var packageName = PACKAGE_NAME_1DM_PLUS
        var state = get1DMAppState(packageManager, packageName, requiredVersionCode)
        if (state == AppState.NOT_INSTALLED) {
            packageName = PACKAGE_NAME_1DM_NORMAL
            state = get1DMAppState(packageManager, packageName, requiredVersionCode)
            if (state == AppState.NOT_INSTALLED) {
                packageName = PACKAGE_NAME_1DM_LITE
                state = get1DMAppState(packageManager, packageName, requiredVersionCode)
                if (state == AppState.NOT_INSTALLED) {
                    if (askUserToInstall1DMIfNotInstalled) {
                        install1DM(activity, PACKAGE_NAME_1DM_NORMAL, false)
                        return null
                    } else {
                        throw Exception(MESSAGE_INSTALL_1DM)
                    }
                }
            }
        }
        if (state == AppState.UPDATE_REQUIRED) {
            if (askUserToInstall1DMIfNotInstalled) {
                install1DM(activity, packageName, true)
                return null
            } else {
                throw Exception(MESSAGE_UPDATE_1DM)
            }
        }
        return packageName
    }

    private fun get1DMAppState(packageManager: PackageManager, packageName: String, requiredVersion: Int): AppState {
        return try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            if (requiredVersion <= 0 || packageInfo.longVersionCode >= requiredVersion) {
                AppState.OK
            } else {
                AppState.UPDATE_REQUIRED
            }
        } catch (ignore: PackageManager.NameNotFoundException) {
            AppState.NOT_INSTALLED
        }
    }
}