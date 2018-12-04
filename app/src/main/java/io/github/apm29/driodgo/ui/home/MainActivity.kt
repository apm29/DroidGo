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

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentHost, cardStackFragment)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentHost, cardStackFragment)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentHost, cardStackFragment)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data

        println(appLinkAction)
        println(appLinkData)



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