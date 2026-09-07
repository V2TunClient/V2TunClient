package com.aras.client.ui.checkupdate

import android.app.Application
import com.aras.client.AppConfig
import com.aras.client.R
import com.aras.client.dto.CheckUpdateResult
import com.aras.client.dto.UrlContentRequest
import com.aras.client.util.HttpUtil
import java.io.File
import com.aras.client.handler.MmkvManager
import com.aras.client.handler.SettingsManager
import com.aras.client.handler.UpdateCheckerManager
import com.aras.client.ui.base.BaseViewModel
import com.aras.client.util.LogUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CheckUpdateViewModel(application: Application) : BaseViewModel(application) {

    private val _checkPreRelease = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_CHECK_UPDATE_PRE_RELEASE, false)
    )
    val checkPreRelease: StateFlow<Boolean> = _checkPreRelease.asStateFlow()

    private val _updateResult = MutableStateFlow<CheckUpdateResult?>(null)
    val updateResult: StateFlow<CheckUpdateResult?> = _updateResult.asStateFlow()

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    private val _downloadedApk = MutableStateFlow<File?>(null)
    val downloadedApk: StateFlow<File?> = _downloadedApk.asStateFlow()

    fun toggleCheckPreRelease(enabled: Boolean) {
        _checkPreRelease.value = enabled
        MmkvManager.encodeSettings(AppConfig.PREF_CHECK_UPDATE_PRE_RELEASE, enabled)
    }

    fun checkForUpdates() {
        launchLoading {
            toast(R.string.update_checking_for_update)
            try {
                val result = UpdateCheckerManager.checkForUpdate(_checkPreRelease.value)
                if (result.hasUpdate) {
                    _updateResult.value = result
                    _showUpdateDialog.value = true
                } else {
                    toastSuccess(R.string.update_already_latest_version)
                }
            } catch (e: Exception) {
                LogUtil.e(AppConfig.TAG, "Failed to check for updates", e)
                toastError(R.string.toast_failure)
            }
        }
    }

    fun dismissUpdateDialog() {
        _showUpdateDialog.value = false
    }

    fun downloadAndInstallUpdate() {
        val result = _updateResult.value ?: return
        val url = result.downloadUrl ?: run {
            toastError(R.string.toast_failure)
            return
        }

        launchLoading {
            try {
                toast(R.string.update_downloading)
                val safeVersion = (result.latestVersion ?: "update")
                    .replace(Regex("[^A-Za-z0-9._-]"), "_")
                val file = File(app.cacheDir, "V2TunClient-$safeVersion.apk")
                if (file.exists()) file.delete()

                val request = UrlContentRequest(
                    url = url,
                    timeout = 60_000,
                    httpPort = SettingsManager.getHttpPort(),
                    proxyUsername = SettingsManager.getSocksUsername(),
                    proxyPassword = SettingsManager.getSocksPassword()
                )
                val downloaded = HttpUtil.downloadToFile(request, file)
                if (!downloaded || !file.exists() || file.length() == 0L) {
                    file.delete()
                    throw IllegalStateException("APK download failed")
                }

                _showUpdateDialog.value = false
                _downloadedApk.value = file
            } catch (e: Exception) {
                LogUtil.e(AppConfig.TAG, "Failed to download update", e)
                toastError(R.string.update_download_failed)
            }
        }
    }

    fun consumeDownloadedApk() {
        _downloadedApk.value = null
    }
}
