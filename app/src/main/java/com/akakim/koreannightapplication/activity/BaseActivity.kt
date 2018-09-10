package com.akakim.koreannightapplication.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity


open class BaseActivity : AppCompatActivity(){




    protected fun moveClearTask(clazz: Class<out Activity>){

        Intent(this,clazz).let {
            it.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK )
            it.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK )
            startActivity( it )
        }
    }
}