package nilezia.app.foodorder.ui.login


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import nilezia.app.foodorder.R
import nilezia.app.foodorder.base.BaseMvpActivity
import nilezia.app.foodorder.dialog.DialogManager
import nilezia.app.foodorder.helper.FirebaseHelper
import nilezia.app.foodorder.ui.MainActivity
import nilezia.app.foodorder.ui.signup.SignUpActivity


class LoginActivity : BaseMvpActivity<LoginContract.View, LoginContract.Presenter>(), LoginContract.View {


    private var mGoogleApiClient: GoogleApiClient? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mAuth: FirebaseAuth? = null

    var fb: Button? = null

    private lateinit var callbackManager: CallbackManager
    private var loginButton: LoginButton? = null

    companion object {
        private const val RC_SIGN_IN = 1100
    }

    override fun setupView() {
        fb = findViewById(R.id.fb)
        loginButton = findViewById(R.id.buttonFacebookLogin)
    }

    override fun initial() {
        setupFirebase()
        setupFacebookLogin()
        getPresenter().registerFirebase(FirebaseHelper(applicationContext))
        btnSignInWithGoogle.setOnClickListener {
            getPresenter().onGoogleSignIn()
            startActivityForResult(googleSignIn(), RC_SIGN_IN)

        }

        fb?.setOnClickListener {

            loginButton?.performClick()
        }

        btnEmailLogin.setOnClickListener {

            getPresenter().onEmailLogin(edtUsername.text.toString(), edtPassword.text.toString())

        }
        txt_sign_up.setOnClickListener {
            val intent = Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setupLayout(): Int = R.layout.activity_login

    override fun bindView() {

    }

    override fun setupInstance() {

    }

    override fun createPresenter(): LoginContract.Presenter = LoginPresenter.create()

    override fun onRestoreInstanceState(bundle: Bundle) {

    }

    override fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Log.w("Firebase", "signInWithCredential", task.exception)
                    }
                }
    }

    override fun firebaseAuthWithFacebook(accessToken: AccessToken?) {

        val credential = FacebookAuthProvider.getCredential(accessToken?.token!!)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        Log.w("fbLog", "signInWithCredential:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun singinWithEmail(username: String, password: String) {
        mAuth?.signInWithEmailAndPassword(username, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {

                        showDialogLoginFail(task.exception?.message!!)
                    }
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppCompatActivity.RESULT_CANCELED && resultCode == AppCompatActivity.RESULT_OK) {
            finish()
        } else if (requestCode == RC_SIGN_IN) { // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            getPresenter().onConnectGoogleResult(result)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient?.connect()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()

        if (mGoogleApiClient != null && mGoogleApiClient?.isConnected!!) {
            mGoogleApiClient?.stopAutoManage(this@LoginActivity)
            mGoogleApiClient?.disconnect()
        }
        mAuth!!.removeAuthStateListener(mAuthListener!!)
    }

    override fun onPause() {
        super.onPause()
        mGoogleApiClient?.stopAutoManage(this)
        mGoogleApiClient?.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        mGoogleApiClient?.disconnect()
    }

    override fun showLoginSuccess() {

        //TODO:GotoMainActivity

    }

    override fun showDialogLoginFail(msg: String) {
        DialogManager.showMessageDialog(this@LoginActivity, msg)
    }

    override fun showLoadingDialog() {
        progressLogin.visibility = View.VISIBLE
    }

    override fun hideLoadingDialog() {
        progressLogin.visibility = View.GONE
    }

    private fun googleSignIn() = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)

    private fun setupFirebase() {
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            // hideProgressDialog();
            if (user != null) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                setupGoogleSign()
            }
        }
    }

    private fun setupFacebookLogin() {
        callbackManager = CallbackManager.Factory.create()
        loginButton?.setReadPermissions("email", "public_profile")
        loginButton?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("fbLog", "facebook:onSuccess:$loginResult")
                getPresenter().onConnectFacebook(loginResult.accessToken)
            }

            override fun onCancel() {
                showDialogLoginFail("facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                showDialogLoginFail("facebook:onError")
            }
        })
    }

    private fun setupGoogleSign() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

    }
}
