package io.github.apm29.driodgo.di

import dagger.Component
import io.github.apm29.core.arch.dagger.CoreComponent
import io.github.apm29.driodgo.anno.ActivityScope
import io.github.apm29.driodgo.anno.FragmentScope
import io.github.apm29.driodgo.ui.home.CardStackFragment


@FragmentScope
@Component(
    modules = [
        CardStackModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
interface CardStackComponent {
    fun inject(activity: CardStackFragment)
}

