package com.akakim.koreannightapplication.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.akakim.koreannightapplication.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity() {


    var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)



        flLayout.systemUiVisibility =  View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        pbIntro.animate()
                .setDuration(3000L)
                .alpha( 1f )
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        pbIntro.visibility = View.GONE
                    }
                })

        handler.postDelayed( object : Runnable{
            override fun run() {

                var i = Intent(this@IntroActivity,LoginActivity::class.java)

                i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK )
                i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK )

                startActivity ( i )

            }
        },3000L)

    }
}
