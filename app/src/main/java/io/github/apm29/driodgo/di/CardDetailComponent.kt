package io.github.apm29.driodgo.di

import dagger.Component
import io.github.apm29.core.arch.dagger.CoreComponent
import io.github.apm29.driodgo.anno.FragmentScope
import io.github.apm29.driodgo.ui.home.CardDetailActivity
@FragmentScope
@Component(
    modules = [
        CardDetailModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
interface CardDetailComponent {
    fun inject(detailActivity: CardDetailActivity)
}