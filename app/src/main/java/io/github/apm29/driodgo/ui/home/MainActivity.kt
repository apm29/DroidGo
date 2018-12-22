package io.github.apm29.driodgo.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.apm29.core.arch.BaseActivity
import io.github.apm29.driodgo.R
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_host.*

class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_drawer)

        val cardStackFragment1: Fragment by lazy {
            CardStackFragment()
        }
        val cardStackFragment2: Fragment by lazy {
            Fragment()
        }
        val cardStackFragment3: Fragment by lazy {
            Fragment()
        }


        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

            var fragment1 = supportFragmentManager.findFragmentByTag("fragment1")
            var fragment2 = supportFragmentManager.findFragmentByTag("fragment2")
            var fragment3 = supportFragmentManager.findFragmentByTag("fragment3")
            if (fragment1 == null) {
                fragment1 = cardStackFragment1
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentHost, fragment1, "fragment1")
                    .commit()
            }
            if (fragment2 == null) {
                fragment2 = cardStackFragment2
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentHost, fragment2, "fragment2")
                    .commit()
            }
            if (fragment3 == null) {
                fragment3 = cardStackFragment3
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentHost, fragment3, "fragment3")
                    .commit()
            }


            println("cardStackFragment1 = ${cardStackFragment1}")
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .show(fragment1)
                        .hide(fragment2)
                        .hide(fragment3)
                        .commit()
                    true
                }
                R.id.navigation_dashboard -> {
                    supportFragmentManager.beginTransaction()
                        .hide(fragment1)
                        .show(fragment2)
                        .hide(fragment3)
                        .commit()
                    true
                }
                R.id.navigation_notifications -> {
                    supportFragmentManager.beginTransaction()
                        .hide(fragment1)
                        .hide(fragment2)
                        .show(fragment3)
                        .commit()
                    true
                }
                else -> false
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