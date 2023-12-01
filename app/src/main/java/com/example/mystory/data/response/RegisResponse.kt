package com.example.mystory.data.response

import com.google.gson.annotations.SerializedName

class RegisResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)