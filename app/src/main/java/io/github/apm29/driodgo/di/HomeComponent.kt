package io.github.apm29.driodgo.di

import dagger.Component
import io.github.apm29.core.arch.dagger.CoreComponent
import io.github.apm29.driodgo.anno.ActivityScope
import io.github.apm29.driodgo.ui.home.CardStackFragment


@ActivityScope
@Component(
    modules = [
        HomeModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
interface HomeComponent {
    fun inject(activity: CardStackFragment)
}

