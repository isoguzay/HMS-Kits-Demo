package com.isoguzay.inappcodelab.hms.push.model

data class AccessToken (
    var access_token : String,
    var expires_in : Int,
    var token_type : String
)