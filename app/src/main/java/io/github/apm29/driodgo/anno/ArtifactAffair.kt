package io.github.apm29.driodgo.anno

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@kotlin.annotation.Retention
annotation class ArtifactAffair(
    val value: String = "artifact"
)