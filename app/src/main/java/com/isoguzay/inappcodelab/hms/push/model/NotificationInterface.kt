package com.isoguzay.inappcodelab.hms.push.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationInterface {
    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("v1/102253619/messages:send")
    fun createNotification(
        @Header("Authorization") authorization: String?,
        @Body notificationMessageBody: NotificationMessageBody
    ) : Call<NotificationMessage>
}