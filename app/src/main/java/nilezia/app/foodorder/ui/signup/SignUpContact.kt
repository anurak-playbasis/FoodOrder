package nilezia.app.foodorder.ui.signup

interface SignUpContact {
    interface View {
        fun showErrorEmail(text: String)
        fun showErrorName(text: String)
        fun showErrorPassword(text: String)
        fun showErrorConfirmPass(text: String)
        fun showSubmitDialog(email: String, name: String, password: String)
        fun takePhoto()
    }

    interface Presenter {
        fun checkValidEdittext(email: String, name: String, password: String, confirmPass: String)
        fun onOpenCamera()
    }
}