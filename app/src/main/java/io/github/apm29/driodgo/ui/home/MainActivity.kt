package io.github.apm29.driodgo.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.apm29.core.arch.*
import io.github.apm29.core.utils.GlideApp
import io.github.apm29.core.utils.showToast
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.di.DaggerHomeComponent
import io.github.apm29.driodgo.di.HomeModule
import io.github.apm29.driodgo.vm.HomeViewModel
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_host.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    @Inject
    lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data

        println(appLinkAction)
        println(appLinkData)

        DaggerHomeComponent.builder()
            .homeModule(HomeModule(this))
            .coreComponent(DroidGoApp.getCoreComponent(this))
            .build()
            .inject(this)


        savedInstanceState?.apply {
            navigation.selectedItemId = getInt("index", R.id.navigation_home)
        }


        drawer.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener true
        }

        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val widthPixels = displayMetrics.widthPixels

        val imageWidth = ((widthPixels * 1f - 48 * density)/2).toInt()
        val imageHeight = (imageWidth * 16f / 9f).toInt()

        cardList.layoutManager = GridLayoutManager(this, 2)

        homeViewModel.artifactItems.observe(this, Observer {
            cardList.adapter = object : RecyclerView.Adapter<VH>() {
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

                    GlideApp.with(this@MainActivity)
                        .load(cardListItem.large_image?.schinese)
                        .override(imageWidth,imageHeight)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.largeImage)
                }

            }
        })
        showToast("------start load cards-------")
        homeViewModel.message.observe(this, Observer {
            it.getDataIfNotConsumed {
                AlertDialog.Builder(this)
                    .setMessage(it)
                    .create()
                    .show()
            }
        })
        homeViewModel.loading.observe(this, Observer {
            it.getDataIfNotConsumed { loading ->
                showToast(if (loading) "加载中" else "加载完成")
            }
        })

        homeViewModel.loadArtifact()
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val largeImage: ImageView = itemView.findViewById(R.id.largeImage)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        println(intent?.extras)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("index", navigation.selectedItemId)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.main_menu,
            menu
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_refresh) {
            homeViewModel.loadArtifact(reload = true)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
