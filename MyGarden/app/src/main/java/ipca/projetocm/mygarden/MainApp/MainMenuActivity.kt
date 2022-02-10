package ipca.projetocm.mygarden.MainApp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ipca.projetocm.mygarden.R
import com.google.firebase.auth.FirebaseAuth
import ipca.projetocm.mygarden.AccountSettingsAndInfo.AccountSettingsActivity
import ipca.projetocm.mygarden.DataModels.Utility.ProfileAndImageManaging
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.password_check.view.*


class MainMenuActivity : AppCompatActivity() {


    private lateinit var ref : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_menu)
        supportActionBar!!.hide()


        // Gets instance of current user for data display
        ref = FirebaseAuth.getInstance()

        // Sets up initial view with data from profile
        initialViewSetup()

        // Manages Edit profile button click
        manageAccountButtonView.setOnClickListener{

            confirmPasswordDialog()
        }

        // Manages the 2 main buttons for different options
        mainMenuButtonsManager()

    }

    private fun initialViewSetup(){

        // Updates avatar ImageView
        ProfileAndImageManaging.updateView(mainAvatarImageView,ref.currentUser!!.photoUrl!!,this)

        // Updates TextViews with profile info
        usernameTextView.text = ref.currentUser!!.displayName
        emailTextView.text = ref.currentUser!!.email
    }



    private fun mainMenuButtonsManager(){

        storyButtonView.setOnClickListener{

            var intent = Intent(this, AppInfoActivity::class.java)
            startActivity(intent)
        }

        manageGardenButtonView.setOnClickListener{

            var intent = Intent(this, ManageGardenActivity::class.java)
            startActivity(intent)

        }



    }

    private fun confirmPasswordDialog(){

        var dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        val dialogView = layoutInflater.inflate(R.layout.password_check,null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        dialog.show()

        dialogView.confirmPasswordButtonView.setOnClickListener{

            if (dialogView.dialogPasswordView.text.toString().isEmpty()){
                dialogView.dialogPasswordView.error = "Please Enter Password"
                dialogView.dialogPasswordView.requestFocus()
            }
            else
                ref.signInWithEmailAndPassword(ref.currentUser!!.email.toString(), dialogView.dialogPasswordView.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startActivityForResult(Intent(this, AccountSettingsActivity::class.java), 1)
                        }
                        else {
                            dialogView.dialogPasswordView.error = "Wrong Password"
                            dialogView.dialogPasswordView.requestFocus()
                        }
                    }
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Manages result from AccountSettingsActivity (updating views)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK  )
        {
            var updateInformationType : String = data?.getStringExtra("UpdateInformation")!!

            when (updateInformationType) {
                "UpdateProfile" ->{

                    // Updates Image View according to the new photo stored in profile
                    ProfileAndImageManaging.updateView(mainAvatarImageView, ref.currentUser!!.photoUrl!!, this)

                    // Updates main username view according to new username saved in profile in settings activity
                    usernameTextView.text = ref.currentUser!!.displayName
                }

                // Updates Email TextView according to new email stored in account
                "UpdateEmail" -> emailTextView.text = ref.currentUser!!.email


                // After account deletion send you back to login screen
                "DeleteAccount" -> finish()
            }

        }
    }
}