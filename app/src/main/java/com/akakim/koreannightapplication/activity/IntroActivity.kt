package com.akakim.koreannightapplication.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
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



        

        pbIntro.animate()
                .setDuration(1000L)
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
        },1000L)

    }
}
