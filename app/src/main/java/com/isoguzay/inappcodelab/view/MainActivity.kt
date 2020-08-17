package com.isoguzay.inappcodelab.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.huawei.agconnect.crash.AGConnectCrash
import com.huawei.hms.ml.scan.HmsScan
import com.isoguzay.inappcodelab.R
import com.isoguzay.inappcodelab.hms.ml.MlKitActivity
import com.isoguzay.inappcodelab.hms.ml.text.TextActivity
import com.isoguzay.inappcodelab.hms.push.manager.PushManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val DEFINED_CODE = 222
        private val REQUEST_CODE_SCAN = 0X01
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AGConnectCrash.getInstance().enableCrashCollection(true)
        PushManager.getDeviceIdToken(applicationContext)

        button_get.setOnClickListener {
            val goMainActivity = Intent(applicationContext, AccountActivity::class.java)
            startActivity(goMainActivity)
        }

        button_map.setOnClickListener {
            val goMapActivity = Intent(applicationContext, MapActivity::class.java)
            startActivity(goMapActivity)
        }

        button_analytics.setOnClickListener {
            val feedback = Intent(applicationContext, AnalyticsActivity::class.java)
            startActivity(feedback);
        }

        button_push.setOnClickListener {
            val push = Intent(applicationContext, PushActivity::class.java)
            startActivity(push);
        }

        button_drive.setOnClickListener {
            val drive = Intent(applicationContext, DriveActivity::class.java)
            startActivity(drive)
        }

        button_location.setOnClickListener {
            val location = Intent(applicationContext, LocationActivity::class.java)
            startActivity(location)
        }

        button_game_service.setOnClickListener {
            val gameService = Intent(applicationContext, GameServiceActivity::class.java)
            startActivity(gameService)
        }

        button_scan_kit.setOnClickListener {
            scanKitButtonClick()
        }

        button_ml_kit.setOnClickListener {
            val mlKit = Intent(applicationContext, MlKitActivity::class.java)
            startActivity(mlKit)
        }
    }

    private fun scanKitButtonClick() {
        // Initialize a list of required permissions to request runtime
        val list = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, list, DEFINED_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return
        }
        else if (requestCode == DEFINED_CODE) {
            //start your activity for scanning barcode
            this.startActivityForResult(
                Intent(this, ScanActivity::class.java), REQUEST_CODE_SCAN)
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        else if (requestCode == REQUEST_CODE_SCAN) {
            // Obtain the return value of HmsScan from the value returned by the onActivityResult method by using ScanUtil.RESULT as the key value.
            val hmsScan: HmsScan = data.getParcelableExtra(ScanActivity.SCAN_RESULT)
            if (!TextUtils.isEmpty(hmsScan.getOriginalValue()))
                Toast.makeText(this, hmsScan.getOriginalValue(), Toast.LENGTH_SHORT).show()
        }
    }
}
