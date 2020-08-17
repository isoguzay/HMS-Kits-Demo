package com.isoguzay.inappcodelab.view

import android.Manifest
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import com.isoguzay.inappcodelab.R
import kotlinx.android.synthetic.main.activity_location.*


class LocationActivity : AppCompatActivity() {

    private val TAG = "LocationKitLog"
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        button_get_location.setOnClickListener {
            checkPermissions()
            getLastLocation()
        }

    }

    private fun getLastLocation() {
        try {
            val lastLocation =  mFusedLocationProviderClient.lastLocation

                lastLocation.addOnSuccessListener(OnSuccessListener { location ->
                if (location == null) {
                    Log.i(TAG, "getLastLocation onSuccess location is null")
                    return@OnSuccessListener
                }

                textView_get_location.text = "${location.latitude}, ${location.longitude}"

                Log.i(
                    TAG,
                    "getLastLocation onSuccess location[Longitude,Latitude]:${location.longitude},${location.latitude}"
                )
                return@OnSuccessListener
            }).addOnFailureListener { e ->
                Log.e(TAG, "getLastLocation onFailure:${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "getLastLocation exception:${e.message}")
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    "android.permission.ACCESS_BACKGROUND_LOCATION"
                )
                ActivityCompat.requestPermissions(this, strings, 2)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 1 && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful")
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION  failed")
            }
        }
        if (requestCode == 2) {
            if (grantResults.size > 2 && grantResults[2] == PERMISSION_GRANTED && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED
            ) {
                Log.i(
                    TAG,
                    "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful"
                )
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed")
            }
        }
    }
}