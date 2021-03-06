package nilezia.app.foodorder.ui.login

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import nilezia.app.foodorder.base.BaseMvpPresenter
import nilezia.app.foodorder.base.BaseMvpView
import nilezia.app.foodorder.helper.FirebaseHelper
import nilezia.app.foodorder.ui.login.model.LoginData

interface LoginContract {

    interface View : BaseMvpView {

        fun showLoginSuccess()

        fun showDialogLoginFail(msg: String)

        fun showLoadingDialog()

        fun hideLoadingDialog()

        fun firebaseAuthWithGoogle(acct: GoogleSignInAccount)

        fun firebaseAuthWithFacebook(accessToken: AccessToken?)

        fun singinWithEmail(username: String, password: String)

    }

    interface Presenter : BaseMvpPresenter<View> {

        fun registerFirebase(firebaseHelper: FirebaseHelper)

        fun login(loginData: LoginData)

        fun onGoogleSignIn()

        fun onConnectGoogleResult(result: GoogleSignInResult?)

        fun onConnectFacebook(accessToken: AccessToken)

        fun onEmailLogin(userName: String, password: String)

        fun isValidateEmptyLogin(userName: String, password: String):Boolean

    }

}
