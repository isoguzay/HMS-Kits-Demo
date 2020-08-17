package com.isoguzay.inappcodelab.hms.ml.text

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLLocalTextSetting
import com.huawei.hms.mlsdk.text.MLText
import com.isoguzay.inappcodelab.R
import kotlinx.android.synthetic.main.activity_text_recognition.*
import java.io.IOException


class TextRecognitionActivity : AppCompatActivity() {

    private val myCameraRequestCode = 100
    private val myStorageRequestCode = 300
    private var selectedImageBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognition)

        button_text_recog_from_image.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), myStorageRequestCode)
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 200)
            }
        }

        imageView_text_recog.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), myCameraRequestCode)
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 100)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == myCameraRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 100)
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
        if(requestCode == myStorageRequestCode){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 200)
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                selectedImageBitmap = data?.extras?.get("data") as Bitmap
                imageView_text_recog.setImageBitmap(selectedImageBitmap)
                performTextRecognitionOnDevice(selectedImageBitmap!!)
            }
        }
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                val pickedImage: Uri? = data?.data
                imageView_text_recog.setImageBitmap(MediaStore.Images.Media.getBitmap(this.contentResolver, pickedImage))
                try {
                    performTextRecognitionOnDevice(MediaStore.Images.Media.getBitmap(this.contentResolver, pickedImage))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun performTextRecognitionOnDevice(bitmap: Bitmap){
        val setting = MLLocalTextSetting.Factory()
            .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
            .setLanguage("en")
            .create()
        val analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting)
        val frame = MLFrame.fromBitmap(bitmap)

        val task: Task<MLText> = analyzer.asyncAnalyseFrame(frame)
        task.addOnSuccessListener {
            Toast.makeText(this, it.stringValue, Toast.LENGTH_SHORT).show()
            textView_img_to_text.text = (it.stringValue)

        }.addOnFailureListener {
            Toast.makeText(this,"Text recognition failed, error code ->${it.message}",Toast.LENGTH_SHORT).show()
        }
    }

}