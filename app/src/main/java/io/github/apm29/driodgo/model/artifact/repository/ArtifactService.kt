package io.github.apm29.driodgo.model.artifact.repository

import io.github.apm29.driodgo.model.artifact.bean.Artifact
import io.github.apm29.driodgo.model.artifact.bean.ArtifactUri
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface ArtifactService {
    @GET("/cardset/01")
    fun getArtifactUri():Single<ArtifactUri>

    @GET()
    fun getCardSet(
        @Url() path : String
    ):Single<Artifact>
}