package com.akakim.koreannightapplication.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.util.Log
import com.akakim.koreannightapplication.R
import com.akakim.koreannightapplication.task.UserLoginTask
import com.akakim.koreannightapplication.task.onFinshListener
import com.akakim.koreannightapplication.util.LogUtil
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : BaseActivity(), LoaderCallbacks<Cursor>,FirebaseAuth.AuthStateListener, onFinshListener {

    private var mAuthTask : UserLoginTask? = null
    private var auth      : FirebaseAuth? = null

    private lateinit var gso       : GoogleSignInOptions
    private lateinit var gsoClient : GoogleSignInClient

    private val RC_SIGN_IN  = 123

    private var providers =  Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
    )

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1

    }


    companion object {

        private val REQUEST_READ_CONTACTS = 0

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        gso         = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN ).requestEmail().build()
        gsoClient   = GoogleSignIn.getClient( this , gso )
        auth        = FirebaseAuth.getInstance()



        // Set up the login form.
        populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        btnEmailSignin.setOnClickListener { attemptLogin() }



        btnGoogleSign.setOnClickListener {    startActivity( gsoClient.signInIntent )  }
    }

    override fun onStart() {
        super.onStart()



        var account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount( this )
        if( account == null ){
            // not signed
        }
       // updateUI( acccount )

    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }


    private fun attemptLogin() {


        // Reset errors.
        email.error         = null
        password.error      = null

        // Store values at the time of the login attempt.
        val emailStr        = email.text.toString()
        val passwordStr     = password.text.toString()

        var cancel              = false
        var focusView: View?    = null



        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {

            focusView?.requestFocus()
        } else {

            showProgress(true)
            mAuthTask = UserLoginTask(emailStr, passwordStr,this)
            mAuthTask!!.execute(null as Void?)
        }
    }

    private fun isEmailValid(email: String): Boolean {

        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {

        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }



    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        email.setAdapter(adapter)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( requestCode == RC_SIGN_IN ){

            GoogleSignIn.getSignedInAccountFromIntent( data )
                    .getResult( ApiException::class.java )
                    .let {
                        firebaseAuthWithGoogle( it )
                    }
        }


    }

    private fun handleSignInResult( completedTask  : Task<GoogleSignInAccount>){

        try{
            var account = completedTask.getResult( ApiException::class.java)

            firebaseAuthWithGoogle( account )
        }catch ( e : ApiException){

        }
    }

    private fun firebaseAuthWithGoogle(account : GoogleSignInAccount? ){



        LogUtil.LogD( this.javaClass , " firebaseWithGoogle : " + account?.id)

        auth?.let {

            it.signInWithCredential(GoogleAuthProvider.getCredential( account?.idToken, null ))
                    .addOnCompleteListener( object : OnCompleteListener<AuthResult>{
                        override fun onComplete(authTask : Task<AuthResult>) {

                            if( authTask.isSuccessful ){

                                // sign in success, update UI with the signed-in user's information

                                LogUtil.LogD( this@LoginActivity.javaClass, "signedInCredential:Success ")
                                updateUI( it.currentUser )

                            }else {
                                updateUI( null )
                                LogUtil.LogD( this@LoginActivity.javaClass,"signedIncredential: NotSuccess " )
                            }

                        }
                    })
        }


    }


    private fun updateUI( user : FirebaseUser? ){

        user?.let {





        }
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {

        var user : FirebaseUser? = firebaseAuth.currentUser

            if( user == null ){
                Log.d("TAG","authStatus out ")
            }else {
                Log.d("onAuthStateChanged","userId " + user.uid)
            }

    }


    override fun onFinish() {

    }

    override fun onProgress() {

    }




}
