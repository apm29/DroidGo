package io.github.apm29.driodgo.ui.home

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import io.github.apm29.core.arch.BaseFragment
import io.github.apm29.core.arch.DroidGoApp
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.di.DaggerHomeComponent
import io.github.apm29.driodgo.di.HomeModule
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.vm.HomeViewModel
import kotlinx.android.synthetic.main.fragment_card_stack.*
import javax.inject.Inject


class CardStackFragment : BaseFragment() {

    @Inject
    lateinit var homeViewModel: HomeViewModel

    @Inject
    lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerHomeComponent.builder()
            .homeModule(HomeModule(this))
            .coreComponent((requireActivity().application as DroidGoApp).mCoreComponent)
            .build()
            .inject(this)

        homeViewModel.artifactItems.observe(this, Observer {
            cardAdapter.setCard(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflated = layoutInflater.inflate(R.layout.fragment_card_stack, container, false)
        val toolbar = inflated.findViewById<Toolbar?>(R.id.toolbar)
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        return inflated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.visibility = View.VISIBLE
        searchView.queryHint = getString(R.string.hint_input_hero_name)
        searchView.setOnSearchClickListener {
            if (!searchView.query.isNullOrBlank()) {
                cardAdapter.addCardFilter(FilterTag.Name) { card ->
                    card.card_name?.schinese?.contains(searchView.query) == true
                }
            }
        }
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        cardAdapter.removeCardFilter(FilterTag.Name)
                    } else {
                        cardAdapter.addCardFilter(FilterTag.Name) {
                            it.card_name?.schinese?.contains(newText) == true
                        }
                    }
                    return true
                }

            }
        )
        cardStack.layoutManager = LinearLayoutManager(requireContext())
        val preLoader: ViewPreloadSizeProvider<CardListItem> = ViewPreloadSizeProvider()
        cardStack.addOnScrollListener(RecyclerViewPreloader(this, cardAdapter, preLoader, 5))
        cardStack.adapter = cardAdapter
        loadCardData()
    }

    private fun loadCardData(reload: Boolean = false) {
        homeViewModel.loadArtifact(reload)
    }


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val largeImage: ImageView = itemView.findViewById(R.id.largeImage)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textAbility: TextView = itemView.findViewById(R.id.textAbility)
        val imageIncludes: RecyclerView = itemView.findViewById(R.id.imageIncludes)

        val grpExpand: Group = itemView.findViewById(R.id.grpExpand)
        val grpCollapsed: Group = itemView.findViewById(R.id.grpCollapsed)

        val collapsedImage: ImageView = itemView.findViewById(R.id.collapsedImage)
        val actionExpand: ImageView = itemView.findViewById(R.id.actionExpand)
        val textSmallName: TextView = itemView.findViewById(R.id.textNameCollapsed)
        val cardStack: CardView = itemView.findViewById(R.id.cardStack)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.card_stack_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when {
            item?.itemId == R.id.menu_refresh -> {
                loadCardData(reload = true)
                true
            }
            item?.itemId == R.id.menu_expand -> {
                item.isChecked = !item.isChecked
                item.icon = ContextCompat.getDrawable(
                    requireContext(),
                    if (item.isChecked) R.drawable.ic_action_all_collapse else R.drawable.ic_action_all_expand
                )
                cardAdapter.expandAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}