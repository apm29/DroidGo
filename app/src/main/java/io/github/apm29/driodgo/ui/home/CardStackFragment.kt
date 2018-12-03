package io.github.apm29.driodgo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.apm29.core.utils.GlideApp
import io.github.apm29.driodgo.di.DaggerHomeComponent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.github.apm29.core.arch.DroidGoApp
import io.github.apm29.core.arch.IOSensitiveViewModel
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.di.HomeModule
import io.github.apm29.driodgo.vm.HomeViewModel
import javax.inject.Inject
import io.github.apm29.core.arch.BaseActivityViewModel

class CardStackFragment : Fragment() {


    val io: IOSensitiveViewModel by lazy {
        ViewModelProviders.of(this).get(BaseActivityViewModel::class.java)
    }

    @Inject
    lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerHomeComponent.builder()
            .homeModule(HomeModule(this))
            .coreComponent((requireActivity().application as DroidGoApp).mCoreComponent)
            .build()
            .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflated = layoutInflater.inflate(R.layout.fragment_card_stack, container, false)
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val widthPixels = displayMetrics.widthPixels

        val imageWidth = ((widthPixels * 1f - 48 * density) / 2).toInt()
        val imageHeight = (imageWidth * 16f / 9f).toInt()

        val cardStack = inflated.findViewById<RecyclerView>(R.id.cardStack)

        cardStack.layoutManager = GridLayoutManager(requireContext(), 2)

        homeViewModel.artifactItems.observe(this, Observer {
            cardStack.adapter = object : RecyclerView.Adapter<VH>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                    val view = layoutInflater.inflate(R.layout.item_card_layout, parent, false)
                    return VH(view)
                }

                override fun getItemCount(): Int {
                    return it.filter { card ->
                        card.card_type != "Passive Ability" && card.card_type != "Ability"
                    }.size
                }

                override fun onBindViewHolder(holder: VH, position: Int) {
                    val cardListItem = it.filter {
                        it.card_type != "Passive Ability" && it.card_type != "Ability"
                    }[holder.adapterPosition]

                    GlideApp.with(this@CardStackFragment)
                        .load(cardListItem.large_image?.schinese)
                        .override(imageWidth, imageHeight)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.largeImage)
                }

            }
        })


        homeViewModel.loadArtifact()

        return inflated
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val largeImage: ImageView = itemView.findViewById(R.id.largeImage)
    }
}