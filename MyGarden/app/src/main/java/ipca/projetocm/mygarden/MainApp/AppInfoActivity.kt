package ipca.projetocm.mygarden.MainApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ipca.projetocm.mygarden.R

class AppInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_info_activity)
        supportActionBar!!.hide()
    }
}