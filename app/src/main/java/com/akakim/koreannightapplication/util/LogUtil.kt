package com.akakim.koreannightapplication.util

import android.content.Context
import android.util.Log
import com.akakim.koreannightapplication.BuildConfig


class LogUtil{
    companion object {

        fun LogD(clazzName : Class<Any>, msg : String ) {

            if( BuildConfig.DEBUG ) {
                Log.d(clazzName.simpleName, msg)
            }
        }
    }
}