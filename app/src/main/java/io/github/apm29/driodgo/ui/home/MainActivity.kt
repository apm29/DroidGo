package io.github.apm29.driodgo.ui.home

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.apm29.core.arch.BaseActivity
import io.github.apm29.driodgo.R
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_host.*

class MainActivity : BaseActivity() {



    private val cardStackFragment: CardStackFragment by lazy {
        CardStackFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_drawer)

        val fragment = supportFragmentManager.findFragmentByTag("fragment") ?: cardStackFragment

        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentHost, fragment,"fragment")
                        .commit()
                    true
                }
                R.id.navigation_dashboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentHost, fragment,"fragment")
                        .commit()
                    true
                }
                R.id.navigation_notifications -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentHost, fragment,"fragment")
                        .commit()
                    true
                }
                else->false
            }
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        /**
         * DeepLink
         */
        val appLinkIntent = intent
        val appLinkData = appLinkIntent.data
        val path = appLinkData?.path
        println("DeepLink-path -- $path")


        navigation.selectedItemId = savedInstanceState?.getInt("index", R.id.navigation_home) ?: R.id.navigation_home


        drawer.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener true
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        println(intent?.extras)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("index", navigation.selectedItemId)
    }


}