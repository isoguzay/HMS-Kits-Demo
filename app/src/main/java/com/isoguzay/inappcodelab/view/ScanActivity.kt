package com.isoguzay.inappcodelab.view

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan
import com.isoguzay.inappcodelab.R
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : AppCompatActivity() {

    companion object
    {
        // Declare RemoteView instance
        private var remoteView: RemoteView? = null
        // Declare the key, used to get the value returned from scan kit
        const val SCAN_RESULT = "scanResult"
        var mScreenWidth = 0
        var mScreenHeight = 0
        // scan_view_finder width & height is  300dp
        const val SCAN_FRAME_SIZE = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        //1. Get screen density to calculate viewfinder's rect
        val dm = resources.displayMetrics
        //2. Get screen size
        val density = dm.density
        mScreenWidth = dm.widthPixels
        mScreenHeight = dm.heightPixels
        var scanFrameSize= (SCAN_FRAME_SIZE * density)
        //3. Calculate viewfinder's rect,it's in the middle of the layout
        // Set scanning area(Optional, rect can be null,If not configure,default is in the center of layout)
        val rect = Rect()
        apply {
            rect.left = (mScreenWidth / 2 - scanFrameSize / 2).toInt()
            rect.right = (mScreenWidth / 2 + scanFrameSize / 2).toInt()
            rect.top = (mScreenHeight / 2 - scanFrameSize / 2).toInt()
            rect.bottom = (mScreenHeight / 2 + scanFrameSize / 2).toInt()
        }
        // Initialize RemoteView instance, and set calling back for scanning result
        remoteView = RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE).build()
        remoteView?.onCreate(savedInstanceState)
        remoteView?.setOnResultCallback { result ->
            if (result != null && result.isNotEmpty() && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                val intent = Intent()
                intent.apply {
                    putExtra(SCAN_RESULT, result[0]) }
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            }
        }
        // Add the defined RemoteView to the page layout.
        val params = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val frameLayout = findViewById<FrameLayout>(R.id.rim1)
        frameLayout.addView(remoteView, params)
        // Back to Main Screen
        back_img.setOnClickListener {
            val back = Intent(applicationContext, MainActivity::class.java)
            startActivity(back)
        }
    }

    // Manage remoteView lifecycle
    override fun onStart() {
        super.onStart()
        remoteView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView?.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        remoteView?.onStop()
    }

}