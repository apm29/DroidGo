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
import io.github.apm29.core.utils.expectedHeight
import io.github.apm29.core.utils.grow
import io.github.apm29.core.utils.shrink
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.di.CardStackModule
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.vm.CardStackViewModel
import kotlinx.android.synthetic.main.fragment_card_stack.*
import javax.inject.Inject
import io.github.apm29.driodgo.di.DaggerCardStackComponent
import kotlinx.android.synthetic.main.header_card_stack_layout.*


class CardStackFragment : BaseFragment() {
    override fun layoutRes(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_card_stack
    }

    @Inject
    lateinit var cardStackViewModel: CardStackViewModel

    @Inject
    lateinit var cardAdapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerCardStackComponent.builder()
            .cardStackModule(CardStackModule(this))
            .coreComponent((requireActivity().application as DroidGoApp).mCoreComponent)
            .build()
            .inject(this)

        cardStackViewModel.artifactItems.observe(this, Observer {
            cardAdapter.setCard(it)
        })
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        val toolbar = view.findViewById<Toolbar?>(R.id.toolbar)
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
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


        cardFilter.onFilterChangeCallBack = {
            it.forEach { entry ->
                cardAdapter.addCardFilter(entry.key,entry.value)
            }
        }
        cardFilter.viewTreeObserver.addOnGlobalLayoutListener(
            object :ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    cardFilter.expectedHeight = cardFilter.measuredHeight
                    cardFilter.shrink()
                    cardFilter.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

            }
        )


        loadCardData()
    }

    private fun loadCardData(reload: Boolean = false) {
        cardStackViewModel.loadArtifact(reload)
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

    private var menu:Menu? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.card_stack_menu, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when {
            item?.itemId == R.id.menu_refresh -> {
                onReloadActionClicked()
                true
            }
            item?.itemId == R.id.menu_expand -> {
                onExpandActionClicked(item)
                true
            }
            item?.itemId == R.id.menu_filter->{
                onFilterActionClicked(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onFilterActionClicked(item: MenuItem) {

        //cardFilter.visibility = if(item.isChecked ) View.GONE else View.VISIBLE
        if(!item.isChecked ) {
            if(cardFilter.shrink()){
                item.isChecked = !item.isChecked
            }
        }else{
            if(cardFilter.grow()){
                item.isChecked = !item.isChecked
            }
        }
    }

    private fun onReloadActionClicked() {
        loadCardData(reload = true)
        //先把卡牌样式更换->expanded
        menu?.findItem(R.id.menu_expand)?.apply {
            onExpandActionClicked(this, true)
        }
    }

    private fun onExpandActionClicked(item: MenuItem, expandAll:Boolean? = null) {
        item.isChecked = expandAll?:!item.isChecked
        item.icon = ContextCompat.getDrawable(
            requireContext(),
            if (item.isChecked) R.drawable.ic_action_all_collapse else R.drawable.ic_action_all_expand
        )
        cardAdapter.expandAll(item.isChecked)
    }

}