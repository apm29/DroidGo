package io.github.apm29.driodgo.model.artifact.bean

import com.google.gson.annotations.SerializedName

data class Artifact(@SerializedName("card_set")
                    val cardSet: CardSet
)