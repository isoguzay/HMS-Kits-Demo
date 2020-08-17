package com.isoguzay.inappcodelab.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hmf.tasks.Task
import com.huawei.hms.api.HuaweiMobileServicesUtil
import com.huawei.hms.common.ApiException
import com.huawei.hms.jos.JosApps
import com.huawei.hms.jos.games.Games
import com.huawei.hms.jos.games.PlayersClient
import com.huawei.hms.jos.games.player.Player
import com.huawei.hms.jos.games.player.PlayerExtraInfo
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult
import com.isoguzay.inappcodelab.R
import kotlinx.android.synthetic.main.activity_game_service.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class GameServiceActivity : AppCompatActivity() {

    private val TAG = "GameServiceLog"
    private lateinit var playersClient: PlayersClient
    private var playerID: String? = null
    private var sessionId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_service)
        HuaweiMobileServicesUtil.setApplication(application);

        button_game_init.setOnClickListener {
            init()
        }

        button_game_sign_in.setOnClickListener {
            signIn()
        }

        button_game_get_current_player.setOnClickListener {
            login()
        }

        button_game_submit_begin.setOnClickListener {
            timeReportStart()
        }

        button_game_submit_end.setOnClickListener {
            timeReportEnd()
        }

        button_game_get_player_info.setOnClickListener {
            getPlayerExfra()
        }

    }

    fun getHuaweiIdParams(): HuaweiIdAuthParams? {
        return HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).setIdToken()
            .createParams()
    }

    private fun init() {
        val appsClient = JosApps.getJosAppsClient(this)
        appsClient.init()
        Log.d(TAG,"init success")
    }

        fun signIn() {
            val authHuaweiIdTask =
                HuaweiIdAuthManager.getService(this, getHuaweiIdParams()).silentSignIn()
            authHuaweiIdTask.addOnSuccessListener { authHuaweiId ->
                Log.i(TAG, "silentsignIn success")
                Log.i(TAG, "display:" + authHuaweiId.displayName)
                login()
            }.addOnFailureListener { e ->
                if (e is ApiException) {
                    Log.i(TAG, "signIn failed:" + e.statusCode)
                    Log.i(TAG, "start getSignInIntent")
                    //                    Sign in explicitly. The sign-in result is obtained in onActivityResult.
                    val service =
                        HuaweiIdAuthManager.getService(application, getHuaweiIdParams())
                    startActivityForResult(service.signInIntent, 6013)
                }
            }
        }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 6013) {
            if (null == data) {
                Log.e(TAG,"signIn inetnt is null")
                return
            }
            val jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT")
            if (TextUtils.isEmpty(jsonSignInResult)) {
                Log.d(TAG,"signIn result is empty")
                return
            }
            try {
                val signInResult =
                    HuaweiIdAuthResult().fromJson(jsonSignInResult)
                if (0 == signInResult.status.statusCode) {
                    Log.d(TAG,"signIn success.")
                    Log.d(TAG,"signIn result: " + signInResult.toJson())
                } else {
                    Log.e(TAG,"signIn failed: " + signInResult.status.statusCode)
                }
            } catch (var7: JSONException) {
                Log.e(TAG,"Failed to convert json from signInResult.")
            }
        }
    }

    fun login() {
        playersClient = Games.getPlayersClient(this)
        val playerTask: Task<Player> = playersClient.getCurrentPlayer()
        playerTask.addOnSuccessListener { player ->
            playerID = player.playerId
            Log.i(
                TAG,
                "getPlayerInfo Success, player info: " + player.playerId
            )
        }.addOnFailureListener { e -> //  Failed to obtain player information.
            if (e is ApiException) {
                Log.e(
                    TAG,
                    "getPlayerInfo failed, status: " + e.statusCode
                )
            }
        }
    }

    private fun timeReportStart() {
        if (playersClient == null) {
            Log.i(TAG, "playersClient is null, please init  playersClient first")
            login()
            return
        }
        if (playerID == null) {
            Log.i(TAG, "playerID is null, please getcurrentPlayer login first")
            login()
            return
        }
        val uid: String = UUID.randomUUID().toString()
        val task: Task<String> = playersClient.submitPlayerEvent(playerID, uid, "GAMEBEGIN")
        task.addOnSuccessListener(OnSuccessListener { jsonRequest ->
            try {
                val data = JSONObject(jsonRequest)
                sessionId = data.getString("transactionId")
            } catch (e: JSONException) {
                Log.e(TAG,"parse jsonArray meet json exception")
                return@OnSuccessListener
            }
            Log.d(TAG,"submitPlayerEvent traceId: $jsonRequest")
        }).addOnFailureListener { e ->
            if (e is ApiException) {
                val result = "rtnCode:" + e.statusCode
                Log.d(TAG, result)
            }
        }
    }

    private fun timeReportEnd() {
        if (playersClient == null) {
            Log.i(TAG, "playersClient is null, please init  playersClient first")
            login()
            return
        }
        if (playerID == null) {
            Log.i(TAG, "playerID is null, please getcurrentPlayer login first")
            login()
            return
        }
        if (sessionId == null) {
            Log.i(TAG, "sessionId is null, please submitPlayerEvent Begin  first")
            login()
            return
        }
        val task: Task<String> =
            playersClient.submitPlayerEvent(playerID, sessionId, "GAMEEND")
        task.addOnSuccessListener { s -> Log.d(TAG,"submitPlayerEvent traceId: $s") }
            .addOnFailureListener { e ->
                if (e is ApiException) {
                    val result = "rtnCode:" + e.statusCode
                    Log.d(TAG, result)
                }
            }
    }

    private fun getPlayerExfra() {
        if (playersClient == null) {
            Log.i(TAG, "playersClient is null, please init  playersClient first")
            login()
            return
        }
        if (sessionId == null) {
            Log.i(TAG, "sessionId is null, please submitPlayerEvent Begin  first")
            login()
            return
        }
        val task: Task<PlayerExtraInfo> =
            playersClient.getPlayerExtraInfo(sessionId)
        task.addOnSuccessListener { extra ->
            if (extra != null) {
                Log.d(TAG,
                    "IsRealName: " + extra.isRealName + ", IsAdult: " + extra.isAdult
                            + ", PlayerId: " + extra.playerId + ", PlayerDuration: " + extra.playerDuration
                )
            } else {
                Log.e(TAG,"Player extra info is empty.")
            }
        }.addOnFailureListener { e ->
            if (e is ApiException) {
                val result = "rtnCode:" + e.statusCode
                Log.e(TAG, result)
            }
        }
    }
}