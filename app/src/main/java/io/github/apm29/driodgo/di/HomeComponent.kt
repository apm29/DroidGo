package io.github.apm29.driodgo.di

import dagger.Component
import io.github.apm29.core.arch.CoreComponent
import io.github.apm29.driodgo.ui.home.MainActivity
import io.github.apm29.driodgo.anno.ActivityScope


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
    fun inject(activity: MainActivity)
}

