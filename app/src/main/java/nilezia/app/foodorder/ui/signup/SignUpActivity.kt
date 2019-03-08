package nilezia.app.foodorder.ui.signup

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_sign_up.*
import nilezia.app.foodorder.R


class SignUpActivity : AppCompatActivity() {
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    private val REQUEST_SELECT_CAMERA_BUTTON = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        clickAddImageProfile()


        submitSignUp.setOnClickListener( View.OnClickListener {

            val email = etSignUpEmail.text.toString()
            val name = etSignUpName.text.toString()
            val password = etSignUpPassWord.text.toString()
            val confirmPass = etSignUpConfirmPassword.text.toString()

         val builer = AlertDialog.Builder(this@SignUpActivity)
            builer.setTitle("Show Values of EditText")
            builer.setMessage("Email: "+ email +" Name: "+name +" Password: "+password)
            builer.setNegativeButton("Noted"){
                dialog, which ->
                dialog.dismiss()
            }
            val showValueEdittextDialog : AlertDialog = builer.create()
            showValueEdittextDialog.show()


        })


    }



    private fun clickAddImageProfile() {
        iv_ic_add.setOnClickListener {

            val builder = AlertDialog.Builder(this@SignUpActivity)
            val picItem = arrayOf("Take a photo", "Select image form gallery")
            builder.setTitle("Please select what do you want ??")
            builder.setItems(picItem) { dialog, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> selectPhotoFormGallery()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            val imageProfileDialog: AlertDialog = builder.create()
            imageProfileDialog.show()

        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager).also {
                startActivityForResult(intent,REQUEST_SELECT_CAMERA_BUTTON)
            } ?:
            Toast.makeText(this@SignUpActivity, "This device not have camera!!  "
                    , Toast.LENGTH_LONG)
                    .show()
        }
    }

    private fun selectPhotoFormGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type ="image/*"
            intent.resolveActivity(packageManager).also {
                startActivityForResult(intent,REQUEST_SELECT_IMAGE_IN_ALBUM)
            } ?:
            Toast.makeText(this@SignUpActivity, "This device not have gallery!!  "
                    , Toast.LENGTH_LONG)
                    .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            data?.let {
                val imageAlbum = data!!.data
                setImageByAlbum(imageAlbum)
            }?:
            setImageOfDefault()
            tv_show_profile.setImageResource(R.drawable.ic_add_image_profile2)

        }else if (requestCode == REQUEST_SELECT_CAMERA_BUTTON){
            data?.let{
                val imageCamera = data!!.extras!!.get("data")
                setImageOfCamera(imageCamera)
            } ?:
            setImageOfDefault()
            tv_show_profile.setImageResource(R.drawable.ic_add_image_profile2)

        }
    }

    private fun setImageOfDefault() {
        Glide.with(this@SignUpActivity)
                .clear(tv_show_profile)
    }
    private fun setImageOfCamera(imageCamera: Any?) {
        Glide.with(this@SignUpActivity)
                .load(imageCamera)
                .apply(RequestOptions.circleCropTransform())
                .into(tv_show_profile)
    }
    private fun setImageByAlbum(imageAlbum: Uri?) {
        Glide.with(this@SignUpActivity)
                .load(imageAlbum)
                .apply(RequestOptions.circleCropTransform())
                .into(tv_show_profile)
    }

}
