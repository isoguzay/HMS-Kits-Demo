package com.isoguzay.inappcodelab.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.huawei.cloud.base.auth.DriveCredential
import com.huawei.cloud.base.http.FileContent
import com.huawei.cloud.base.util.StringUtils
import com.huawei.cloud.client.exception.DriveCode
import com.huawei.cloud.services.drive.Drive
import com.huawei.cloud.services.drive.model.File
import com.huawei.cloud.services.drive.model.FileList
import com.isoguzay.inappcodelab.R
import kotlinx.android.synthetic.main.activity_drive_kit.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.util.HashMap

class DriveKitActivity : AppCompatActivity() {

    private var mCredential: DriveCredential? = null
    private var accessToken: String? = null
    private var unionId: String? = null
    private var directoryCreated: File? = null
    private var fileUploaded: File? = null
    private var fileSearched: File? = null
    private var filePathForUpload : String? = null


    companion object {
        private val MIME_TYPE_MAP: MutableMap<String, String> = HashMap()
        private const val REQUEST_SIGN_IN_LOGIN = 1002
        private const val TAG = "MainActivity"
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
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
        setContentView(R.layout.activity_drive_kit)

        accessToken = intent.getStringExtra("accessToken")
        unionId = intent.getStringExtra("unionId")
        init(unionId,accessToken,refreshAT)

        imageView_uploadPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }

        }

        button_drive_upload.setOnClickListener {
            uploadFiles()
        }

        button_drive_download.setOnClickListener {
            queryFiles()
        }

    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageView_uploadPhoto.setImageURI(data?.data)
            val uriPathHelper = URIPathHelper()
            val filePath = data?.data?.let { uriPathHelper.getPath(this, it) }
            filePathForUpload = filePath
        }
    }

    //Function to Upload files
    @SuppressLint("SdCardPath")
    private fun uploadFiles() {
        GlobalScope.launch {
            try {
                if (accessToken == null) {
                    showTips("please click 'Login'.")
                    return@launch
                }
                if (StringUtils.isNullOrEmpty(
                        filePathForUpload.toString()
                    )
                ) {
                    showTips("Please input upload file name above.")
                    return@launch
                }

                val fileObject = java.io.File(filePathForUpload)
                if (!fileObject.exists()) {
                    showTips("The input file does not exit.")
                    return@launch
                }
                val appProperties: MutableMap<String, String> = HashMap()
                appProperties["appProperties"] = "property"
                File().setFileName("somepath" + System.currentTimeMillis()).setMimeType("application/vnd.huawei-apps.folder").appSettings = appProperties
                directoryCreated = buildDrive()?.files()?.create(File())?.execute()
                // create test.jpg on cloud
                val mimeType = mimeType(fileObject)
                val content = File()
                    .setFileName(fileObject.name)
                    .setMimeType(mimeType)
                    .setParentFolder(listOf(directoryCreated?.id))
                fileUploaded = buildDrive()?.files()
                    ?.create(content, FileContent(mimeType, fileObject))
                    ?.setFields("*")
                    ?.execute()
                showTips("upload success")
            } catch (ex: Exception) {
                Log.d(TAG, "upload", ex)
                showTips("upload error $ex")
            }

        }
    }

    //Function to QueryFiles
    private fun queryFiles() {
        GlobalScope.launch {
            try {
                if (accessToken == null) {
                    showTips("please click 'Login'.")
                    return@launch
                }
                if (StringUtils.isNullOrEmpty(
                        editText_download.text.toString()
                    )
                ) {
                    showTips("please input file name above.")
                    return@launch
                } else {
                    val queryFile =
                        "fileName = '" + editText_download.text + "' and mimeType != 'application/vnd.huawei-apps.folder'"
                    val request = buildDrive()?.files()?.list()
                    var files: FileList?
                    while (true) {
                        files = request
                            ?.setQueryParam(queryFile)
                            ?.setPageSize(10)
                            ?.setOrderBy("fileName")
                            ?.setFields("category,nextCursor,files/id,files/fileName,files/size")
                            ?.execute()
                        if (files == null || files.files.size > 0) {
                            break
                        }
                        if (!StringUtils.isNullOrEmpty(files.nextCursor)) {
                            request?.cursor = files.nextCursor
                        } else {
                            break
                        }
                    }
                    var text: String
                    if (files != null && files.files.size > 0) {
                        fileSearched = files.files[0]
                        text = fileSearched.toString()
                    } else {
                        text = "empty"
                    }
                    val finalText = text
                    runOnUiThread { textView_download.text = finalText }
                    showTips("query ok")
                }
    } catch (ex: Exception) {
        Log.d(TAG, "query", ex)
        showTips("query error $ex")
    }

        }
    }

    //function to Download files
    private fun downloadFiles() {
        GlobalScope.launch {

            try {
                if (accessToken == null) {
                    showTips("please click 'Login'.")
                    return@launch
                } else if (fileSearched == null) {
                    showTips("please click 'QUERY FILE'.")
                    return@launch
                } else {
                    val content = File()
                    val request = buildDrive()?.files()?.get(fileSearched?.id)
                    content.setFileName(fileSearched?.fileName).id = fileSearched?.id
                    val downloader = request?.mediaHttpDownloader
                    fileSearched?.getSize()?.minus(1)?.let {
                        downloader?.setContentRange(
                            0, it
                        )
                    }
                    val filePath =
                        "/storage/emulated/0/Huawei/Drive/DownLoad/Demo_" + fileSearched?.fileName
                    request?.executeContentAndDownloadTo(
                        FileOutputStream(
                            java.io.File(
                                filePath
                            )
                        )
                    )
                    showTips("download to $filePath")
                }
            } catch (ex: Exception) {
                Log.d(TAG, "download", ex)
                showTips("download error $ex")
            }
        }

    }

    private fun showTips(toastText: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, toastText, Toast.LENGTH_LONG).show()
            textView_upload.text = toastText
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

    private fun buildDrive() = Drive.Builder(mCredential, this).build()


    private fun mimeType(file: java.io.File?): String? {
        if (file != null && file.exists() && file.name.contains(".")) {
            val fileName = file.name
            val suffix = fileName.substring(fileName.lastIndexOf("."))
            if (MIME_TYPE_MAP.keys.contains(suffix)) {
                return MIME_TYPE_MAP[suffix]
            }
        }
        return "*/*"
    }
}