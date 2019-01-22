package nilezia.app.foodorder.ui.login


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import nilezia.app.foodorder.R
import nilezia.app.foodorder.base.BaseMvpActivity
import nilezia.app.foodorder.helper.FirebaseHelper
import nilezia.app.foodorder.ui.MainActivity


class LoginActivity : BaseMvpActivity<LoginContract.View, LoginContract.Presenter>(), LoginContract.View {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mAuth: FirebaseAuth? = null


    companion object {
        private const val RC_SIGN_IN = 1100
    }

    override fun setupView() {


    }

    override fun initial() {
        setupFirebase()
        getPresenter().registerFirebase(FirebaseHelper(applicationContext))
        btnSignInWithGoogle.setOnClickListener {
            getPresenter().onGoogleSignIn()
            startActivityForResult(googleSignIn(), RC_SIGN_IN)
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

    override fun showLoginSuccess() {

    }

    override fun showDialogLoginFail() {

    }

    override fun showLoadingDialog() {
        progressLogin.visibility = View.VISIBLE
    }

    override fun hideLoadingDialog() {
        progressLogin.visibility = View.GONE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppCompatActivity.RESULT_CANCELED && resultCode == AppCompatActivity.RESULT_OK) {
            finish()
        } else if (requestCode == RC_SIGN_IN) { // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            getPresenter().onConnectGoogleResult(result)
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

    private fun googleSignIn() = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)

    private fun setupFirebase() {
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            // hideProgressDialog();
            if (user != null) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                setupGoogleSign()
            }
        }
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
