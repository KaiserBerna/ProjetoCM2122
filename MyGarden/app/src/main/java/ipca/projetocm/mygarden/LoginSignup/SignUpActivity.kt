package ipca.projetocm.mygarden.LoginSignup

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import ipca.projetocm.mygarden.DataModels.Utility.ProfileAndImageManaging
import ipca.projetocm.mygarden.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ipca.projetocm.mygarden.DataModels.PlantModels.Evergreen
import ipca.projetocm.mygarden.DataModels.PlantModels.Plants
import ipca.projetocm.mygarden.DataModels.Slot
import kotlinx.android.synthetic.main.login_activity.emailView
import kotlinx.android.synthetic.main.login_activity.passwordView
import kotlinx.android.synthetic.main.signup_activity.*
import kotlinx.android.synthetic.main.select_pic_layout.view.*
import java.util.ArrayList


// Documentation used as base for code
//https://firebase.google.com/docs/auth/android/password-auth


class SignUpActivity : AppCompatActivity() {

    private lateinit var refToAcc: FirebaseAuth
    private lateinit var refToDatabase: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        supportActionBar!!.hide()

        // Gets Firebase Auth Instance
        refToAcc = FirebaseAuth.getInstance()

        // Manages new account creation button click
        CreateAccountButtonView.setOnClickListener {

            // Method responsible for account creation
            newAccountCreation()
        }

        // Manages Image click
        newAccountImageView.setOnClickListener {

            // Method used for setting up confirmPasswordDialog for type of image choosing(camera or gallery)
            methodForImageChoosing()
        }
    }


    private fun newAccountCreation() {

        // Errors Management

        // Username Error
        if (usernameView.text.toString().isEmpty()) {
            usernameView.error = "Username Required"
            usernameView.requestFocus()
            return
        }

        // Email Erros
        if (emailView.text.toString().isEmpty()) {
            emailView.error = "Please Enter Email"
            emailView.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailView.text.toString()).matches()) {

            emailView.error = "Please Enter a Valid Email"
            emailView.requestFocus()
            return
        }

        // Password Errors
        if (passwordView.text.toString().isEmpty()) {
            passwordView.error = "Please Enter Password"
            passwordView.requestFocus()
            return
        }

        if (confirmPasswordView.text.toString() != passwordView.text.toString()) {
            confirmPasswordView.error = "Passwords do not Match"
            confirmPasswordView.requestFocus()
            return
        }





        // Creates user account in Firebase
        refToAcc.createUserWithEmailAndPassword(
            emailView.text.toString(),
            passwordView.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // If account is created successfully send verification email
                    refToAcc.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                // Saves selected image for profile picture and Updates Profile and finishes activity once is all wrapped up

                                ProfileAndImageManaging.imageStorageAndProfileUpdate(
                                    newAccountImageView.drawable.toBitmap(),
                                    usernameView.text.toString(),
                                    refToAcc,
                                    this, { finish() })



                                Toast.makeText(
                                    this, "Account Created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                } else {

                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, "Password too Short. Try Again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun methodForImageChoosing() {

        // Sets up image type choosing View
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_pic_layout, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        // Manages type click
        view.cameraId.setOnClickListener {
            pickImageFromCamera()
        }
        view.galleryId.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromCamera() {

        // Intent used for taking picture from camera to be used
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(this.packageManager!!)?.also {
                startActivityForResult(pictureIntent, 1)
            }
        }
    }

    private fun pickImageFromGallery() {

        // Intent used for picking gallery photo
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 2)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If photo is taken from camera successfully
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Gets bitmap image from camera
            var bitmapImage = data?.extras?.get("data") as Bitmap

            // Updates ImageView
            ProfileAndImageManaging.updateView(newAccountImageView, bitmapImage)
        }

        // If photo is selected from gallery
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            // Converts Uri to bitmap
            val inputStream = contentResolver.openInputStream(data!!.data!!)
            var bitmapImage = BitmapFactory.decodeStream(inputStream)

            // Updates ImageView
            ProfileAndImageManaging.updateView(newAccountImageView, bitmapImage)
        }
    }
}