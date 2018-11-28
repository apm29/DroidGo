package io.github.apm29.driodgo.model.artifact.bean

import com.google.gson.annotations.SerializedName

data class ArtifactUri(@SerializedName("expire_time")
                       val expireTime: Long?,
                       @SerializedName("cdn_root")
                       val cdnRoot: String?,
                       @SerializedName("url")
                       val url: String?)