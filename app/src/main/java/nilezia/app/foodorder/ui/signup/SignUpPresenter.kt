package nilezia.app.foodorder.ui.signup

import android.util.Patterns

class SignUpPresenter(view: SignUpContact.View) : SignUpContact.Presenter {

    var mView = view

    override fun onOpenCamera() {
        mView.takePhoto()
    }

    override fun checkValidEdittext(email: String, name: String, password: String, confirmPass: String) {

        if (email.isValidEmail()) {
            if (name.isValidUserName()) {
                if (password.isValidPassword()) {
                    if (confirmPass.isValidConfirmPass() && password == confirmPass) {
                        mView.showSubmitDialog(email,name,password)
                    } else {
                        mView.showErrorConfirmPass("This password is not valid before password")
                    }
                } else {
                    mView.showErrorPassword("Please input password")
                }
            } else {
                mView.showErrorName("Please input username!!")
            }
        } else {
            mView.showErrorEmail("Not email format!! Please input email again!! ")
        }
    }



    private fun String.isValidEmail(): Boolean
            = this.isNotEmpty() &&
            Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun String.isValidUserName(): Boolean
            = this.isNotEmpty()
    private fun String.isValidPassword():Boolean
            = this.isNotEmpty()

    private fun String.isValidConfirmPass(): Boolean
            = this.isNotEmpty()

}