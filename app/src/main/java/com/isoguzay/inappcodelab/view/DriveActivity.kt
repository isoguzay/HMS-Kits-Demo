package com.isoguzay.inappcodelab.view

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.huawei.agconnect.datastore.core.SharedPrefUtil.init

import com.isoguzay.inappcodelab.R
import kotlinx.android.synthetic.main.activity_drive.*
import com.huawei.cloud.base.auth.DriveCredential
import com.huawei.cloud.base.util.StringUtils
import com.huawei.cloud.client.exception.DriveCode
import com.huawei.cloud.services.drive.DriveScopes
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import java.util.HashMap


class DriveActivity : AppCompatActivity() {

    private var mCredential: DriveCredential? = null
    private var accessToken: String? = null
    private var unionId: String? = null

    companion object {
        private val MIME_TYPE_MAP: MutableMap<String, String> = HashMap()
        private const val REQUEST_SIGN_IN_LOGIN = 1002
        private const val TAG = "MainActivity"

        init {
            MIME_TYPE_MAP.apply {
                put(".doc", "application/msword")
                put(".jpg", "image/jpeg")
                put(".mp3", "audio/x-mpeg")
                put(".mp4", "video/mp4")
                put(".pdf", "application/pdf")
                put(".png", "image/png")
                put(".txt", "text/plain")
            }
        }
    }

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private val refreshAT = DriveCredential.AccessMethod {
        /**
         * Simplified code snippet for demonstration purposes. For the complete code snippet,
         * please go to Client Development > Obtaining Authentication Information > Store Authentication Information
         * in the HUAWEI Drive Kit Development Guide.
         **/
        return@AccessMethod accessToken
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS_STORAGE, 1)
        }

        button_drivekit_login.setOnClickListener {
            driveLogin()
        }

    }

    private fun driveLogin() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val scopeList: MutableList<Scope> = ArrayList()
        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE)) // All permissions, except permissions for the app folder.
        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_READONLY)) // Permissions to view file content and metadata.
        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_FILE)) // Permissions to view and manage files.
        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_METADATA)) // Permissions to view and manage file metadata, excluding file content.
        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_METADATA_READONLY)) // Permissions to view file metadata, excluding file content.
        scopeList.add(Scope(DriveScopes.SCOPE_DRIVE_APPDATA)) // Permissions to upload and store app data.
        scopeList.add(HuaweiIdAuthAPIManager.HUAWEIID_BASE_SCOPE) // Basic account permissions.
        val authParams = HuaweiIdAuthParamsHelper(DEFAULT_AUTH_REQUEST_PARAM)
            .setAccessToken()
            .setIdToken()
            .setScopeList(scopeList)
            .createParams()
        // Call the account API to get account information.
        val client = HuaweiIdAuthManager.getService(this, authParams)
        startActivityForResult(client.signInIntent, REQUEST_SIGN_IN_LOGIN)
    }

    // Exceptional process for obtaining account information. Obtain and save the related accessToken and unionID using this function.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "onActivityResult, requestCode = $requestCode, resultCode = $resultCode")
        when (requestCode) {
            REQUEST_SIGN_IN_LOGIN -> {
                val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
                if (authHuaweiIdTask.isSuccessful) {
                    val huaweiAccount = authHuaweiIdTask.result
                    accessToken = huaweiAccount.accessToken
                    unionId = huaweiAccount.unionId
                    val returnCode = init(unionId, accessToken, refreshAT)
                    if (DriveCode.SUCCESS == returnCode) {
                        showTips("login ok")
                        val goDriveKit = Intent(this, DriveKitActivity::class.java)
                        goDriveKit.putExtra("accessToken", accessToken)
                        goDriveKit.putExtra("unionId", unionId)
                        startActivity(goDriveKit)
                    } else if (DriveCode.SERVICE_URL_NOT_ENABLED == returnCode) {
                        showTips("drive is not enabled")
                    } else {
                        showTips("login error")
                    }
                } else {
                    Log.d(
                        TAG,
                        "onActivityResult, signIn failed: " + (authHuaweiIdTask.exception as ApiException).statusCode
                    )
                    Toast.makeText(
                        applicationContext,
                        "onActivityResult, signIn failed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun init(unionID: String?, at: String?, refreshAT: DriveCredential.AccessMethod?): Int {
        return if (StringUtils.isNullOrEmpty(unionID) || StringUtils.isNullOrEmpty(at)) {
            DriveCode.ERROR
        } else {
            val builder = DriveCredential.Builder(unionID, refreshAT)
            mCredential = builder.build().setAccessToken(at)
            DriveCode.SUCCESS
        }
    }

    private fun showTips(toastText: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, toastText, Toast.LENGTH_LONG).show()
            textView_drivekit.text = toastText
        }
    }




}