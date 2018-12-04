package io.github.apm29.driodgo.ui.home

import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.apm29.core.arch.BaseActivityViewModel
import io.github.apm29.core.arch.DroidGoApp
import io.github.apm29.core.arch.IOSensitiveViewModel
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.di.DaggerHomeComponent
import io.github.apm29.driodgo.di.HomeModule
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.vm.HomeViewModel
import kotlinx.android.synthetic.main.fragment_card_stack.*
import javax.inject.Inject

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
        val cardStack = inflated.findViewById<RecyclerView>(R.id.cardStack)
        val toolbar = inflated.findViewById<Toolbar?>(R.id.toolbar)
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
//        cardStack.layoutManager = GridLayoutManager(requireContext(), 2)

        cardStack.layoutManager = LinearLayoutManager(requireContext())
        homeViewModel.artifactItems.observe(this, Observer {
            if (cardStack.adapter == null) {
                cardStack.adapter = CardAdapter(this.requireContext(), it.toMutableList())
            } else {
                (cardStack.adapter as CardAdapter).setCard(it)
            }
        })

        loadCardData()

        return inflated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    private fun loadCardData(reload:Boolean = false) {
        homeViewModel.loadArtifact(reload)
    }

    fun addFilter(tag: FilterTag, filter: (CardListItem) -> Boolean): Boolean {
        if (cardStack.adapter == null) return false
        (cardStack.adapter as CardAdapter).addCardFilter(tag, filter)
        return true
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val largeImage: ImageView? = itemView.findViewById(R.id.largeImage)
        val textName: TextView? = itemView.findViewById(R.id.textName)
        val textAbility: TextView? = itemView.findViewById(R.id.textAbility)
        val imageIncludes: RecyclerView? = itemView.findViewById(R.id.imageIncludes)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.card_stack_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_refresh) {
            loadCardData(reload = true)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}