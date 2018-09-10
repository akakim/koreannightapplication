package com.akakim.koreannightapplication.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.akakim.koreannightapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

                var user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
                if( user == null ){

                    moveClearTask( LoginActivity::class.java)
                    // 현재 로그인 한 정보가 없다.
                }else {

                    // 로그인한

                    if ( !user.isEmailVerified ){

                        Toast.makeText( this@IntroActivity , "이메일 인증이 되지 않았습니다. \n 이메일 인증을 해주세요 ",Toast.LENGTH_SHORT).show();
                        moveClearTask( LoginActivity::class.java)
                    } else {

                        moveClearTask( RoomListActivity::class.java)
                    }
                }

            }
        },3000L)

    }


}
