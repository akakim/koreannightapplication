package com.akakim.koreannightapplication.task

import android.os.AsyncTask
import com.akakim.koreannightapplication.data.ResponseData


open class UserLoginTask
    constructor(val email : String, val password : String, val onFinshListener: onFinshListener)
    : AsyncTask<Void, Void, ResponseData>() {



    override fun doInBackground(vararg params: Void?): ResponseData {

        val response = ResponseData()




        return response
    }


    override fun onPostExecute(result: ResponseData) {
        super.onPostExecute(result)


    }
}